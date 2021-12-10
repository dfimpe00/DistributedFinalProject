import java.util.HashMap;
import java.util.Map;

public class DataNodeList {
	
	
	public static HashMap<String, String[]> dataNodes = new HashMap<String, String[]>(); // Key = DataNode Name values [0]:ip, [1]:accesses


	
	//adds an key value pair to DataNode (call when DataNode servers connect to Master)
	public static void addDataNode(String dnName, String ip) {
		String[] ph = {ip, "0"};
		dataNodes.put(dnName, ph);
	}
	
	//returns an array of two keys with the least accesses from dataNodes
	public static String[] getMinAccessedInput() {
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
	public static synchronized void incrementAccesss(String dnName) {
		String[] value = dataNodes.get(dnName);
		int accesses = Integer.parseInt(value[1]);
		accesses++;
		value[1] = accesses + "";
		dataNodes.put(dnName, value);
	}
	
	//call if unable connect to a DataNode
	public static void removeDataNode(String dnName) {
		dataNodes.remove(dnName);
	}
	
	public static int getSize() {
		return dataNodes.size();
	}
}
