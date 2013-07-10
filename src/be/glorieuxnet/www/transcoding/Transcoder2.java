package be.glorieuxnet.www.transcoding;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Gerwin Glorieux
 * Backup transcoder class. Not used in the Software
 */
public class Transcoder2 {
	private HttpServletResponse response;
	private ServletOutputStream stream;

	public Transcoder2(String filepath, HttpServletResponse aResponse) throws IOException {
		response = aResponse;
		stream =  response.getOutputStream();
		
		//set response parameters
		response.setContentType("audio/ogg");
		File file = new File(filepath);
	    response.addHeader("Content-Disposition", "attachment; filename="
	    		+ file.getName().replaceAll(".m4a", ".ogg"));
	    response.setContentLength((int) file.length());
		
	    File source = new File(filepath);
	    File target = new File("target.ogg");
	    AudioAttributes audio = new AudioAttributes();
	    audio.setCodec("libvorbis");
	    audio.setBitRate(new Integer(128000));
	    audio.setChannels(new Integer(2));
	    audio.setSamplingRate(new Integer(44100));
	    EncodingAttributes attrs = new EncodingAttributes();
	    attrs.setFormat("ogg");
	    attrs.setAudioAttributes(audio);
	    Encoder encoder = new Encoder();
	    try {
			encoder.encode(source, target, attrs);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
