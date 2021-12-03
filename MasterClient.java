import java.io.PrintWriter;
import java.net.Socket;

public class MasterClient extends Thread {

	public Socket client;

	public MasterClient(Socket client) {
		this.client = client;
	}

	public static void main(String[] args) {

	}

	public static void sendMessage(String ip) {
		String host = ip;
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


