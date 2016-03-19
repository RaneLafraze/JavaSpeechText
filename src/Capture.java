import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


public class Capture implements Runnable {
	
	Thread thread;
	TargetDataLine line;
	
	AudioInputStream audioInput;
	AudioFormat audioFormat;
	
	
	public Capture() {
				
	}
	
	/**
	 * Starts the thread.
	 */
	public void start() {
		thread = new Thread(this);
		thread.setName("Capture");
		thread.start();
	}
	
	/**
	 * Stops the thread.
	 */
	public void stop() {
		thread = null;
	}
	
	/**
	 * Stops the thread from capturing audio and prints the given error message.
	 * 
	 * @param message
	 * a string containing the error message
	 */
	private void endCapture(String message) {
		stop();
		System.err.println(message);
	}
	
	public void run() {
		
		double duration = 0.0;
		audioInput = null;
		
		// Make sure a compatible line is supported for the audio input
		
		AudioFormat format = getAudioFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		
		if(!AudioSystem.isLineSupported(info)) {
			endCapture("The current line " + info + " is not supported.");
			return;
		}
		
		// Get and open the data line to capture the audio
		
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, line.getBufferSize());
		} catch(LineUnavailableException lu) {
			endCapture("The line could not be loaded. Error: " + lu);
			return;
		} catch(SecurityException s) {
			endCapture(s.toString());
			return;
		} catch(Exception ex) {
			endCapture(ex.toString());
			return;
		}
		
		// Set up variables for the audio
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		int frameSizeInBytes = format.getFrameSize();
		int bufferLengthInFrames = line.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] audioData = new byte[bufferLengthInBytes];
		int bytesRead; // Number of bytes read
		
		line.start();
		
		while(thread != null) {
			if((bytesRead = line.read(audioData, 0, bufferLengthInBytes)) == -1) {
				break;
			}
			output.write(audioData, 0, bytesRead);
		}
		
		System.out.print("D: ");
		for(int p = 0; p < audioData.length; p++) {
			System.out.print(audioData[p] + " ");
		}
		System.out.println("\n");
		
		// Stop and close the line
		
		line.stop();
		line.close();
		line = null;
		
		// Stop and close the output stream
		
		try {
			output.flush();
			output.close();
		} catch(IOException io) {
			io.printStackTrace();
		}
		
		// Load bytes into the audio input stream
		
		byte audioBytes[] = output.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		audioInput = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
		
		long milliseconds = (long)((audioInput.getFrameLength() * 1000) / format.getFrameRate());
		duration = milliseconds / 1000.0;
		
		try {
			audioInput.reset();
		} catch(Exception ex) {
			ex.printStackTrace();
			return;
		}
		
		
	}
	
	/**
	 * Gets the predefined audio format.
	 * 
	 * @return
	 * The AudioFormat that was setup.
	 */
	private AudioFormat getAudioFormat() {
		
		AudioFormat format;
		
		// Predefined Constants
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 44100.0f;
		int sampleSize = 16;
		int channels = 2; // 1 = Mono    2 = Stereo
		boolean bigEndian = true; // Whether or not the bytes should be stored in Endian order
		
		
		format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
		return format;
	}
	
	
	/**
	 * Delays, or waits, the given amount of time in milliseconds.
	 * 
	 * @param millis
	 * the amount of time to wait in milliseconds
	 */
	private void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
