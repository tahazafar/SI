import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class PrivateKeyVerification {

	public static void main(String args[]) {
		
		if(args.length < 2) {
			System.out.println("Usage: AppName BinaryfilePath secretPhrase ");
			System.exit(1);
		}
		
		try {
			Cipher chipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			byte [] seed = args[1].getBytes();
			Key k = generateKey(seed);
			byte[] plainText;
			byte[] encryptedText;
			Path pFile = Paths.get(args[0]) ;
			byte[] privateKeyText;
			/* after generating the key from the secret passsword decrypt!*/
			encryptedText = ReadArquive(pFile);
			try {
				plainText = decrypt(k,chipher,encryptedText);
				/* plainText contains the private Key*/
//				String stringKey = new String(convertToString(plainText));
//				System.out.println("chiave privata"+":"+ stringKey);
			} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
					| UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static byte[] ReadArquive(Path pFile) {
	
		if(Files.exists(pFile) == false) {
			System.err.print("FILE DOESN'T EXIST, EXITING \n");
			System.exit(2);
		}
		
		try {
			byte[] fileBytes = Files.readAllBytes(pFile);
			return fileBytes;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}

	private static Key generateKey(byte[] seed) {
		// how do I use SHA1PRNG??
		
		try {
			KeyGenerator key = KeyGenerator.getInstance("DES");
			SecureRandom random;
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
				random.setSeed(seed);
				key.init(56,random);
				return key.generateKey();
				
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return null;
	}

	public static byte[] decrypt(Key k,  Cipher cipher,byte[] cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
	
		cipher.init(Cipher.DECRYPT_MODE, k);
		byte[] newPlainText = cipher.doFinal(cipherText);
		System.out.println( "Finish decryption: " );
		System.out.println( new String(newPlainText, "UTF8") );
		return newPlainText;
	}
	
//	public static String convertToString(byte[] fileBytes) {
//		String string = new String();
//		if(fileBytes != null) {
//			for(int i = 0; i < fileBytes.length; i++)
//				string = string + String.format("%02X", fileBytes[i]);
//		}
//		return string;
//	}
}
