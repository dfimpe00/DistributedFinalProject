import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MasterServer {
	
	public static HashMap<String, HashMap<String, String[]>> fileLog = new HashMap<String, HashMap<String, String[]>>(); //Key : username value:hashmap<String, String[]> key: filename value [0]date [1]path [2]server
	public static HashMap<String, String[]> dataNodes = new HashMap<String, String[]>(); // Key = DataNode Name values [0]:ip, [1]:accesses
	public static HashMap<String, String> up = new HashMap<String, String>();
	
	public static void main(String[] args)throws IOException{
		
		int port = 52005; 
		readfile("password.txt");// loads the userpassword Hashmap from a text file
		loadFileLog();//loads the FileLog from a text file
		
		
			try {
				ServerSocket ss = new ServerSocket(port);
				System.out.println("Starting server");
		
				while(true) {
		
					Socket client = ss.accept();
					System.out.println("New client connected"
                            + client.getInetAddress()
                                  .getHostAddress());
					MasterThread mt=new MasterThread(client, fileLog, dataNodes, up);
					
					Thread t = new Thread(mt);
					t.start();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		
	}
	
	//loads in values to fileLog from the masterfiles.txt
		public static void loadFileLog() {
			
			String[] logLine;
			String username;
			String filename;
			// 
			try (BufferedReader br = new BufferedReader(new FileReader("masterfiles.txt"))) {
			    String line;
			    while ((line = br.readLine()) != null && br.readLine()!=" ") {
			       logLine = line.split("::");
			       if(logLine.length == 5) {
			    	   username = logLine[0];
				       filename = logLine[1];
				       String[] datePathServer = {logLine[2], logLine[3], logLine[4]};
				       
				       //if key doesn't exist add it and make the HashMap for the value
				       if(!fileLog.containsKey(username)) {
				    	   HashMap<String, String[]> innermap =  new HashMap<String, String[]>();
				    	   innermap.put(filename, datePathServer);
				    	   fileLog.put(username, innermap);
				       }else {
				    	   fileLog.get(username).put(filename, datePathServer);
				       }

			       }
			       		       
			    }
			    
			}catch(Exception e){
				e.printStackTrace();	
			}
			
		}
		
		public static void readfile(String filename) {
			try {
				FileReader fr = new FileReader(filename);
				BufferedReader br = new BufferedReader(fr);
			
				String line = "";
				while ((line = br.readLine()) != null) {

					String[] parts = line.split(",");

					String name = parts[0];
					String passWord = parts[1];
					
					// put name, number in HashMap if they are
					// not empty
					if (!name.equals("") && !passWord.equals("")) {

						up.put(name, passWord);
					}
				}

				br.close();

			} catch (Exception e) {

				e.printStackTrace();
			}
			
		}

}
