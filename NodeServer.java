import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeServer {
	
	public static void main(String[] args) {
		int port = 52005;
		String NAME = "DATA1";
		
		SendNode(NAME);
		
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Starting Server");
			
			while(true) {
				Socket client = ss.accept();
				System.out.println("New client connected: " + client.getInetAddress().getHostAddress());
				
				NodeThread nt = new NodeThread(client);
				Thread t = new Thread(nt);
				t.start();
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void SendNode(String name) {
		
		String host = "192.168.1.196";
		int port = 52005;

		try {
			Socket socket = new Socket(host, port);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			String ip = socket.getLocalAddress() + "";
			String[] ipa = ip.split("/");
			
			writer.write("1,");
			writer.write(ipa[1] + ",");
			writer.write(name);
			writer.close();
			socket.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
