import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NodeThread extends Thread {

	Socket s;
	int bytesRead;
	public static int FILE_SIZE = 6022386;

	public NodeThread(Socket s) {
		this.s = s;
	}

	public void run() {
		try {
			byte[] mybytearray = new byte[FILE_SIZE];
			InputStream is = s.getInputStream();

			bytesRead = is.read(mybytearray, 0, mybytearray.length);

			String msg = new String(mybytearray);

			String[] tokens = msg.split(",");

			OutputStream os = s.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);

			if (tokens[0].equals("2")) {
				// file insert

				String username = tokens[1];
				String filename = tokens[2];
				String contents = tokens[3];
				File dir = new File(username + "/");

				if (dir.isDirectory()) {
					File f = new File(username + "/" + filename);
					FileWriter fr = new FileWriter(f);
					fr.write(tokens[3]);
				} else {
					System.out.println("making a dir");
					dir.mkdir();
					File f = new File(username + "/" + filename);
					FileWriter fr = new FileWriter(f);
					fr.write(tokens[3]);
				}

			} else if (tokens[0].equals("4")) {
				// file delete
				String username = tokens[1];
				String filename = tokens[2].trim();

				File dir = new File(username + "/");

				if (dir.isDirectory()) {

					Files.deleteIfExists(Paths.get(username + "/" + filename));
					System.out.println("File: " + username + "/" + filename + " was successfully deleted");

				}
			} else if (tokens[0].equals("6")) {
				// file retrieve
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
