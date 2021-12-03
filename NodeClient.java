import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class NodeClient extends Thread {

	private Socket client;

	public NodeClient(Socket client) {
		this.client = client;
	}

	public static void main(String[] args) {

	}

	public static void sendNode() {

		// sending node information to master server to be able to add to array list
		Node n = new Node("192.168.1.181", 52005);
		
		String host = "192.168.1.196";
		int port = 52005;

		try {
			Socket socket = new Socket(host, port);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			writer.write(n.getHost());
			writer.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * 1. open a socket 2. open some stream to the socket 3. read data or write
	 */
}
