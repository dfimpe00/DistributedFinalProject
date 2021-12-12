import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MasterServer {
	
	
	public static void main(String[] args)throws IOException{
		
		int port = 52005;
		
		UserPass.readfile("password.txt");
		FileLogging.loadFileLog();
		
			try {
				ServerSocket ss = new ServerSocket(port);
				System.out.println("Starting server");
		
				while(true) {
		
					Socket client = ss.accept();
					System.out.println("New client connected"
                            + client.getInetAddress()
                                  .getHostAddress());
					MasterThread mt=new MasterThread(client);
					
					Thread t = new Thread(mt);
					t.start();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		
	}

}
