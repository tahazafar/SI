import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;

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
			Key k = generateSecretKey(seed);
			byte[] base64Text;
			byte[] encryptedText;
			Path pFile = Paths.get(args[0]) ;
			String privateKey64encoded;
			byte[] pK64decoded;
			/* after generating the key from the secret passsword decrypt!*/
			encryptedText = ReadArquive(pFile);
			try {
				base64Text = decryptDES(k,chipher,encryptedText);
				/* plainText contains the private Key and some attidional phrase*/
				privateKey64encoded = parsePrivateKey(base64Text);
				/* now we have the 64BASE encoded String of the key*/
				pK64decoded = Base64.getDecoder().decode(privateKey64encoded);
				
				PKCS8EncodedKeySpec Keyspec = new PKCS8EncodedKeySpec(pK64decoded);
				/*RSA is the standard for Asymmetric Key*/
				KeyFactory keyF = KeyFactory.getInstance("RSA");
				try {
					PrivateKey privateKey = keyF.generatePrivate(Keyspec);
					
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
					| UnsupportedEncodingException e) {
				
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			
			e.printStackTrace();
		}
	}
	
	/* it removes BEGIN key and --END KEY--*/
	private static String parsePrivateKey(byte[] plainText) {
		try {
			String decrypted = new String(plainText, "UTF8" );
			int i = 0;
			String[] parts = decrypted.split("\n"); 
			StringBuffer sb = new StringBuffer();
			for(String s: parts) {
				if(i == 0 || i ==( parts.length-1)) {
					
				}else {
					sb.append(s+"\n");
				}
				i++;
			}
		//	System.out.println(sb);
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
		
			e.printStackTrace();
		}
		
		return null;
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

	private static Key generateSecretKey(byte[] seed) {
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

	public static byte[] decryptDES(Key k,  Cipher cipher,byte[] cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
	
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
