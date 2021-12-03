import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class userClient extends Thread {

	private Socket client;

	public userClient(Socket client) {
		this.client = client;
	}

	public static void main(String[] args) {

		String host = "192.168.1.196";
		int port = 52005;

		try {
			Socket socket = new Socket(host, port);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

			System.out.println("Sending Message........");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * 1. open a socket 2. open some stream to the socket 3. read data or write
	 */
}
