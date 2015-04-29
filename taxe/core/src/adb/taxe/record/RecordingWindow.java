package adb.taxe.record;

import java.io.IOException;
import java.net.URLDecoder;

/**This class is used to store static methods related to creating new recording windows*/
public class RecordingWindow {
	
	/**This method creates a new window by opening the game's own jar again with the location of a recording as one
	 * of the arguments passed to the new process created by running the jar.
	 * @param fileLoc The location of a recording ot playback
	 */
	public static void createNewRecordingWindow(String fileLoc)
	{

		try {
			String path = RecordingWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			System.out.println(decodedPath);
			String[] cmd = {"java", "-jar" , decodedPath.split("/")[decodedPath.split("/").length - 1],  fileLoc};
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
 