import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
			
			if(tokens[0].equals("2")) {
				//file insert
				
			}else if(tokens[0].equals("4")) {
				//file delete
			}else if(tokens[0].equals("6")) {
				//file retrieve
			}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
