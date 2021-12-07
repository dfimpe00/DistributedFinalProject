import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.ArrayList;

public class MasterServer {

	private static HashMap<String, HashMap<String, String[]>> fileLog = new HashMap<String, HashMap<String, String[]>>();
	static ArrayList<String> nodeList = new ArrayList<String>();
	public static String INSTRUCTION_FILE = "instruction.txt";
	public static String FILE_TO_BE_RECEIVED;
	public static int FILE_SIZE = 6022386;

	public static void main(String[] args) throws IOException {

		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		int port = 52005;

		MasterClient masterClient;

		ServerSocket server = new ServerSocket(port);

		System.out.println("Server is starting...");

		while (true) {
			String instruction = "";

			try {
				masterClient = new MasterClient(server.accept());
				Thread t = new Thread(masterClient);

				try {

					System.out.println("Accepted connection : " + masterClient.client);

					// recieve file
					byte[] mybytearray = new byte[FILE_SIZE];
					InputStream is = masterClient.client.getInputStream();

					fos = new FileOutputStream(INSTRUCTION_FILE);
					bos = new BufferedOutputStream(fos);

					bytesRead = is.read(mybytearray, 0, mybytearray.length);
					current = bytesRead;

					do {
						bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
						if (bytesRead >= 0)
							current += bytesRead;
					} while (bytesRead > -1);

					bos.write(mybytearray, 0, current);
					bos.flush();

					System.out.println("File " + INSTRUCTION_FILE + " downloaded (" + current + " bytes read)");
				} finally {
					if (fos != null)
						fos.close();
					if (bos != null)
						bos.close();
				}

				try {

					// InputStreamReader ISR = new
					// InputStreamReader(masterClient.client.getInputStream());
					FileReader fr = new FileReader(INSTRUCTION_FILE);
					BufferedReader br = new BufferedReader(fr);

					String line;
					String[] tokens = null;

					while ((line = br.readLine()) != null) {

						tokens = line.split(",");
						if (tokens[0].equals("1")) {
							String ip = tokens[1];
							System.out.println(ip);
							nodeList.add(ip);

						} else if (tokens[0].equals("2")) {

							for (int i = 0; i < nodeList.size(); i++) {
								MasterClient.sendFiletoNode(nodeList.get(i), INSTRUCTION_FILE);
							}

						}
						// sets filename being received to filename sent specified by user
						else if (tokens[0].equals("3")) {
							FILE_TO_BE_RECEIVED = tokens[1];
							for (int i = 0; i < nodeList.size(); i++) {
								MasterClient.sendFileName(nodeList.get(i), FILE_TO_BE_RECEIVED);

							}
						}

					}
				} catch (Exception ex) {

				}

				String message = "Thread " + t.getName() + " ";

				System.out.println(message);
				t.start();

			} catch (Exception ex) {

			}
		}

	}

	public static String[] getFileList(String username) {

		Set<String> set = fileLog.get(username).keySet();
		String[] fileList = new String[set.size()];

		int i = 0;
		for (String x : set) {
			fileList[i] = x;
			i++;
		}
		return fileList;
	}

