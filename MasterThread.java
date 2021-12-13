import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MasterThread extends Thread{
	Socket s;
	String[] values;
	int bytesRead;
	int current = 0;
	String isValid = "";
	public static int FILE_SIZE = 6022386;
	public HashMap<String, HashMap<String, String[]>> fileLog;
	public HashMap<String, String> up;
	public HashMap<String, String[]> dataNodes;
	
	
	public MasterThread(Socket s, HashMap<String, HashMap<String, String[]>> fileLog, HashMap<String, String[]> dataNodes, HashMap<String, String> up) {
		this.s= s;
		this.fileLog = fileLog;
		this.dataNodes = dataNodes;
		this.up = up;
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
				//insert file
				String username = tokens[1];
				String filename = tokens[2];
				String content = tokens[3].trim();
				String[] dn = getMinAccessedInput();
				for(int i = 0; i < dn.length; i++) {
					if(dn[i].equals("")) {
						
					} else {
						
						String[] values = dataNodes.get(dn[i]);
					
						try {
							Socket socket = new Socket(values[0], 52005);
							OutputStream os1 = socket.getOutputStream();
							String output = tokens[0] + "," + tokens[1]+ "," + tokens[2]+ "," + tokens[3];
							byte[] byteArray = output.getBytes();
							os1.write(byteArray);
						}catch(Exception ex) {
							
						}
					}
				}
				
			}else if(tokens[0].equals("3")) {
				//retrieve file list
				String username = tokens[1].trim();
				System.out.println(username);
				String[] list = getFileList(username);
				System.out.println(list[0]);
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
				String password = tokens[2].trim();
				
				System.out.println(up.get(username));
				System.out.println(password);
				
				if(up.get(username).equals(password)) {
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
	
	//removes logged file from fileLog hashmap and updates the log text file
		public void logRemove(String username, String filename) {
			if(fileLog.containsKey(username)) {
				if(fileLog.get(username).containsKey(filename)) {
					fileLog.get(username).remove(filename);
					printlog();
				}
			}
		}
		
		public String[] getFileList(String username) {
			
			Set<String> set = fileLog.get(username).keySet();
			String[] fileList = new String[set.size()];
			
			int i = 0;
			for(String x : set) {
				fileList[i] = x;
				i++;
			}
			return fileList;
		}
		
		
		//#############################################################################################################
		// 										FILE LOG METHODS
		//adds the entered values into the fileLog hashmap with the keys being the username and filename
		public synchronized void logAdd(String username, String filename, String date, String path, String server) {
			
			String[] datePathServer = {date,path,server};
			
			//if key doesn't exist add it and make the HashMap for the value
		    if(!fileLog.containsKey(username)) {
		    	HashMap<String, String[]> innermap =  new HashMap<String, String[]>();
		    	innermap.put(filename, datePathServer);
		    	fileLog.put(username, innermap);
		    	
		    	try {
				    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", true)));
				    out.println(username + "::" + filename + "::" + date + "::" + path + "::" + server);
				    out.close();
				} catch (IOException e) {
				    e.printStackTrace();
				}
		    	
		    }else if(fileLog.get(username).containsKey(filename)) {
		    	
		    }else {
		    	fileLog.get(username).put(filename, datePathServer);
		    	try {
				    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", true)));
				    out.println(username + "::" + filename + "::" + date + "::" + path + "::" + server);
				    out.close();
				} catch (IOException e) {
				    e.printStackTrace();
				}
		    }	
		}
		
		
		//prints the contents of the fileLog hashmap to masterfiles.txt
		public synchronized void printlog() {
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", false)));
			  
			    for(Map.Entry<String, HashMap<String, String[]>> set1 : fileLog.entrySet()) {
			    	for(Map.Entry<String, String[]> set2 : set1.getValue().entrySet()) {
			    		out.println(set1.getKey() + " " + set2.getKey() + " " + set2.getValue()[0]+ " " + set2.getValue()[1]+ " " + set2.getValue()[2]);
			    	}
			    }
			
			    out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
					
			
		}
		
		//Prints the contents of the fileLog hashmap to the console
		public void PrintFileLog() {
			for(Map.Entry<String, HashMap<String, String[]>> set1 : fileLog.entrySet()) {
				for(Map.Entry<String, String[]> set2 : set1.getValue().entrySet()) {
					System.out.println(set1.getKey() + " " + set2.getKey() + " " + set2.getValue()[0]+ " " + set2.getValue()[1]+ " " + set2.getValue()[2]);
				}
			}
		}
		
		//#############################################################################################################
		//											DATANODELIST METHODS
		//adds an key value pair to DataNode (call when DataNode servers connect to Master)
		public void addDataNode(String dnName, String ip) {
			String[] ph = {ip, "0"};
			dataNodes.put(dnName, ph);
		}
		
		//returns an array of two keys with the least accesses from dataNodes
		public String[] getMinAccessedInput() {
			String[] min= new String[2];
			int min1=Integer.MAX_VALUE;
			String minOne = "";
			int min2=Integer.MAX_VALUE;
			String minTwo = "";
			for(Map.Entry<String, String[]> entry : dataNodes.entrySet()) {
				String[] ph = entry.getValue();
				int accesses= Integer.parseInt(ph[1]);
				if(accesses < min1) {
					min2 = min1;
					minTwo = minOne; 
					min1 = accesses;
					minOne = entry.getKey();
				}else if(accesses < min2) {
					min2 = accesses;
					minTwo = entry.getKey();
				}
			}
			
			min[0] = minOne;
			min[1] = minTwo;
			return min;
		}
		
		//given a dataNodes key increment the access and save it back into the key value pair
		public synchronized void incrementAccesss(String dnName) {
			String[] value = dataNodes.get(dnName);
			int accesses = Integer.parseInt(value[1]);
			accesses++;
			value[1] = accesses + "";
			dataNodes.put(dnName, value);
		}
		
		//call if unable connect to a DataNode
		public void removeDataNode(String dnName) {
			dataNodes.remove(dnName);
		}
		
		public int getSize() {
			return dataNodes.size();
		}
		//#############################################################################################################
		// 													USER PASS METHODS
		
		public void insert(String user, String pass) {
			up.put(user, pass);
		}
		
		public  void readfile(String filename) {
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
		
		public synchronized void addtofile(String user, String pass) {
			try {
				String password = encryptPass(pass); 
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("password.txt", true)));
			    out.println(user + ":" + password);
			    out.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
		
		public String encryptPass(String passWord) {

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
		
		//#############################################################################################################
		
		

}