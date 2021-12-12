import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MasterThread extends Thread{
	Socket s;
	String[] values;
	int bytesRead;
	int current = 0;
	String isValid = "";
	public static int FILE_SIZE = 6022386;
	
	
	public MasterThread(Socket s) {
		this.s= s;
	}
	
	public void run() {
		try {
			
			System.out.println("hello");

			byte[] mybytearray = new byte[FILE_SIZE];
			InputStream is = s.getInputStream();

			bytesRead = is.read(mybytearray, 0, mybytearray.length);
			
			
			

//			do {
//				bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
//				if (bytesRead >= 0)
//					current += bytesRead;
//			} while (bytesRead > -1);
			String msg = new String(mybytearray);
			System.out.println(msg);
			
			
			
			String[] tokens = msg.split(",");
			
			OutputStream os = s.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			
			System.out.println("writing");

			//dos.writeUTF("Testing");
			//dos.flush();
			
			
			//0:??? 1: DnConnect 2: Inserting File 3: Retrieve File 4: delete a file 5: get file list
			if(tokens[0].equals("0")) {
				
			}else if(tokens[0].equals("1")){
				//New Data Node connecting
				String ip = tokens[1];
				String nodeName = tokens[2];
				SystemLogging.dnconnect(nodeName, ip);
				DataNodeList.addDataNode(nodeName, ip);
				
			}else if(tokens[0].equals("2")) {
				
			}else if(tokens[0].equals("3")) {
				//retrieve file list
				String username = tokens[1];
				String[] list = FileLogging.getFileList(username);
				String filelist = "";
				for(int i = 0; i<list.length; i++) {
					if(i == 0) {
						filelist= list[i];
					}else {
						filelist= filelist + "," + list[i];
					}
				}
				
				dos.writeUTF(filelist);
				
			}else if(tokens[0].equals("4")) {
				//Delete a file from DataNodes
				String filetoDelete = tokens[1];
				//String filetoDelete = tokens[2];
				
			}else if(tokens[0].equals("5")) {
				//User Login
				String username = tokens[1];
				String password = tokens[2];
				
				if(UserPass.up.get(username).equals(password)) {
					isValid = "1";
					SystemLogging.userconnect(username, s.getInetAddress()+"");
				}else {
					isValid = "2";
					SystemLogging.userauth(username, s.getInetAddress()+"");
				}
				
				dos.writeUTF(isValid);
				dos.flush();
				dos.close();
				
			}
			
			
			
			
			
			
			dos.close();
			
			System.out.println("closing the socket and terminating program.");
			s.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}