import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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

	public static void sendFileName(String server, String FILE_TO_SEND) {

		int port = 52005;

		try {
			Socket socket = new Socket(server, port);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			writer.write("3," + FILE_TO_SEND);
			writer.close();

			System.out.println("Sending Message........");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void sendFiletoNode(String SERVER, String FILE_TO_SEND) throws IOException {

		int SOCKET_PORT = 52005;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		BufferedWriter writer = null;

		Socket sock = null;
		try {
			sock = new Socket(SERVER, SOCKET_PORT);
			System.out.println("Connecting...");

			writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

			// send file

			File myFile = new File(FILE_TO_SEND);
			byte[] mybytearray = new byte[(int) myFile.length()];

			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);

			bis.read(mybytearray, 0, mybytearray.length);
			os = sock.getOutputStream();

			System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
			os.write(mybytearray, 0, mybytearray.length);
			os.flush();
			System.out.println("Done.");

		} finally {
			if (bis != null)
				bis.close();
			if (os != null)
				os.close();
			if (sock != null)
				sock.close();

		}
	}

	/*
	 * 1. open a socket 2. open some stream to the socket 3. read data or write
	 */
}
