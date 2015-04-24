package adb.taxe.record;

import java.io.IOException;
import java.net.URLDecoder;

public class RecordingWindow {
	
	public static void createNewRecordingWindow(String fileLoc)
	{

		try {
			String path = RecordingWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			System.out.println(decodedPath);
			String[] cmd = {"java", "-jar" , decodedPath.split("/")[decodedPath.split("/").length - 1],  fileLoc};
			Process proc = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
 