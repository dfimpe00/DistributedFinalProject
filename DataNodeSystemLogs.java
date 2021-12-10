import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class DataNodeSystemLogs {

	//below here are methods for system logs and appending to the system logs
			public static synchronized void sysLogPrint(String str) {
				try {
				    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("datalog.txt", true)));
				    out.println(str);
				    out.close();
				} catch (IOException e) {
				    e.printStackTrace();
				}
			}
			
			public static void fileinsert(String userName, String fileName, String fileSize, String Path) {
				LocalDateTime datetime = LocalDateTime.now();
				String str1 = "[fileinsert]";
				String str2 = datetime + ", " + userName + ", " + fileName + ", " + fileSize + ", " + Path;
				String str3 = String.format("%-15s %s", str1, str2);
				sysLogPrint(str3);
			}
			
			public static void fileDelete(String userName, String fileName) {
				LocalDateTime datetime = LocalDateTime.now();
				String str1 = "[fileDelete]";
				String str2 = datetime + ", " + userName + ", " + fileName;
				String str3 = String.format("%-15s %s", str1, str2);
				sysLogPrint(str3);
			}
			
			public static void fileAccess(String userName, String fileName) {
				LocalDateTime datetime = LocalDateTime.now();
				String str1 = "[fileAccess]";
				String str2 = datetime + ", " + userName + ", " + fileName;
				String str3 = String.format("%-15s %s", str1, str2);
				sysLogPrint(str3);
			}

}