	// loads in values to fileLog from the masterfiles.txt
	public static void loadFileLog() {

		String[] logLine;
		String username;
		String filename;
		//
		try (BufferedReader br = new BufferedReader(new FileReader("masterfiles.txt"))) {
			String line;
			while ((line = br.readLine()) != null && br.readLine() != " ") {
				logLine = line.split("::");
				if (logLine.length == 5) {
					username = logLine[0];
					filename = logLine[1];
					String[] datePathServer = { logLine[2], logLine[3], logLine[4] };

					// if key doesn't exist add it and make the HashMap for the value
					if (!fileLog.containsKey(username)) {
						HashMap<String, String[]> innermap = new HashMap<String, String[]>();
						innermap.put(filename, datePathServer);
						fileLog.put(username, innermap);
					} else {
						fileLog.get(username).put(filename, datePathServer);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// adds the entered values into the fileLog hashmap with the keys being the
	// username and filename
	public static synchronized void logAdd(String username, String filename, String date, String path, String server) {

		String[] datePathServer = { date, path, server };

		// if key doesn't exist add it and make the HashMap for the value
		if (!fileLog.containsKey(username)) {
			HashMap<String, String[]> innermap = new HashMap<String, String[]>();
			innermap.put(filename, datePathServer);
			fileLog.put(username, innermap);

			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", true)));
				out.println(username + "::" + filename + "::" + date + "::" + path + "::" + server);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (fileLog.get(username).containsKey(filename)) {

		} else {
			fileLog.get(username).put(filename, datePathServer);
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", true)));
				out.println(username + "::" + filename + "::" + date + "::" + path + "::" + server);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// prints the contents of the fileLog hashmap to masterfiles.txt
	public static synchronized void printlog() {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", false)));

			for (Map.Entry<String, HashMap<String, String[]>> set1 : fileLog.entrySet()) {
				for (Map.Entry<String, String[]> set2 : set1.getValue().entrySet()) {
					out.println(set1.getKey() + " " + set2.getKey() + " " + set2.getValue()[0] + " "
							+ set2.getValue()[1] + " " + set2.getValue()[2]);
				}
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Prints the contents of the fileLog hashmap to the console
	public static void PrintFileLog() {
		for (Map.Entry<String, HashMap<String, String[]>> set1 : fileLog.entrySet()) {
			for (Map.Entry<String, String[]> set2 : set1.getValue().entrySet()) {
				System.out.println(set1.getKey() + " " + set2.getKey() + " " + set2.getValue()[0] + " "
						+ set2.getValue()[1] + " " + set2.getValue()[2]);
			}
		}
	}

	// removes logged file from fileLog hashmap and updates the log text file
	public static void logRemove(String username, String filename) {
		if (fileLog.containsKey(username)) {
			if (fileLog.get(username).containsKey(filename)) {
				fileLog.get(username).remove(filename);
				printlog();
			}
		}

	}

	// below here are methods for system logs and appending to the system logs
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
		String str2 = datetime + ", " + nodeName + ", " + ip;
		String str3 = String.format("%-15s %s", str1, str2);
		sysLogPrint(str3);
	}

	public static void userauth(String userName, String ip) {
		LocalDateTime datetime = LocalDateTime.now();
		String str1 = "[userauth]";
		String str2 = datetime + ", " + userName + ", " + ip;
		String str3 = String.format("%-15s %s", str1, str2);
		sysLogPrint(str3);
	}

	public static void userconnect(String userName, String ip) {
		LocalDateTime datetime = LocalDateTime.now();
		String str1 = "[userconnect]";
		String str2 = datetime + ", " + userName + ", " + ip;
		String str3 = String.format("%-15s %s", str1, str2);
		sysLogPrint(str3);
	}

	public static void dnfailure(String dnName, String ip) {
		LocalDateTime datetime = LocalDateTime.now();
		String str1 = "[dnfailure]";
		String str2 = datetime + ", " + dnName + ", " + ip;
		String str3 = String.format("%-15s %s", str1, str2);
		sysLogPrint(str3);
	}

	public static void fileinsert(String dnName, String userName, String fileName) {
		LocalDateTime datetime = LocalDateTime.now();
		String str1 = "[fileinsert]";
		String str2 = datetime + ", " + dnName + ", " + userName + ", " + fileName;
		String str3 = String.format("%-15s %s", str1, str2);
		sysLogPrint(str3);
	}

	public static void filedelete(String dnName, String userName, String fileName) {
		LocalDateTime datetime = LocalDateTime.now();
		String str1 = "[filedelete]";
		String str2 = datetime + ", " + dnName + ", " + userName + ", " + fileName;
		String str3 = String.format("%-15s %s", str1, str2);
		sysLogPrint(str3);
	}

	public static void fileaccess(String dnName, String userName, String fileName) {
		LocalDateTime datetime = LocalDateTime.now();
		String str1 = "[fileaccess]";
		String str2 = datetime + ", " + dnName + ", " + userName + ", " + fileName;
		String str3 = String.format("%-15s %s", str1, str2);
		sysLogPrint(str3);
	}

	public static void filefailure(String dnName, String userName, String fileName) {
		LocalDateTime datetime = LocalDateTime.now();
		String str1 = "[filefailure]";
		String str2 = datetime + ", " + userName + ", " + fileName;
		String str3 = String.format("%-15s %s", str1, str2);
		sysLogPrint(str3);
	}
}
