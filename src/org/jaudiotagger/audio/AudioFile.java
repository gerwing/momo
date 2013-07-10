/*
 * Entagged Audio Tag library 
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;
import java.util.ArrayList;

import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.asf.AsfTag;
import org.jaudiotagger.audio.wav.WavTag;
import org.jaudiotagger.audio.real.RealTag;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;

/**
 * <p>This is the main object manipulated by the user representing an audiofile, its properties and its tag.</p>
 * <p>The prefered way to obtain an <code>AudioFile</code> is to use the <code>AudioFileIO.read(File)</code> method.</p>
 * <p>The <code>AudioFile</code> contains every properties associated with the file itself (no meta-data), like the bitrate, the sampling rate, the encoding audioHeaders, etc.</p>
 * <p>To get the meta-data contained in this file you have to get the <code>Tag</code> of this <code>AudioFile</code></p>
 *
 * @author Raphael Slinckx
 * @version $Id$
 * @see AudioFileIO
 * @see Tag
 * @since v0.01
 * 
 * This Class has been extended by Gerwin Glorieux to be used in this Software
 */
public class AudioFile
{
    //Logger
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio");

    /**
     * The physical file that this instance represents.
     */
    protected File file;

    /**
     * The Audio header info
     */
    protected AudioHeader audioHeader;

    /**
     * The tag
     */
    protected Tag tag;
    
    /**
	 * Completeness level of audiofiles
	 */
	public static final int ALL_DATA_AND_COVER = 1; //all data is present and cover art as well
	public static final int ALL_DATA_NO_COVER = 2; //all data is present but no cover art
	public static final int NO_DATA_AND_COVER = 3; //no data but a cover is present
	public static final int NO_DATA_NO_COVER = 4; //data is missing and no cover
	/*
	 * Coverpath parameters
	 */
	public static final String COVERPATH = "web" + File.separator + "resources" + File.separator + "covers" + File.separator; //path of cover on file system
	public static final String WEBCOVERPATH = "resources/covers/"; // path of cover on web server
	private String COVERTYPE = ".jpg";//standard as jpg
	private String IMAGETYPE = "jpg";
	public static final String PNG = ".png"; //PNG type 
	public static final String JPG = ".jpg"; //JPG type
	public static final String NO_COVER_PATH = "resources/covers/no_cover.jpg"; //Path of the no cover image
	
    public AudioFile()
    {

    }

    /**
     * <p>These constructors are used by the different readers, users should not use them, but use the <code>AudioFileIO.read(File)</code> method instead !.</p>
     * <p>Create the AudioFile representing file f, the encoding audio headers and containing the tag</p>
     *
     * @param f           The file of the audio file
     * @param audioHeader the encoding audioHeaders over this file
     * @param tag         the tag contained in this file or null if no tag exists
     */
    public AudioFile(File f, AudioHeader audioHeader, Tag tag)
    {
        this.file = f;
        this.audioHeader = audioHeader;
        this.tag = tag;
    }


    /**
     * <p>These constructors are used by the different readers, users should not use them, but use the <code>AudioFileIO.read(File)</code> method instead !.</p>
     * <p>Create the AudioFile representing file denoted by pathnames, the encoding audio Headers and containing the tag</p>
     *
     * @param s           The pathname of the audio file
     * @param audioHeader the encoding audioHeaders over this file
     * @param tag         the tag contained in this file
     */
    public AudioFile(String s, AudioHeader audioHeader, Tag tag)
    {
        this.file = new File(s);
        this.audioHeader = audioHeader;
        this.tag = tag;
    }

    /**
     * <p>Write the tag contained in this AudioFile in the actual file on the disk, this is the same as calling the <code>AudioFileIO.write(this)</code> method.</p>
     *
     * @throws CannotWriteException If the file could not be written/accessed, the extension wasn't recognized, or other IO error occured.
     * @see AudioFileIO
     */
    public void commit() throws CannotWriteException
    {
        AudioFileIO.write(this);
    }

