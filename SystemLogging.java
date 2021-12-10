import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class SystemLogging {
	
		public static synchronized void sysLogPrint(String str) {
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
			    out.println(str);
			    out.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
		
		public static void dnconnect(String nodeName, String ip) {
			LocalDateTime datetime = LocalDateTime.now();
			String str1 = "[dncconnect]";
			String str2 = datetime +", "+ nodeName + ", " + ip;
			String str3 = String.format("%-15s %s", str1, str2);
			sysLogPrint(str3);
			}
		
		public static void userauth(String userName, String ip) {
			LocalDateTime datetime = LocalDateTime.now();
			String str1 = "[userauth]";
			String str2 = datetime +", "+ userName + ", " + ip;
			String str3 = String.format("%-15s %s", str1, str2);
			sysLogPrint(str3);
		}
		
		public static void userconnect(String userName, String ip) {
			LocalDateTime datetime = LocalDateTime.now();
			String str1 = "[userconnect]";
			String str2 = datetime +", "+ userName + ", " + ip;
			String str3 = String.format("%-15s %s", str1, str2);
			sysLogPrint(str3);
		}
		
		public static void dnfailure(String dnName, String ip) {
			LocalDateTime datetime = LocalDateTime.now();
			String str1 = "[dnfailure]";
			String str2 = datetime +", "+ dnName + ", " + ip;
			String str3 = String.format("%-15s %s", str1, str2);
			sysLogPrint(str3);
		}
		
		public static void fileinsert(String dnName, String userName, String fileName) {
			LocalDateTime datetime = LocalDateTime.now();
			String str1 = "[fileinsert]";
			String str2 = datetime +", "+ dnName + ", " + userName + ", " + fileName;
			String str3 = String.format("%-15s %s", str1, str2);
			sysLogPrint(str3);
		}
		
		public static void filedelete(String dnName, String userName, String fileName) {
			LocalDateTime datetime = LocalDateTime.now();
			String str1 = "[filedelete]";
			String str2 = datetime +", "+ dnName + ", " + userName + ", " + fileName;
			String str3 = String.format("%-15s %s", str1, str2);
			sysLogPrint(str3);
		}
		
		public static void fileaccess(String dnName, String userName, String fileName) {
			LocalDateTime datetime = LocalDateTime.now();
			String str1 = "[fileaccess]";
			String str2 = datetime +", "+ dnName + ", " + userName + ", " + fileName;
			String str3 = String.format("%-15s %s", str1, str2);
			sysLogPrint(str3);
		}
		
		public static void filefailure(String dnName, String userName, String fileName) {
			LocalDateTime datetime = LocalDateTime.now();
			String str1 = "[filefailure]";
			String str2 = datetime +", " + userName + ", " + fileName;
			String str3 = String.format("%-15s %s", str1, str2);
			sysLogPrint(str3);
		}
}


