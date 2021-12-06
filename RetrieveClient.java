import java.io.File;
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
		
		File folder = new File("C:\\Users\\Daxim\\Desktop\\");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println(listOfFiles[i].getName());
			}
		}
	}
}
