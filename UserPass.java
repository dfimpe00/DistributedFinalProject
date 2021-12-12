import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class UserPass {
	public static HashMap<String, String> up = new HashMap<String, String>();
	
	public static void insert(String user, String pass) {
		up.put(user, pass);
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
	
	public static synchronized void addtofile(String user, String pass) {
		try {
			String password = encryptPass(pass); 
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("password.txt", true)));
		    out.println(user + ":" + password);
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public static String encryptPass(String passWord) {

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
}
