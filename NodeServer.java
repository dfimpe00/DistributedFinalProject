import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class NodeServer {

	private static HashMap<String, HashMap<String, String[]>> fileLog = new HashMap<String, HashMap<String, String[]>>();
	public static String FILE_TO_RECEIVED;
	public static String INSTRUCTION_FILE = "instruction.txt";
	public final static int FILE_SIZE = 6022386;

	public static void main(String[] args) throws IOException {

		int port = 52005;

		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		NodeClient nodeClient;
		NodeClient.sendNode();

		ServerSocket server = new ServerSocket(port);

		System.out.println("Server is starting....");

		while (true) {

			try {

				nodeClient = new NodeClient(server.accept());

				Thread t = new Thread(nodeClient);

				try {

					System.out.println("Accepted connection : " + nodeClient.client);

					// receive file
					byte[] mybytearray = new byte[FILE_SIZE];
					InputStream is = nodeClient.client.getInputStream();

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

				if (FILE_TO_RECEIVED != null) {
					try {
						FileReader fr = new FileReader(INSTRUCTION_FILE);
						BufferedReader br = new BufferedReader(fr);

						File f = new File(FILE_TO_RECEIVED);
						FileWriter myWriter = new FileWriter(f);

						String line;
						String[] tokens = null;
						int count = 0;

						while ((line = br.readLine()) != null) {

							tokens = line.split(",");
							if (tokens[0].equals("2")) {
								myWriter.write(tokens[2]);
								myWriter.write("\n");
							} else {
								myWriter.write(line);
								myWriter.write("\n");
							}

						}

						myWriter.close();

					} catch (IOException e) {
						System.out.println("An error occurred.");
						e.printStackTrace();
					}
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

						if (tokens[0].equals("3")) {
							FILE_TO_RECEIVED = tokens[1];
						} else if (tokens[0].equals("4")) {
							deleteFile(tokens[1]);
						}

					}
				} catch (Exception ex) {

				}

				String message = "Thread " + t.getName() + " has been assigned to this client.";

				System.out.println(message);

				t.start();

			} catch (Exception ex) {
				server.close();
				ex.printStackTrace();

			}
		}
	}

	public static void deleteFile(String file) throws IOException {

		Path fileToDelete = Paths.get(file);
		Files.delete(fileToDelete);
		System.out.println("File: " + file + " was successfully deleted");

	}

	// loads in values to fileLog from the datafiles.txt
	public static void loadFileLog() {

		String[] logLine;
		String username;
		String filename;
		//
		try (BufferedReader br = new BufferedReader(new FileReader("datafiles.txt"))) {
			String line;
			while ((line = br.readLine()) != null && br.readLine() != " ") {
				logLine = line.split("::");
				if (logLine.length == 6) {
					username = logLine[0];
					filename = logLine[1];
					String[] datePathSizeAccess = { logLine[2], logLine[3], logLine[4], logLine[5] };

					// if key doesn't exist add it and make the HashMap for the value
					if (!fileLog.containsKey(username)) {
						HashMap<String, String[]> innermap = new HashMap<String, String[]>();
						innermap.put(filename, datePathSizeAccess);
						fileLog.put(username, innermap);
					} else {
						fileLog.get(username).put(filename, datePathSizeAccess);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// adds the entered values into the fileLog hashmap with the keys being the
	// username and filename
	public static synchronized void logInsert(String username, String filename, String date, String path, String size,
			String accessnum) {

		String[] datePathServer = { date, path, size, accessnum };

		// if key doesn't exist add it and make the HashMap for the value
		if (!fileLog.containsKey(username)) {
			HashMap<String, String[]> innermap = new HashMap<String, String[]>();
			innermap.put(filename, datePathServer);
			fileLog.put(username, innermap);

			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", true)));
				out.println(username + "::" + filename + "::" + date + "::" + path + "::" + size + "::" + accessnum);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (fileLog.get(username).containsKey(filename)) {

		} else {
			fileLog.get(username).put(filename, datePathServer);
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", true)));
				out.println(username + "::" + filename + "::" + date + "::" + path + "::" + size + "::" + accessnum);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void logDelete(String username, String filename) {
		if (fileLog.containsKey(username)) {
			if (fileLog.get(username).containsKey(filename)) {
				fileLog.get(username).remove(filename);
				printlog();
			}
		}

	}

	// prints the contents of the fileLog hashmap to datafiles.txt
	public static synchronized void printlog() {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("datafiles.txt", false)));

			for (Map.Entry<String, HashMap<String, String[]>> set1 : fileLog.entrySet()) {
				for (Map.Entry<String, String[]> set2 : set1.getValue().entrySet()) {
					out.println(set1.getKey() + " " + set2.getKey() + " " + set2.getValue()[0] + " "
							+ set2.getValue()[1] + " " + set2.getValue()[2] + " " + set2.getValue()[3]);
				}
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// adds one to the number of time accessed and then rewrites the datafiles.txt
	public static synchronized void accessedFile(String user, String file) {
		String[] hashvalue = fileLog.get(user).get(file);
		int numAccessed = Integer.parseInt(hashvalue[5]);
		numAccessed += 1;
		hashvalue[5] = numAccessed + "";
		fileLog.get(user).put(file, hashvalue);
		printlog();
	}

	// below here are methods for system logs and appending to the system logs
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
