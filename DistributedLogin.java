import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class DistributedLogin {

	public static Map<String, String> map = new HashMap<String, String>();

	public static void main(String[] args) {

		System.out.println(readFile("password.txt"));
		System.out.println(map.get("dax30"));
		System.out.println(encryptPass(map.get("dax30")));
		System.out.println(encryptPass(map.get("daegan30")));
		System.out.println(map.get("christopher500"));
		System.out.println(checkPass("daegan30", "passwerd4000"));
	}

	public static void writeToFile(String fileName, String username, String passWord) {

		try {

			FileWriter fw = new FileWriter(fileName, true);
			BufferedWriter bw = new BufferedWriter(fw);

			String line = username + ":" + encryptPass(passWord);

			bw.newLine();
			bw.write(line);

			bw.close();
			fw.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static Map<String, String> readFile(String filename) {

		try {

			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			String line = "";
			String[] tokens = null;

			while ((line = br.readLine()) != null) {

				String[] parts = line.split(":");

				String name = parts[0].trim();
				String passWord = parts[1].trim();

				// put name, number in HashMap if they are
				// not empty
				if (!name.equals("") && !passWord.equals("")) {

					map.put(name, passWord);
				}
			}

			br.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return map;
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

	public static boolean checkPass(String username, String passWord) {

		readFile("password.txt");
		String pass = readFile("password.txt").get(username);

		String userpass = encryptPass(pass);

		if (userpass.equals(passWord)) {

			return true;
		}

		return false;
	}
}
