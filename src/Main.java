
public class Main {
	
	static GlobalVariables globalVars = new GlobalVariables();
	static AudioManager audioManager = new AudioManager();
	
	public static void main(String args[]) {
		
		audioManager.captureAudio(2000);
		
	}
}
