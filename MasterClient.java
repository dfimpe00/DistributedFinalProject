import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MasterClient extends Thread {

	public static Map<String, String> map = new HashMap<String, String>();
	public Socket client;

	public MasterClient(Socket client) {
		this.client = client;
	}

	public static void main(String[] args) {

	}

	public static void sendSuccessfulLogin(String server, String FILE_TO_SEND) {

		int port = 52005;

		try {
			Socket socket = new Socket(server, port);
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

	public static void sendDeleteFileName(String server, String FILE_TO_SEND) {

		int port = 52005;

		try {
			Socket socket = new Socket(server, port);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			writer.write("4," + FILE_TO_SEND);
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

	public static Map<String, String> readFile(String filename) {

		try {

			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			String line = "";
			String[] tokens = null;

			while ((line = br.readLine()) != null) {

				String[] parts = line.split(":");

				String name = parts[0].trim();
				String passWord = parts[1].trim();

				// put name, number in HashMap if they are
				// not empty
				if (!name.equals("") && !passWord.equals("")) {

					map.put(name, passWord);
				}
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return map;
	}

	public static String encryptPass(String passWord) {

		StringBuffer output = new StringBuffer();

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(passWord.getBytes(StandardCharsets.UTF_8));

			for (int i = 0; i < hash.length; i++) {

				String hex = Integer.toHexString(0xff & hash[i]);

				if (hex.length() == 1) {

					output.append('0');
				}

				output.append(hex);
			}

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}

		return output.toString();
	}

	public static boolean checkPass(String username, String passWord) {

		String pass = readFile("password.txt").get(username);

		String userpass = encryptPass(pass);

		if (userpass.equals(passWord)) {

			return true;
		}

		return false;
	}

	/*
	 * 1. open a socket 2. open some stream to the socket 3. read data or write
	 */
}
