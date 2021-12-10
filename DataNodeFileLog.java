import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class DataNodeFileLog {
	
	private static HashMap<String, HashMap<String, String[]>> fileLog = new HashMap<String, HashMap<String, String[]>>();

	
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

	
	//adds the entered values into the fileLog hashmap with the keys being the username and filename
	public static synchronized void logInsert(String username, String filename, String date, String path, String size, String accessnum) {
			
			String[] datePathServer = {date,path,size, accessnum};
			
			//if key doesn't exist add it and make the HashMap for the value
		    if(!fileLog.containsKey(username)) {
		    	HashMap<String, String[]> innermap =  new HashMap<String, String[]>();
		    	innermap.put(filename, datePathServer);
		    	fileLog.put(username, innermap);
		    	
		    	try {
				    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", true)));
				    out.println(username + "::" + filename + "::" + date + "::" + path + "::" + size+"::" + accessnum);
				    out.close();
				} catch (IOException e) {
				    e.printStackTrace();
				}
		    	
		    }else if(fileLog.get(username).containsKey(filename)) {
		    	
		    }else {
		    	fileLog.get(username).put(filename, datePathServer);
		    	try {
				    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("masterfiles.txt", true)));
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