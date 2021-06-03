import java.io.IOException;
import java.net.*;
import java.util.*;
import java.math.*;
import javax.crypto.spec.*;
import java.security.*;
import javax.crypto.*;

//Alice
public class Host {
	//http://esus.com/encryptingdecrypting-using-rc4/
	private static String algorithm = "RC4";
	private DatagramSocket socket;
	private DatagramPacket sP;
	private InetAddress address;
	private String key;
	private String pw;
	public static void main(String[] args) throws Exception {
		System.out.println("Waiting for connection...");
		Host h = new Host();
		h.run();
		   
	}
	
	public void run() throws Exception {
		
		
		socket = new DatagramSocket(1500);
	    DatagramPacket packet = new DatagramPacket(new byte[512],512);
	    
	    //To get password
	    String password = KeyGen.getPassword();
	    
	    //To get p
	    //BigInteger p = Gen.getP();
	    
	    //To get g
	    //BigInteger g = Gen.getG(p);
	    
	    Random r = new Random();
	    int low = 100;
	    int high = 999;
	    int result = r.nextInt(high-low) + low;
	    
	    BigInteger x = BigInteger.valueOf(result);
	    
	    BigInteger compute = g.modPow(x, p);
	    
	    String computeString = String.valueOf(compute);
	    
	    String toEncrypt = computeString;
	    byte[] encrypted = encrypt(toEncrypt, password);
	    String decrypted = decrypt(encrypted, password);
	    
	    //System.out.println(decrypted);
	   
		  packet = new DatagramPacket(new byte[512],512);
		  //Receive "test" string
	      socket.receive( packet );
	      System.out.println(new String(packet.getData()));
	      
	      //Send Public key to Bob
	      packet = new DatagramPacket(encrypted,encrypted.length,InetAddress.getByName("127.0.0.1"),1300);
	      socket.send( packet );
	      
	      //Receive public Key from Bob
	      packet = new DatagramPacket(new byte[512],512);
	      socket.receive( packet );
	      
	      byte[] data = trim(packet.getData());
	   
	      String decryptCipher = decrypt(data,password);
	      
	      BigInteger bigCipher = new BigInteger(decryptCipher);
	      //System.out.println("asdasd: " + bigCipher);
	       BigInteger sessionKeyBig = (bigCipher.pow(result)).mod(p); 
	       //System.out.println("qweqwe: "+sessionKeyBig);
	       String sessionKey = String.valueOf(sessionKeyBig);
	       //Alice Session Key
	       String hashedSessionKey = sha1(sessionKey);
	       key = hashedSessionKey;
	       pw = password;
		   InetAddress.getByName("127.0.0.1");
		   
		   System.out.println("Starting message connection");
		   StartThread();
		  
	}
	public void StartThread() throws UnknownHostException {
		 AliceThread a = new AliceThread(this);
		 Thread t = new Thread(a);
		 t.start();
		 
		 AliceSendThread a1 = new AliceSendThread(this);
		 Thread t1 = new Thread(a1);
		 t1.start();
		 
	}

	
	
	public static byte[] encrypt(String toEncrypt, String key) throws Exception {
	      // create a binary key from the argument key (seed)
	      SecureRandom sr = new SecureRandom(key.getBytes());
	      KeyGenerator kg = KeyGenerator.getInstance(algorithm);
	      kg.init(sr);
	      SecretKey sk = kg.generateKey();
	  
	      // create an instance of cipher
	      Cipher cipher = Cipher.getInstance(algorithm);
	  
	      // initialize the cipher with the key
	      cipher.init(Cipher.ENCRYPT_MODE, sk);
	  
	      // encrypt
	      byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
	  
	      return encrypted;
	   }
	  
	   public static String decrypt(byte[] toDecrypt, String key) throws Exception {
	      // create a binary key from the argument key (seed)
	      SecureRandom sr = new SecureRandom(key.getBytes());
	      KeyGenerator kg = KeyGenerator.getInstance(algorithm);
	      kg.init(sr);
	      SecretKey sk = kg.generateKey();
	  
	      // do the decryption with that key
	      Cipher cipher = Cipher.getInstance(algorithm);
	      cipher.init(Cipher.DECRYPT_MODE, sk);
	      byte[] decrypted = cipher.doFinal(toDecrypt);
	  
	      return new String(decrypted);
	   }
	   static byte[] trim(byte[] bytes)
		{
		    int i = bytes.length - 1;
		    while (i >= 0 && bytes[i] == 0)
		    {
		        --i;
		    }

		    return Arrays.copyOf(bytes, i + 1);
		}
	   static String sha1(String input) throws NoSuchAlgorithmException {
	       MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	       byte[] result = mDigest.digest(input.getBytes());
	       StringBuffer sb = new StringBuffer();
	       for (int i = 0; i < result.length; i++) {
	           sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	       }
	        
	       return sb.toString();
	   }
	   
	   public class AliceThread implements Runnable{
		   
		   private Host h;
		   private DatagramPacket dPacket;
		   private String message;
		   public AliceThread(Host host) {
			   h = host;
		   }
		   
		   public void run() {
			   try {
				   while(true) {
					   
					   dPacket = new DatagramPacket(new byte[512],512);
					   h.socket.receive(dPacket);
					   
					   byte[] data = trim(dPacket.getData());
					   String decryptCipher = decrypt(data,pw);
					   
					   //System.out.println(decryptCipher);
					   
					   String hash = decryptCipher.substring(decryptCipher.length()-40);
					   //System.out.println(hash);
					   
					   String message = decryptCipher.substring(0,decryptCipher.length()-40);
					   //System.out.println(message);
					   
					   
					   String hashPrime = sha1(key+message);
					   
					   if(hashPrime.equals(hash)) {
						   System.out.println("Message Accepted");
						   System.out.println(message);
						   if(message.equalsIgnoreCase("exit")) {
							   System.out.println("Exiting...");
							   System.exit(0);
						   }
						   System.out.println("Enter Your Message: ");
					   }else {
						   System.out.println("Hash different, message declined");
						   System.exit(0);
					   }
					   
					   
				   }
			   }catch(Exception e) {}
			   
		   }
	   }
	   
public class AliceSendThread implements Runnable{
		   
		   private Host h;
		   private String s1;
		   private byte[] arr;
		   private Scanner s = new Scanner (System.in);
		   private InetAddress address;
		   public AliceSendThread(Host host) throws UnknownHostException {
			   h = host;
			   address = address.getByName("127.0.0.1");
		   }
		   
		   public void run() {
			   try {
				   while(true) {
					   //System.out.println(key);
						  System.out.println("Enter Your Message: ");
						  s1 = s.nextLine();
						  
						  String hash = sha1(key+s1);
						  byte[] encrypted = encrypt(s1+hash, pw);
						  
						  sP = new DatagramPacket(encrypted, encrypted.length, address, 1300);
						  socket.send(sP);
						  
						  if(s1.equalsIgnoreCase("exit")) {
							  System.out.println("Exiting...");
							  System.exit(0);
						  }
					   }
			   }catch(Exception e) {}
			   
		   }
	   }

}
