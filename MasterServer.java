import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.ArrayList;

public class MasterServer {

	static ArrayList<String> nodeList = new ArrayList<String>();
	public final static String FILE_TO_RECEIVED = "test123.txt";
	public final static int FILE_SIZE = 6022386;

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

					fos = new FileOutputStream(FILE_TO_RECEIVED);
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

					System.out.println("File " + FILE_TO_RECEIVED + " downloaded (" + current + " bytes read)");
				} finally {
					if (fos != null)
						fos.close();
					if (bos != null)
						bos.close();
				}

				try {

					// InputStreamReader ISR = new
					// InputStreamReader(masterClient.client.getInputStream());
					FileReader fr = new FileReader("test123.txt");
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
							System.out.println("test");
						}

					}
				} catch (Exception ex) {

				}

				for (int i = 0; i < nodeList.size(); i++) {
					MasterClient.sendMessage(nodeList.get(i));
				}

				String message = "Thread " + t.getName() + " ";

				System.out.println(message);
				t.start();

			} catch (Exception ex) {

			}
		}

	}
}
