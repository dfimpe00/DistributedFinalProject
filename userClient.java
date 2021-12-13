/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author fimpe
 */
public class userClient extends Thread {

    public final static int SOCKET_PORT = 52005;
    public final static String SERVER = "localhost";

    public Socket client;

    public userClient(Socket client) {
        this.client = client;
    }

    public static void main(String[] args) {

    }

    public static String acceptMessage() throws IOException, ClassNotFoundException {

        ObjectInputStream ois = null;
        String host = "192.168.7.147";
        int port = 52005;
        String message = "";
        
        try {

            Socket socket = new Socket(host, port);
            InputStream in = socket.getInputStream();
            //ois = new ObjectInputStream(socket.getInputStream());
            DataInputStream din = new DataInputStream(in);
            //message = (String) ois.readObject();
            message = din.readUTF();
            System.out.println("Message Received: " + message);
            
            System.out.println("Sending string to the ServerSocket");
            //ois.close();
            din.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    public static String sendEncrypt(String user, String pass) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        StringBuffer output = new StringBuffer();
        BufferedWriter writer = null;
        ObjectInputStream ois = null;
        String host = "192.168.7.147";
        int port = 52005;

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));

            for (int i = 0; i < hash.length; i++) {

                String hex = Integer.toHexString(0xff & hash[i]);

                if (hex.length() == 1) {

                    output.append('0');
                }

                output.append(hex);
            }

            Socket socket = new Socket(host, port);
            InputStream in = socket.getInputStream();
            DataInputStream din = new DataInputStream(in);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write("5," + user + "," + output.toString());
            writer.flush();
            
    
            System.out.println("Sending Message........");
            
            String input = din.readUTF();
            System.out.println(input);
            writer.close();
            in.close();
            din.close();
            return input;
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return "2";

    }

    public static void sendDeleteFileName(String userLogin, String FILE_TO_SEND) {

        String host = "192.168.7.147";
        int port = 52005;

        try {
            Socket socket = new Socket(host, port);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write("4," + userLogin + "," + FILE_TO_SEND);
            writer.close();

            System.out.println("Sending Message........");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendFileName(String FILE_TO_SEND) {

        String host = "192.168.7.147";
        int port = 52005;

        try {
            Socket socket = new Socket(host, port);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write("3," + FILE_TO_SEND);
            writer.close();

            System.out.println("Sending Message........");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendFiletoMaster(String userLogin, String PATH, String FILE_TO_SEND) throws IOException {

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        BufferedWriter writer = null;

        Socket sock = null;
        try {
            sock = new Socket(SERVER, SOCKET_PORT);
            System.out.println("Connecting...");

            writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

            writer.write("2," + userLogin + ",");
            writer.flush();

            // send file
            File myFile = new File(PATH + FILE_TO_SEND);
            byte[] mybytearray = new byte[(int) myFile.length()];

            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);

            bis.read(mybytearray, 0, mybytearray.length);
            os = sock.getOutputStream();

            System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            System.out.println("Done.");

        } finally {
            if (bis != null) {
                bis.close();
            }
            if (os != null) {
                os.close();
            }
            if (sock != null) {
                sock.close();
            }

        }
    }
    
    public static String[] retrieveFiles(String username) {
    	 String host = "192.168.7.147";
         int port = 52005;
         String message = null;
         String[] fileList = null;
         try {
        	 Socket socket = new Socket(host, port);
             InputStream in = socket.getInputStream();
             DataInputStream din = new DataInputStream(in);
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

             writer.write("3," + username);
             writer.flush();
             ObjectInputStream ois;

            // ois = new ObjectInputStream(socket.getInputStream());
             
             //message =  (String) ois.readObject();
             //System.out.println("Message Recieved: " + message);
             String input = din.readUTF();
             System.out.println(input);
             fileList = input.split(",");
             //ois.close();
             writer.close();
         } catch (Exception ex) {
             ex.printStackTrace();
         }
         
         
         return fileList;
    }

    /*
	 * 1. open a socket 2. open some stream to the socket 3. read data or write
     */
}
