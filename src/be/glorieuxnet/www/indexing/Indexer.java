package be.glorieuxnet.www.indexing;

import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import be.glorieuxnet.www.JSON.Song;
import be.glorieuxnet.www.dao.CollectionDAO;
import be.glorieuxnet.www.datalookup.MetadataLookup;

/**
 * @author Gerwin Glorieux
 * The Indexer class can be used to make a list of all the files in the Configured Folders, Tagg all these files automatically
 * and save them to the database.
 * Only new Files that are not in the database already will be scanned.
 * This class also provides methods to get and set the Configuration
 */
public class Indexer {

	/**
	 * @param args
	 */
	private static String CONFPATH = "resources" + File.separator + "conf.ser";
	private ArrayList<String> fileList; //final list with all audio files
	
	/*
	 * Test Variables
	 */
	private int count = 0;
	private int count2 = 0;
	private int count3 = 0;
	private int count4 = 0;
	
	/*
	 * Database Access
	 */
	private CollectionDAO collection;
	
	/**
	 * Construct a new Indexer class
	 */
	public Indexer() {
		fileList = new ArrayList<String>();
		collection = new CollectionDAO();
	}
	
	/**
	 * Invoking this method will cause this class to automatically scan the configured folders for files
	 * and tag them if needed/possible
	 */
	public void indexAll() {
		for(Folder s:getConfiguration().getFolders())//then index all files in those folders
		{
			indexFiles(s.getFolder()); 
		}
		System.out.println(fileList.size());
		//after indexing all the files, try tagging them automatically
		tagAll();
		//delete files from database that are no longer in the configured folders
		deleteRemovedFilesFromDB();
	}
	
