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


}
