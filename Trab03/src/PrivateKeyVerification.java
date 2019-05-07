import java.io.UnsupportedEncodingException;
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
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public byte[] decrypt(Key k,  Cipher cipher,byte[] cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
	
		cipher.init(Cipher.DECRYPT_MODE, k);
		byte[] newPlainText = cipher.doFinal(cipherText);
		System.out.println( "Finish decryption: " );
		System.out.println( new String(newPlainText, "UTF8") );
		return newPlainText;
	}
}