	private void indexFiles(String aFile) {
		try {
			File file = new File(aFile);
			if(file.isDirectory()) // if File is directory, loop all files in directory
			{
				String[] files = file.list();
				for(String s : files)
				{
					indexFiles(file.getAbsolutePath() + File.separator + s);
				}
			}
			else if(isAudioFile(file)) //check if file is audioFile
			{
				fileList.add(file.getAbsolutePath());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private void tagAll() {
		AudioFile file;
		Tag tag;
		for(String s:fileList) {
			try {
				file = AudioFileIO.read(new File(s));
				//If file is already in database do nothin to avoid extra datalookups
				//Only new files get scanned and added
				if(collection.selectSongByFilepath(file.getFile().getAbsolutePath())==-1) {
					tag = file.getTagOrCreateDefault();
					int checker = file.getCompleteLevel();
					if (checker==AudioFile.ALL_DATA_AND_COVER) {
						count++;
						//SAVE COVER
						//check file type
						if(!file.hasExistingCover()) {
							//save
							saveCoverToDisk(tag.getFirstArtwork().getImage(), file);
						}
						//SAVE TO DB
						collection.insertSong(file);
					}
					else if (checker==AudioFile.ALL_DATA_NO_COVER) {
						//GET COVER
						//check if cover has already been saved
						if(!file.hasExistingCover()) {
							BufferedImage image = MetadataLookup.getCoverArtFromLastFM(file);
							if(image!=null) {
								saveCoverToDisk(image, file);
								count++;
							}
							else {
								image = MetadataLookup.getCoverArtFromDiscogs(file);
								if(image!=null) {
									saveCoverToDisk(image, file);
									count++;
								}
								else count2++;
							}
						}
						else count++;
						//SAVE TO DB
						collection.insertSong(file);
					}
					else if (checker == AudioFile.NO_DATA_AND_COVER) {
						//GET DATA
						//check if at least title is not empty, else set title to file name
						if(!AudioFile.isEmpty(tag.getFirst(FieldKey.TITLE))) {
							MetadataLookup.getMetadataFromMusicBrainz(file);
						}
						else {
							tag.setField(FieldKey.TITLE, file.getFile().getName());
						}
						if(file.getCompleteLevel() == AudioFile.NO_DATA_AND_COVER)
						{
							count3++;
						}
						else if(file.getCompleteLevel() == AudioFile.ALL_DATA_AND_COVER){
							count++;
						}
						//SAVE COVER
						//check file type
						if(!file.hasExistingCover()) {
							saveCoverToDisk(tag.getFirstArtwork().getImage(), file);
						}
						//SAVE TO DB
						collection.insertSong(file);
					}
					else if (checker == AudioFile.NO_DATA_NO_COVER) {
						//GET DATA
						//check if at least title is not empty, else set title to file name
						if(!AudioFile.isEmpty(tag.getFirst(FieldKey.TITLE))) {
							MetadataLookup.getMetadataFromMusicBrainz(file);
						}
						else {
							tag.setField(FieldKey.TITLE, file.getFile().getName());
						}
						//GET COVER
						if(!file.hasExistingCover() && !AudioFile.isEmpty(tag.getFirst(FieldKey.ALBUM)) 
								&& !AudioFile.isEmpty(file.getAlbumArtist())) {
							BufferedImage image = MetadataLookup.getCoverArtFromLastFM(file);
							if(image!=null) {
								saveCoverToDisk(image, file);
								if(file.getCompleteLevel() == AudioFile.ALL_DATA_AND_COVER){
									count++;
								}
								else count3++;
							}
							else {
								image = MetadataLookup.getCoverArtFromDiscogs(file);
								if(image!=null) {
									saveCoverToDisk(image, file);
									if(file.getCompleteLevel() == AudioFile.ALL_DATA_AND_COVER){
										count++;
									}
									else count3++;
								}
								else if(file.getCompleteLevel() == AudioFile.ALL_DATA_NO_COVER) {
									count2++;
								}
								else count4++;
							}
						}
						else if(file.getCompleteLevel() == AudioFile.ALL_DATA_AND_COVER){
							count++;
						}
						else count3++;
						//SAVE TO DB
						collection.insertSong(file);
					}
				}
			} catch (CannotReadException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TagException e) {
				e.printStackTrace();
			} catch (ReadOnlyFileException e) {
				e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(count);
		System.out.println(count2);
		System.out.println(count3);
		System.out.println(count4);
	}
	
	private void deleteRemovedFilesFromDB() {
		ArrayList<Song> songs = collection.selectAllSongFilepaths();
		//More songs in database
		if(songs.size() > fileList.size()){
			//check which songs are still in the folders, if not in folders then delete
			for(Song s:songs){
				if(fileList.indexOf(s.getRequestPath())==-1) 
					collection.deleteSongByID(Integer.parseInt(s.getId()));
			}
		}
	}
	
	private void saveCoverToDisk(BufferedImage cover, AudioFile file) {
		try {
			ImageIO.write(cover, file.getCoverImageType(), new File(file.getCoverPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the currently save Configuration. If no Configuration has been saved it will return a new
	 * Configuration object
	 * @return Configuration object
	 */
	public static Configuration getConfiguration() {
		File conffile = new File(CONFPATH);
		if(conffile.exists()) //load file
		{
			try {
				ObjectInputStream input = new ObjectInputStream(new FileInputStream(conffile));
				Configuration configuration;
				configuration = (Configuration) input.readObject();
				input.close();
				return configuration;
			}
			catch (IOException ie) {
				System.err.println(ie.getMessage());
				removeConfiguration();
				return null;
			} 
			catch (ClassNotFoundException e) {
				System.err.println(e.getMessage());
				removeConfiguration();
				return null;
			}
		}
		else {
			Configuration c = new Configuration();
			setConfiguration(c);
			return c;
		}
	}
	
	/**
	 * This method will remove the current configuration
	 */
	public static void removeConfiguration () {
		setConfiguration(new Configuration());
	}
	
	/**
	 * Set the Configuration and save it to disk
	 * @param configuration New Configuration
	 */
	public static void setConfiguration(Configuration configuration) {
		File conffile = new File(CONFPATH);
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(conffile));	
			output.writeObject(configuration);
			output.close();
		}
		catch (IOException ie) {
			System.err.println(ie.getMessage());
		}
	}

	private boolean isAudioFile(File f) {
		int length = f.getAbsolutePath().length()-5;
		return f.isFile()&&(f.getAbsolutePath().lastIndexOf(".mp3")>length||
				f.getAbsolutePath().lastIndexOf(".ogg")>length||
				f.getAbsolutePath().lastIndexOf(".m4a")>length||
				f.getAbsolutePath().lastIndexOf(".mp4")>length);
	}
}
