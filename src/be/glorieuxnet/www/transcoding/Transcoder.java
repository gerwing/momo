package be.glorieuxnet.www.transcoding;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * @author Gerwin Glorieux
 * This class was built to transcode audio on the fly in case a browser didn't support an Audio Codec
 * This class does not work appropriatly and a different solution should be found
 */
public class Transcoder {

	private IStreamCoder outCoder;
	private OutputStream stream;
	private HttpServletResponse response;
	
	/**
	 * Create a new Transcoder
	 * @param filepath path of the file to transcode
	 * @param aResponse Servlet response to which the file should be transcoded
	 * @throws IOException
	 */
	public Transcoder(String filepath, HttpServletResponse aResponse) throws IOException {
		response = aResponse;
		stream =  response.getOutputStream();
		
		//set response parameters
		response.setContentType("audio/ogg");
		File file = new File(filepath);
	    response.addHeader("Content-Disposition", "attachment; filename="
	    		+ file.getName().replaceAll(".m4a", ".ogg"));
	    response.setContentLength((int) file.length());
		
	    //initialize stream and coder
	    ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_VORBIS);
	    outCoder = IStreamCoder.make(Direction.ENCODING, codec);
		
		//decode the audio
		decodeAudio(filepath);
	}
	
	private void decodeAudio(String filepath) throws IOException {
		IContainer container = IContainer.make();
		
		//Open container
		if (container.open(filepath, IContainer.Type.READ, null) < 0)
		      throw new IllegalArgumentException("could not open file: " + filepath);
		
		int numStreams = container.getNumStreams();
		
		//Find audioStream
	    int audioStreamId = -1;
	    IStreamCoder audioCoder = null;
	    for(int i = 0; i < numStreams; i++)
	    {
	      IStream stream = container.getStream(i);
	      IStreamCoder coder = stream.getStreamCoder();
	      
	      if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
	      {
	        audioStreamId = i;
	        audioCoder = coder;
	        break;
	      }
	    }
	    if (audioStreamId == -1)
	      throw new RuntimeException("could not find audio stream in container: "+ filepath);
	    
	    //open decoder
	    if (audioCoder.open(null, null) < 0)
	        throw new RuntimeException("could not open audio decoder for container: "+ filepath);
	    
	    //open encoder
	    outCoder.setSampleRate(audioCoder.getSampleRate());
	    outCoder.setSampleFormat(Format.FMT_FLT);
	    outCoder.setChannels(audioCoder.getChannels());
	    int retVal = outCoder.open(null, null);
	    if (retVal < 0) {
	    	throw new IllegalArgumentException("could not open file: " + filepath);
	    }
	    
	    IBuffer buffer = IBuffer.make(null, 128000);
	    IPacket packet = IPacket.make(buffer);
	    IPacket outPacket = IPacket.make(buffer);
	    IAudioResampler resampler = IAudioResampler.make(2, audioCoder.getChannels(), 
	    		audioCoder.getSampleRate(), audioCoder.getSampleRate(),
	    		Format.FMT_FLT, audioCoder.getSampleFormat());
	    while(container.readNextPacket(packet) >= 0) {
	    	if(packet.getStreamIndex() == audioStreamId) {
	    		//is audio packet
	    		IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());
	    		IAudioSamples resamples = IAudioSamples.make(1024, 2);
	    		int offset = 0;
	    		while(offset < packet.getSize()) {
	    			int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
	    			if(bytesDecoded < 0)
	    				throw new RuntimeException("problem decoding audio");
	    			offset += bytesDecoded;
	    			if(samples.isComplete()){
	    				//resampler.resample(resamples, samples, samples.getNumSamples());
	    				int samplesConsumed = 0;
	    				while(samplesConsumed < samples.getNumSamples()){
	    					int bytesEncoded = outCoder.encodeAudio(outPacket, samples, samplesConsumed);
	    					if(bytesEncoded < 0)
	    						throw new RuntimeException("problem encoding audio");
	    					samplesConsumed += bytesEncoded;
	    					if(outPacket.isComplete()) {
	    						stream.write(outPacket.getData().getByteArray(0, outPacket.getSize()));
	    					}
	    				}
	    				
	    			}
	    		}
	    	}
	    	else {
	    		//drop packet
	    	}
	    }
	    stream.close();
	}
}
