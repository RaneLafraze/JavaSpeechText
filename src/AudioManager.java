import javax.sound.sampled.AudioInputStream;


public class AudioManager {
	
	AudioInputStream audioInput;
	
	public AudioManager() {
		
		
		
	}
	
	/**
	 * Captures audio for the give amount of time in milliseconds.
	 * 
	 * @param timeToCapture
	 * the time, in milliseconds, to record for
	 */
	public void captureAudio(int millis) { 
		
		Capture capture = new Capture();
		capture.start();
		delay(millis);
		capture.stop();
		
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
