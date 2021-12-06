import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {

	public static void main(String[] args) throws IOException {

		int port = 3205;

		DeleteClient client;

		ServerSocket server = new ServerSocket(port);

		System.out.println("Server is starting");

		while (true) {

			try {

				client = new DeleteClient(server.accept());

				Socket socket = server.accept();

				System.out.println("Connection from " + client);

				Thread t = new Thread(client);

				// String message = "Thread " + t.getName() + " ";

				// System.out.println(message);
				t.start();

				// get the input stream from the connected socket
				InputStream inputStream = socket.getInputStream();
				// create a DataInputStream so we can read data from it.
				DataInputStream dataInputStream = new DataInputStream(inputStream);

				// read the message from the socket
				String message = dataInputStream.readUTF();
				System.out.println("The message sent from the socket was: " + message);

				// passes message to the deleteFile method and deletes the file
				deleteFile(message);

			} catch (Exception ex) {
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
