import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileLogging {
	
	public static HashMap<String, HashMap<String, String[]>> fileLog = new HashMap<String, HashMap<String, String[]>>(); //Key : username value:hashmap<String, String[]> key: filename value [0]date [1]path [2]server

	
	
	public static String[] getFileList(String username) {
		
		Set<String> set = fileLog.get(username).keySet();
		String[] fileList = new String[set.size()];
		
		int i = 0;
		for(String x : set) {
			fileList[i] = x;
			i++;
		}
		return fileList;
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
	
	
	//adds the entered values into the fileLog hashmap with the keys being the username and filename
	public static synchronized void logAdd(String username, String filename, String date, String path, String server) {
		
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
	public static synchronized void printlog() {
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
	public static void PrintFileLog() {
		for(Map.Entry<String, HashMap<String, String[]>> set1 : fileLog.entrySet()) {
			for(Map.Entry<String, String[]> set2 : set1.getValue().entrySet()) {
				System.out.println(set1.getKey() + " " + set2.getKey() + " " + set2.getValue()[0]+ " " + set2.getValue()[1]+ " " + set2.getValue()[2]);
			}
		}
	}
	
	//removes logged file from fileLog hashmap and updates the log text file
	public static void logRemove(String username, String filename) {
		if(fileLog.containsKey(username)) {
			if(fileLog.get(username).containsKey(filename)) {
				fileLog.get(username).remove(filename);
				printlog();
			}
		}
		
		
	}

}
