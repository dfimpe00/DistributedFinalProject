import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class NodeThread extends Thread {
	
	
	static HashMap<String, HashMap<String, String[]>> fileLog = new HashMap<String, HashMap<String, String[]>>();
	Socket s;
	int bytesRead;
	public static int FILE_SIZE = 6022386;

	public NodeThread(Socket s, HashMap<String, HashMap<String, String[]>> fileLog) {
		this.s = s;
		this.fileLog = fileLog;
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

			if (tokens[0].equals("2")) {
				// file insert

				String username = tokens[1];
				String filename = tokens[2];
				String contents = tokens[3];
				File dir = new File(username + "/");
				logInsert(username, filename, LocalDateTime.now() +"", username+"/", contents.length() + "", "0");

				if (dir.isDirectory()) {
					File f = new File(username + "/" + filename);
					FileWriter fr = new FileWriter(f);
					fr.write(tokens[3]);
				} else {
					System.out.println("making a dir");
					dir.mkdir();
					File f = new File(username + "/" + filename);
					FileWriter fr = new FileWriter(f);
					fr.write(tokens[3]);
				}

			} else if (tokens[0].equals("4")) {
				// file delete
				String username = tokens[1];
				String filename = tokens[2].trim();

				File dir = new File(username + "/");

				if (dir.isDirectory()) {

					Files.deleteIfExists(Paths.get(username + "/" + filename));
					System.out.println("File: " + username + "/" + filename + " was successfully deleted");
					logDelete(username, filename);
				}
			} else if (tokens[0].equals("6")) {
				// file retrieve
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//adds the entered values into the fileLog hashmap with the keys being the username and filename
		public static synchronized void logInsert(String username, String filename, String date, String path, String size, String accessnum) {
				
				String[] datePathServer = {date,path,size, accessnum};
				
				//if key doesn't exist add it and make the HashMap for the value
			    if(!fileLog.containsKey(username)) {
			    	HashMap<String, String[]> innermap =  new HashMap<String, String[]>();
			    	innermap.put(filename, datePathServer);
			    	fileLog.put(username, innermap);
			    	
			    	try {
					    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("datafiles.txt", true)));
					    out.println(username + "::" + filename + "::" + date + "::" + path + "::" + size+"::" + accessnum);
					    out.close();
					} catch (IOException e) {
					    e.printStackTrace();
					}
			    	
			    }else if(fileLog.get(username).containsKey(filename)) {
			    	
			    }else {
			    	fileLog.get(username).put(filename, datePathServer);
			    	try {
					    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("datafiles.txt", true)));
					    out.println(username + "::" + filename + "::" + date + "::" + path + "::" + size+"::" + accessnum);
					    out.close();
					} catch (IOException e) {
					    e.printStackTrace();
					}
			    }	
			}
		
		public static void logDelete(String username, String filename) {
			if(fileLog.containsKey(username)) {
				if(fileLog.get(username).containsKey(filename)) {
					fileLog.get(username).remove(filename);
					printlog();
				}
			}
		
	}

		
		//prints the contents of the fileLog hashmap to datafiles.txt
		public static synchronized void printlog() {
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("datafiles.txt", false)));
			  
			    for(Map.Entry<String, HashMap<String, String[]>> set1 : fileLog.entrySet()) {
			    	for(Map.Entry<String, String[]> set2 : set1.getValue().entrySet()) {
			    		out.println(set1.getKey() + " " + set2.getKey() + " " + set2.getValue()[0]+ " " + set2.getValue()[1]+ " " + set2.getValue()[2] + " " + set2.getValue()[3]);
			    	}
			    }
			
			    out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
					
			
		}
		
		//adds one to the number of time accessed and then rewrites the datafiles.txt
		public static synchronized void accessedFile(String user, String file) {
			String[] hashvalue = fileLog.get(user).get(file);
			int numAccessed = Integer.parseInt(hashvalue[5]);
			numAccessed += 1;
			hashvalue[5] = numAccessed + ""; 
			fileLog.get(user).put(file, hashvalue);
			printlog();
		}
		
}