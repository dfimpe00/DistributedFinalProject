import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.*;


public class DeleteClient extends Thread {

	private Socket client;

	public DeleteClient(Socket client) {
		this.client = client;
	}

	public static void main(String[] args) {

		String host = "localhost";
		int port = 3205;

		// file path to be deleted
		String filePath = "/Users/westongraham/Desktop/test.txt";

		try {
			Socket socket = new Socket(host, port);
			System.out.println("Connected");
			

			// get the output stream from the socket.
			OutputStream outputStream = socket.getOutputStream();
			// create a data output stream from the output stream so we can 
			// send data through it.
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

			System.out.println("Sending string to the ServerSocket");

			// write the message we want to send.
			dataOutputStream.writeUTF(filePath);
			dataOutputStream.flush(); // send the message
			dataOutputStream.close(); // closes the output stream when we're done.

			
			 socket.close(); // closes the socket

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
			// write the message we want to send.
			dataOutputStream.writeUTF(filePath);
			dataOutputStream.flush(); // send the message
			dataOutputStream.close(); // closes the output stream when we're done.

			
			 socket.close(); // closes the socket

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
