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

		//File to be inserted
		String fromFile = "test.txt";
    //File path for inserted file
		String toFile = "C:\\Users\\Daxim\\Desktop\\test.txt";

        Path source = Paths.get(fromFile);
        Path target = Paths.get(toFile);

        try {
            Files.copy(source, target);

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

}
