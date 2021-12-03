import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MasterServer {

	static ArrayList<String> nodeList = new ArrayList<String>();

	public static void main(String[] args) throws IOException {

		int port = 52005;

		MasterClient masterClient;

		ServerSocket server = new ServerSocket(port);

		System.out.println("Server is starting...");

		while (true) {

			try {

				masterClient = new MasterClient(server.accept());

				Thread t = new Thread(masterClient);

				InputStreamReader ISR = new InputStreamReader(masterClient.client.getInputStream());
				BufferedReader br = new BufferedReader(ISR);

				String ip = br.readLine();
				System.out.println(ip);
				nodeList.add(ip);

				for (int i = 0; i < nodeList.size(); i++) {
					MasterClient.sendMessage();
				}

				String message = "Thread " + t.getName() + " ";

				System.out.println(message);
				t.start();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
}
