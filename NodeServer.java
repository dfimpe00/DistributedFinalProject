import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NodeServer {
	
	public static HashMap<String, HashMap<String, String[]>> fileLog = new HashMap<String, HashMap<String, String[]>>();
	
	public static void main(String[] args) {
		int port = 52005;
		String NAME = "DATA2";
		
		SendNode(NAME);
		loadFileLog();
		
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Starting Server");
			
			while(true) {
				Socket client = ss.accept();
				System.out.println("New client connected: " + client.getInetAddress().getHostAddress());
				
				NodeThread nt = new NodeThread(client, fileLog);
				Thread t = new Thread(nt);
				t.start();
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void SendNode(String name) {
		
		String host = "10.181.244.157";
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
	
	//loads in values to fileLog from the datafiles.txt
		public static void loadFileLog() {
				
				String[] logLine;
				String username;
				String filename;
				// 
				try (BufferedReader br = new BufferedReader(new FileReader("datafiles.txt"))) {
				    String line;
				    while ((line = br.readLine()) != null && br.readLine()!=" ") {
				       logLine = line.split("::");
				       if(logLine.length == 6) {
				    	   username = logLine[0];
					       filename = logLine[1];
					       String[] datePathSizeAccess = {logLine[2], logLine[3], logLine[4], logLine[5]};
					       
					       //if key doesn't exist add it and make the HashMap for the value
					       if(!fileLog.containsKey(username)) {
					    	   HashMap<String, String[]> innermap =  new HashMap<String, String[]>();
					    	   innermap.put(filename, datePathSizeAccess);
					    	   fileLog.put(username, innermap);
					       }else {
					    	   fileLog.get(username).put(filename, datePathSizeAccess);
					       }

				       }
				       		       
				    }
				}catch(Exception e){
					e.printStackTrace();	
				}
			}

}

