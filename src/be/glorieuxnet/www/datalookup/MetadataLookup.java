package be.glorieuxnet.www.datalookup;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.musicbrainz.controller.Recording;
import org.musicbrainz.model.searchresult.RecordingResultWs2;

import be.glorieuxnet.www.datalookup.Discogs.DiscogsRelease;
import be.glorieuxnet.www.datalookup.Discogs.SearchResult;
import be.glorieuxnet.www.datalookup.MusicBrainz.CoverArt;

import com.google.gson.Gson;

import de.umass.lastfm.Album;

/**
 * @author Gerwin Glorieux
 * The MetadataLookup class contains static methods that can be used to automatically annotate AudioFile objects
 * or find Album covers given an AudioFile object
 */
public class MetadataLookup {
	
	private static final String lastfmAPI = "fbdb30d89333689b2fdc7ee87be6a5e6";
	private static List<String> lfmCoverartCache = new ArrayList<String>();
	private static List<String> dcCoverartCache = new ArrayList<String>();
	private static List<String> mbCoverartCache = new ArrayList<String>();

	/**
	 * Use this method to retrieve coverart for a given AudioFile object from the Last.fm Web Service
	 * @param file JAudiotagger library AudioFile object
	 * @return BufferedImage object containing the retrieved image. If no image was retrieved this will be null
	 */
	public static BufferedImage getCoverArtFromLastFM(AudioFile file) {
		BufferedImage image = null;
		//check cache
		if(!lfmCoverartCache.contains(getSignature(file))) {
			try {
				lfmCoverartCache.add(getSignature(file));
				//Get from last.fm
				Album album = Album.getInfo(
						file.getAlbumArtistNoComp(), file.getTag().getFirst(FieldKey.ALBUM), lastfmAPI, "1");
				String link = "";
				//Get best image url
				if(album!=null) {
					link = album.getBestImageUrl();
				}
				//check if url is not empty and return image
				if(!AudioFile.isEmpty(link)) {
					URL url = new URL(link);
					image = ImageIO.read(url);
					//check type
					if(link.toLowerCase().indexOf(".jpg")<0&&link.toLowerCase().indexOf("jpeg")<0) {
						file.setCoverType(AudioFile.PNG);
					}
					else file.setCoverType(AudioFile.JPG);
				}
			} catch(IOException io) {
				io.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return image;
	}
	
	/**
	 * Use this method to retrieve coverart for a given AudioFile object from the Discogs Web Service 
	 * @param file JAudiotagger library AudioFile object
	 * @return BufferedImage object containing the retrieved image. If no image was retrieved this will be null
	 */
	public static BufferedImage getCoverArtFromDiscogs(AudioFile file) {
		BufferedImage image = null;
		String url = "http://api.discogs.com/database/search?q=" +
				file.getAlbumTitleNoBrackets() + "&artist=" + file.getAlbumArtist();
		//Check cache first
		if(!dcCoverartCache.contains(getSignature(file))) {
			try {
				dcCoverartCache.add(getSignature(file));
				url = url.replaceAll(" ", "%20");
				InputStreamReader reader = new InputStreamReader(new URL(url).openStream());
				SearchResult result = new Gson().fromJson(reader, SearchResult.class);
				//check if something got returned
				if(result.getResults().size()>0){
					String id = result.getResults().get(0).getId();
					url = "http://api.discogs.com/releases/" + id;
					reader = new InputStreamReader(new URL(url).openStream());
					DiscogsRelease release = new Gson().fromJson(reader, DiscogsRelease.class);
					if(release.getImages()!=null) {
 						String imageUrl = release.getImages().get(0).getResource_url();
						if(!AudioFile.isEmpty(imageUrl)) {
							image = ImageIO.read(new URL(imageUrl));
							//Check file type
							if(imageUrl.toLowerCase().indexOf(".jpg")<0&&imageUrl.toLowerCase().indexOf("jpeg")<0) {
								file.setCoverType(AudioFile.PNG);
							}
							else file.setCoverType(AudioFile.JPG);
						}
					}
				}
			} catch (IOException io) {
				io.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return image;
	}
	
	/**
	 * Use this method to retrieve coverart for a given AudioFile object from the Musicbrainz Web Service 
	 * @param file JAudiotagger library AudioFile object
	 * @return BufferedImage object containing the retrieved image. If no image was retrieved this will be null
	 */
	public static BufferedImage getCoverArtFromMusicBrainz(AudioFile file) {
		BufferedImage image = null;
		//Check wether the file has a musicbrainz ID
		if(!AudioFile.isEmpty(file.getTag().getFirst(FieldKey.MUSICBRAINZ_RELEASEID))) {
			String url = "http://coverartarchive.org/release/" + 
					file.getTag().getFirst(FieldKey.MUSICBRAINZ_RELEASEID);
			if(!mbCoverartCache.contains(getSignature(file))) {
				try {
					mbCoverartCache.add(getSignature(file));
					InputStreamReader reader = new InputStreamReader(new URL(url).openStream());
					CoverArt cover = new Gson().fromJson(reader, CoverArt.class);
					//get non empty link
					String link = (AudioFile.isEmpty(cover.getImages().get(0).getThumbnails().getLarge())) ?
							cover.getImages().get(0).getThumbnails().getSmall() : 
								cover.getImages().get(0).getThumbnails().getLarge();
					//check if url is not empty and return image
					if(!AudioFile.isEmpty(link)) {
						URL urlImg = new URL(link);
						image = ImageIO.read(urlImg);
						//check type
						if(link.toLowerCase().indexOf(".jpg")<0&&link.toLowerCase().indexOf("jpeg")<0) {
							file.setCoverType(AudioFile.PNG);
						}
						else file.setCoverType(AudioFile.JPG);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return image;
	}
	
	/**
	 * Use this method to try and automatically update a songs metadata. This method takes an AudioFile object and will set
	 * any metadata that is found about the file in the AudioFile object.
	 * @param file JAudiotagger library AudioFile object
	 */
	public static void getMetadataFromMusicBrainz(AudioFile file) {
		try {
			//Prepare Search
			Recording record = new Recording();
			record.getSearchFilter().setMinScore(new Long(100));
			//Determine query
			String title = file.getTag().getFirst(FieldKey.TITLE);
			String album = file.getTag().getFirst(FieldKey.ALBUM);
			String trackno = file.getTag().getFirst(FieldKey.TRACK);
			boolean compilation = 
					(file.getTag().getFirst(FieldKey.IS_COMPILATION).equals("1")) ? true : false;
			if(file.getAlbumArtist().toLowerCase().indexOf("various")>=0) compilation = true;
			String artist; //depends on wether album is a compilation
			if(compilation) {
				artist = "Various Artists";
			}
			else {
				artist = file.getAlbumArtistNoComp();
			}
			//ONLY CONTINUE IF AT LEAST TITLE AND ALBUM PRESENT
			if(!AudioFile.isEmpty(title)&&!AudioFile.isEmpty(album)) {
				//Build query string
				String query = "";
				if(!AudioFile.isEmpty(title)) {
					query += "\"" + title + "\"";
				}
				if(!AudioFile.isEmpty(artist)) {
					query += " AND artist:\"" + artist + "\"";
				}
				if(!AudioFile.isEmpty(album)) {
					query += " AND release:\"" + album + "\"";
				}
				if(compilation) {
					query += " AND secondarytype:compilation";
				}
				if(!AudioFile.isEmpty(trackno)) {
					query += " AND tnum:" + trackno;
				}
				//Search for record
				record.search(query);
				List<RecordingResultWs2> results = record.getFirstSearchResultPage();
				//Update results
				if(results.size()!=0) {
					RecordingResultWs2 result = results.get(0);
					//check if still compilation after search
					boolean stillComp = result.getRecording().getReleases().get(0).getReleaseGroup().getType().toLowerCase().indexOf("compilation")>=0;
					//only continue if the result is still a compilation or they were never a compilation
					if((compilation&&stillComp)||(!compilation&&!stillComp)) {
						artist = result.getRecording().getArtistCredit().getNameCredits().get(0).getArtistName();
						album = result.getRecording().getReleases().get(0).getTitle();
						title = result.getRecording().getTitle();
						String year = result.getRecording().getReleases().get(0).getYear();
						String notracks = Integer.toString(
								result.getRecording().getReleases().get(0).getTracksCount());
						trackno = Long.toString(
								result.getRecording().getReleases().get(0).getMediumList().getMedia().get(0).getTrackList().getOffset()+1);
						String discno = Integer.toString(result.getRecording().getReleases().get(0).getMediumList().getMedia().get(0).getPosition());
						try {
							if(stillComp) {
								file.getTag().setField(FieldKey.ALBUM_ARTIST, "Various Artists");
								file.getTag().setField(FieldKey.IS_COMPILATION, "1");
							}
							else {
								file.getTag().setField(FieldKey.IS_COMPILATION, "0");
							}
							file.getTag().setField(FieldKey.ARTIST, artist);
							file.getTag().setField(FieldKey.ALBUM, album);
							file.getTag().setField(FieldKey.TITLE, title);
							file.getTag().setField(FieldKey.YEAR, year);
							file.getTag().setField(FieldKey.TRACK_TOTAL, notracks);
							file.getTag().setField(FieldKey.TRACK, trackno);
							file.getTag().setField(FieldKey.DISC_NO, discno);
							file.getTag().setField(FieldKey.MUSICBRAINZ_RELEASEID, 
									result.getRecording().getReleases().get(0).getId());
							
						} catch (KeyNotFoundException e) {
							e.printStackTrace();
						} catch (FieldDataInvalidException e) {
							e.printStackTrace();
						}
					}
					
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getSignature(AudioFile file){
		return file.getAlbumArtist()+file.getTag().getFirst(FieldKey.ALBUM)+file.getTag().getFirst(FieldKey.DISC_NO);
	}
	
}
