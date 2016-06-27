import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
	
	private boolean captureAudio = false;
	
	
	public Capture() {
				
	}
	
	/**
	 * Starts the thread.
	 */
	public void start() {
		thread = new Thread(this);
		thread.setName("Capture");
		thread.start();
		
		captureAudio = true;
	}
	
	/**
	 * Stops the thread.
	 */
	public void stop() {
		captureAudio = false;
		//thread = null;
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
		byte[] audioData = new byte[line.getBufferSize() / 5];
		int bytesRead; // Number of bytes read
		
		line.start();
		
		while(captureAudio) {
			// Update the number of bytes read
			bytesRead = line.read(audioData, 0, audioData.length);
			// Write the audioData to the output stream
			output.write(audioData, 0, bytesRead);
		}
		
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
		
		// Print the bytes recorded
		
		byte audioBytes[] = output.toByteArray();
		for(int p = 0; p < audioBytes.length; p++) {
			if((audioBytes[p] != -1) && (audioBytes[p] != 0) && (audioBytes[p] != 1)) {
				System.out.print(audioBytes[p] + ", ");
			}
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
			e.printStackTrace();
		}
	}
	
}