    /**
     * Set the file to store the info in
     *
     * @param file
     */
    public void setFile(File file)
    {
        this.file = file;
    }

    /**
     * Retrieve the physical file
     *
     * @return
     */
    public File getFile()
    {
        return file;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    /**
     * Return audio header
     * @return
     */
    public AudioHeader getAudioHeader()
    {
        return audioHeader;
    }

    /**
     * <p>Returns the tag contained in this AudioFile, the <code>Tag</code> contains any useful meta-data, like
     * artist, album, title, etc. If the file does not contain any tag the null is returned. Some audio formats do
     * not allow there to be no tag so in this case the reader would return an empty tag whereas for others such
     * as mp3 it is purely optional.
     *
     * @return Returns the tag contained in this AudioFile, or null if no tag exists.
     */
    public Tag getTag()
    {
        return tag;
    }

    /**
     * <p>Returns a multi-line string with the file path, the encoding audioHeader, and the tag contents.</p>
     *
     * @return A multi-line string with the file path, the encoding audioHeader, and the tag contents.
     *         TODO Maybe this can be changed ?
     */
    @Override
	public String toString()
    {
        return "AudioFile " + getFile().getAbsolutePath()
                + "  --------\n" + audioHeader.toString() + "\n" + ((tag == null) ? "" : tag.toString()) + "\n-------------------";
    }

    /**
     * Check does file exist
     *
     * @param file
     * @throws FileNotFoundException
     */
    public void checkFileExists(File file)throws FileNotFoundException
    {
        logger.info("Reading file:" + "path" + file.getPath() + ":abs:" + file.getAbsolutePath());
        if (!file.exists())
        {
            logger.severe("Unable to find:" + file.getPath());
            throw new FileNotFoundException(ErrorMessage.UNABLE_TO_FIND_FILE.getMsg(file.getPath()));
        }
    }

    /**
     * Checks the file is accessible with the correct permissions, otherwise exception occurs
     *
     * @param file
     * @param readOnly
     * @throws ReadOnlyFileException
     * @throws FileNotFoundException
     * @return
     */
    protected RandomAccessFile checkFilePermissions(File file, boolean readOnly) throws ReadOnlyFileException, FileNotFoundException
    {
        RandomAccessFile newFile;

        checkFileExists(file);

        // Unless opened as readonly the file must be writable
        if (readOnly)
        {
            newFile = new RandomAccessFile(file, "r");
        }
        else
        {
            if (!file.canWrite())
            {
                logger.severe("Unable to write:" + file.getPath());
                throw new ReadOnlyFileException(ErrorMessage.NO_PERMISSIONS_TO_WRITE_TO_FILE.getMsg(file.getPath()));
            }
            newFile = new RandomAccessFile(file, "rws");
        }
        return newFile;
    }

    /**
     * Optional debugging method
     *
     * @return
     */
    public String displayStructureAsXML()
    {
        return "";
    }

    /**
     * Optional debugging method
     *
     * @return
     */
    public String displayStructureAsPlainText()
    {
        return "";
    }


    /** Create Default Tag
     *
     * @return
     */
    //TODO might be better to instantiate classes such as Mp4File,FlacFile ecetera
    //TODO Generic tag is very misleading because soem of these formats cannot actually save the tag
    public Tag createDefaultTag()
    {
        if(SupportedFileFormat.FLAC.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new FlacTag(VorbisCommentTag.createNewTag(), new ArrayList< MetadataBlockDataPicture >());
        }
        else if(SupportedFileFormat.OGG.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return VorbisCommentTag.createNewTag();
        }
        else if(SupportedFileFormat.MP4.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.M4A.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.M4P.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.WMA.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new AsfTag();
        }
        else if(SupportedFileFormat.WAV.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new WavTag();
        }
        else if(SupportedFileFormat.RA.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new RealTag();
        }
        else if(SupportedFileFormat.RM.getFilesuffix().equals(file.getName().substring(file.getName().lastIndexOf('.'))))
        {
            return new RealTag();
        }
        else
        {
            throw new RuntimeException("Unable to create default tag for this file format");
        }

    }

    /**
     * Get the tag or if the file doesn't have one at all, create a default tag  and return
     *
     * @return
     */
    public Tag getTagOrCreateDefault()
    {
        Tag tag = getTag();
        if(tag==null)
        {
            return createDefaultTag();
        }
        return tag;
    }

     /**
     * Get the tag or if the file doesn't have one at all, create a default tag  and set it
     *
     * @return
     */
    public Tag getTagOrCreateAndSetDefault()
    {
        Tag tag = getTag();
        if(tag==null)
        {
            tag = createDefaultTag();
            setTag(tag);
            return tag;
        }
        return tag;
    }

    /**
     *
     * @param file
     * @return filename with audioFormat separator stripped of.
     */
    public static String getBaseFilename(File file)
    {
        int index=file.getName().toLowerCase().lastIndexOf(".");
        if(index>0)
        {
            return file.getName().substring(0,index);
        }
        return file.getName();
    }
    
    /**
     * Written by Gerwin Glorieux
     * Check how complete the metadata for this audiofile is
     * @return level of completeness
     */
    public int getCompleteLevel() {
		try {
			if (isEmpty(tag.getFirst(FieldKey.ARTIST))||isEmpty(tag.getFirst(FieldKey.TITLE))||
				isEmpty(tag.getFirst(FieldKey.TRACK))||isEmpty(tag.getFirst(FieldKey.ALBUM))||
				isEmpty(tag.getFirst(FieldKey.TRACK_TOTAL))) 
			{
				if(isEmpty(tag.getFirst(FieldKey.COVER_ART))) 
					return NO_DATA_NO_COVER;
				else return NO_DATA_AND_COVER;
			}
			else if (isEmpty(tag.getFirst(FieldKey.COVER_ART))) 
				return ALL_DATA_NO_COVER;
			else return ALL_DATA_AND_COVER;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return NO_DATA_NO_COVER;
		}
	}
    
    /**
     * Written by Gerwin Glorieux
     * This method returns the album artist by checking if the Album_Artist tag is filled in,
     * otherwise it returns the Artist field
     * @return String containing album artist
     */
    public String getAlbumArtist() {
    	if(tag.getFirst(FieldKey.IS_COMPILATION).equals("1")) {
    		return "Various Artists";
    	}
    	else return (isEmpty(tag.getFirst(FieldKey.ALBUM_ARTIST))) 
				? tag.getFirst(FieldKey.ARTIST) : tag.getFirst(FieldKey.ALBUM_ARTIST);
    }
    
    /**
     * Written by Gerwin Glorieux
     * Returns the artist of an Audiofile not taking compilation information in to account
     * @return Artist
     */
    public String getAlbumArtistNoComp() {
    	return (isEmpty(tag.getFirst(FieldKey.ALBUM_ARTIST))) 
				? tag.getFirst(FieldKey.ARTIST) : tag.getFirst(FieldKey.ALBUM_ARTIST);
    }
    
    /**
     * Written by Gerwin Glorieux
     * Get the album artist without taking compilation information into account. Only return part of the artist name.
     * @return Part of the artist name
     */
    public String getAlbumArtistNoCompPart() {
    	String artist = (isEmpty(tag.getFirst(FieldKey.ALBUM_ARTIST))) 
				? tag.getFirst(FieldKey.ARTIST) : tag.getFirst(FieldKey.ALBUM_ARTIST);
		return artist.substring(0,artist.length()-3);
    }
    
    /**
     * Written By Gerwin Glorieux
     * Get the Album title without any brackets
     * @return Album title
     */
    public String getAlbumTitleNoBrackets() {
    	String album = tag.getFirst(FieldKey.ALBUM);
    	int start = album.indexOf('[');
    	int end = album.lastIndexOf(']');
    	if(start>-1&&end>start) {
    		String brackets = album.substring(start, end+1);
    		return album.replace(brackets, "");
    	}
    	else return album;
    }
    
    /**
     * Written by Gerwin Glorieux
     * This method returns the coverpath for this audiofile based on the Album title, Artist name and Disc no
     * @return Coverpath string
     */
    public String getCoverPath () {
		String artist = getAlbumArtist();
		String discno = (isEmpty(tag.getFirst(FieldKey.DISC_NO))) ? "1" : tag.getFirst(FieldKey.DISC_NO);
		String album = tag.getFirst(FieldKey.ALBUM);
		return getCoverPathFromVariables(COVERPATH, artist, discno, album, COVERTYPE);
	}
    
    /**
     * Written by Gerwin Glorieux
     * Returns the path of the cover on the web server
     * @return Path of the audio files' cover on the webserver
     */
    public String getWebCoverPath() {
    	String artist = getAlbumArtist();
		String discno = (isEmpty(tag.getFirst(FieldKey.DISC_NO))) ? "1" : tag.getFirst(FieldKey.DISC_NO);
		String album = tag.getFirst(FieldKey.ALBUM);
		return getCoverPathFromVariables(WEBCOVERPATH, artist, discno, album, COVERTYPE);
    }
    
    /**
     * Written by Gerwin Glorieux
     * Generates a coverpath for a cover and removes all unwanted characters
     * @param basetype Web or normal coverpath
     * @param artist Artist
     * @param discno Disc Number
     * @param album Album Title
     * @return String with all non valid filepath characters removed
     */
    public String getCoverPathFromVariables(String basetype, String artist, String discno, String album, String coverType){
    	String path = artist + album + discno;
		path = path.replaceAll("[^\\p{L}\\p{N}]", "");
		return basetype + path + coverType;
    }
    
    /**
     * Written by Gerwin Glorieux
     * Checks wether a string is empty or not
     * @param s String to be checked
     * @return true if String is empty, false if String is not empty
     */
    public static boolean isEmpty(String s) {
		if(s.equals("")||s.equals("null")||s==null)
			return true;
		else return false;
	}
    
    /**
     * Written by Gerwin Glorieux
     * Sets the cover type for this AudioFile
     * @param s Cover type from Audiofile class
     */
    public void setCoverType(String s) {
    	COVERTYPE = s;
    	IMAGETYPE = s.substring(1, 4);
    }
    
    /**
     * Written by Gerwin Glorieux
     * Return the image type of for this AudioFile (without the dot)
     * @return png or jpg
     */
    public String getCoverImageType() {
    	return IMAGETYPE;
    }
    
    /**
     * Written by Gerwin Glorieux
     * Check whether this Audio file has a cover embedded or saved to disk. Return true if a cover exists.
     * @return True if cover exists
     */
    public boolean hasExistingCover() {
    	File cover;
    	//IF File has cover built in
    	if(!isEmpty(tag.getFirst(FieldKey.COVER_ART))) {
    		if(tag.getFirstArtwork().getMimeType().toLowerCase().equals(ImageFormats.MIME_TYPE_PNG)) {
				setCoverType(AudioFile.PNG);
			}
    		else setCoverType(AudioFile.JPG);
    		cover = new File(getCoverPath());
    		if(cover.exists()) return true;
    		else return false;
    	}
    	//If file doesnt have cover built in, but might have gotten it from metadata lookups
    	else {
	    	cover = new File(getCoverPath());
	    	if(cover.exists()) {
	    		return true;
	    	}
	    	if(COVERTYPE.equals(AudioFile.JPG)) {
	    		cover = new File(getCoverPath().replaceAll(".jpg",".png"));
	    		setCoverType(PNG);
	    	}
	    	else {
	    		cover = new File(getCoverPath().replaceAll(".png",".jpg"));
	    		setCoverType(JPG);
	    	}
	    	if(cover.exists()) {
	    		return true;
	    	}
	    	else return false;
    	}
    }
}
