import java.io.IOException;
import java.net.ServerSocket;

public class NodeServer {

	public static void main(String[] args) throws IOException {

		int port = 52005;

		NodeClient client;
		NodeClient.sendNode();

		ServerSocket server = new ServerSocket(port);

		System.out.println("Server is starting....");

		while (true) {

			try {

				client = new NodeClient(server.accept());

				Thread t = new Thread(client);

				String message = "Thread " + t.getName() + " has been assigned to this client.";

				System.out.println(message);
				t.start();

			} catch (Exception ex) {
				server.close();
				ex.printStackTrace();

			}
		}
	}
}
