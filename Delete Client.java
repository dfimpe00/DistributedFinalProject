import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Client extends Thread {

	private Socket client;

	public Client(Socket client) {
		this.client = client;
	}

	public static void main(String[] args) {

		String host = "localhost";
		int port = 3205;

		// file path to be deleted
		String filePath = "/Users/westongraham/Desktop/test.txt";

		try {
			Socket socket = new Socket(host, port);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

			Path fileToDelete = Paths.get(filePath);
			Files.delete(fileToDelete);
			System.out.println("File: " + filePath + " was successfully deleted");

			System.out.println("Sending Message........");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
