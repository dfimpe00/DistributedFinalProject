import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MasterServer {

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
		String isFive = "";
		String tokens1 = "";
		String tokens2 = "";
		String isValid = "";

		while (true) {

			try {
				masterClient = new MasterClient(server.accept());

				Thread t = new Thread(masterClient);

				t.start();

				if (isFive.equals("5")) {

					if (MasterClient.checkPass(tokens1, tokens2)) {
						isValid = "1";
					} else {
						isValid = "2";
					}

					ObjectOutputStream oos = new ObjectOutputStream(masterClient.client.getOutputStream());

					oos.writeObject(isValid);

					isFive = "";
					tokens1 = "";
					tokens2 = "";
					oos.close();

				}

				try {

					System.out.println("Accepted connection : " + masterClient.client);

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

					FileReader fr = new FileReader(INSTRUCTION_FILE);
					BufferedReader br = new BufferedReader(fr);

					String line;
					String[] tokens = null;

					while ((line = br.readLine()) != null) {

						tokens = line.split(",");
						// sending ip from nodes case
						if (tokens[0].equals("1")) {
							String ip = tokens[1];
							nodeList.add(ip);

						}
						// inserting file case, tokens[0] is instruciton, tokens[1] is username
						else if (tokens[0].equals("2")) {

							for (int i = 0; i < nodeList.size(); i++) {
								MasterClient.sendFiletoNode(nodeList.get(i), INSTRUCTION_FILE);
							}

						}
						// sets filename being received to filename sent specified by user, tokens[1] is
						// filename, set global variable to tokens[1]
						else if (tokens[0].equals("3")) {
							FILE_TO_BE_RECEIVED = tokens[1];
							for (int i = 0; i < nodeList.size(); i++) {
								MasterClient.sendFileName(nodeList.get(i), FILE_TO_BE_RECEIVED);

							}
						}
						// delete a file case, tokens[0] is instruction, tokens[1] is username,
						// tokens[2] is filename being deleted
						else if (tokens[0].equals("4")) {
							String filetoDelete = tokens[2];
							for (int i = 0; i < nodeList.size(); i++) {
								MasterClient.sendDeleteFileName(nodeList.get(i), filetoDelete);

							}
						}
						// user authentication
						else if (tokens[0].equals("5")) {

							isFive = tokens[0];
							tokens1 = tokens[1];
							tokens2 = tokens[2];

						}
					}
				} catch (Exception ex) {

				}

				String message = "Thread " + t.getName() + " ";
				System.out.println(message);

			} catch (Exception ex) {

			}
		}

	}

	
}
