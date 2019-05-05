import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


public class DigestCalculator {
	private static LinkedHashMap <String, Arquive > arquives = new LinkedHashMap<String, Arquive>();
	private static String digestAlgorithm;
public static void main(String[] args) throws Exception {
		
		if (args.length < 3) {
		      System.err.println("Usage: java DigestCalculator Tipo_Digest Caminho_ArqListaDigest Caminho_Arq1... Caminho_ArqN");
		      System.exit(1);
		    }
		Path filePath;
		digestAlgorithm = args[0];
//		String fileName = new String();
		File inFile = new File(args[1]);
		ReadArquivList(inFile);
		System.out.println("........");
		for(int i=2;i<args.length;i++) {
			filePath = Paths.get(args[i]);
			//System.out.println(filePath.toString());
			ReadArquive(filePath);
		}
		System.out.println("........");
		writeArquiveList(inFile);
	}
	


	private static void writeArquiveList(File outFile) {
		String state = new String();
		String line ;
		Arquive q;
		try {
			FileWriter fw = new FileWriter(outFile);
			for(Entry<String, Arquive> entry : arquives.entrySet()) {
				q = entry.getValue();
				state = q.getStatus();
				if(state.contentEquals("OK")) {
					
				line=entry.getKey()+" ";
				if(q.getDigest("SHA1") != null) {
					line = line +" SHA1 "+q.getDigest("SHA1");
				}
				if(q.getDigest("MD5") != null) {
					line = line +" MD5 "+q.getDigest("MD5");
				}
					fw.write(line);
				}
			}
			fw.close();
			
		}catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + 
                    outFile + "'");                
            }
            catch(IOException ex) {
                System.out.println(
                    "Error writing file '" 
                    + outFile + "'");                  
                // Or we could just do this: 
                // ex.printStackTrace();
            }
		
			
		}
	



	private static void ReadArquive(Path pFile) throws IOException {
		if(Files.exists(pFile) == false) {
			System.err.print("FILE DOESN'T EXIST, EXITING \n");
			System.exit(2);
		}
		
		byte[] fileBytes = Files.readAllBytes(pFile);
		
		//messageDigest.update(fileBytes, 0, fileBytes.length);
		fileBytes = computeDigest(fileBytes);
		String digestComputed= new String(convertToString(fileBytes));
		String fileName = pFile.getFileName().toString();
		
		
		 CheckCollision(digestComputed,fileName);
		
		System.out.println("File: "+fileName+" digest_computed:"+digestComputed+" fileArq: "+arquives.get(fileName).getDigest(digestAlgorithm));
		
	}
	



   // check collision and print digest status;
	private static void CheckCollision(String digestComputed, String fileName) {
		Arquive q ;
		boolean present = false;
		boolean updated = false;
		Arquive p;
		String state = new String();
		if(arquives.containsKey(fileName)) { // the element is present
			q = arquives.get(fileName);
			if(q.getDigest(digestAlgorithm)!= null) {
				present = true;
			}else { // the element was found but the specific digest no
				q.SetStatus("NOT FOUND");
				q.addDigest(digestAlgorithm, digestComputed);
				q.PrintArquive(digestAlgorithm);
				updated = true;
			}

		}else {
			q = new Arquive(fileName, digestComputed, digestAlgorithm, "NOT FOUND");
			q.PrintArquive(digestAlgorithm);
			
		}
		// check collision in order to write just the correct digest in the files
		for(Entry<String, Arquive> entry : arquives.entrySet()) {
			if(!entry.getKey().contentEquals(fileName)) {
				p = entry.getValue();
				state =q.Check(digestComputed, digestAlgorithm, p);
				// possible improvment
			}
		}
		if(!state.equals("COLLISION")) {
			state = q.Check(digestComputed,digestAlgorithm,q);
			if(!present && !updated) {
				// se non ci sono state collisioni inserisco il nuovo dato nella lista di digest
				// con un nuovo stato
				arquives.put(fileName, q);
			}
		}
		
		if(present && !updated)
			q.PrintArquive(digestAlgorithm);
		
			
		
		return ;
	}



	private static String convertToString(byte[] fileBytes) {
		String digest = new String();
		if(fileBytes != null) {
			for(int i = 0; i < fileBytes.length; i++)
				digest = digest + String.format("%02X", fileBytes[i]);
		}
		return digest;
	}



	private static byte[] computeDigest( byte[] fileBytes) {
		byte[] digest = null;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(digestAlgorithm);
			System.out.println("Digest's Algorithm: " + digestAlgorithm);
			System.out.println("Provider: " + md.getProvider());
			md.update(fileBytes, 0, fileBytes.length);
			digest = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return digest;
	}



	private static void ReadArquivList(File inFile) {
		String line = null;
		Arquive q;
		int i= 0;
		String textFileName; 
		String tokens[];
		try {
			FileReader fileReader = new FileReader(inFile);
			
			BufferedReader bufferedReader = 
	                new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
          tokens = line.split(" ");
          textFileName = tokens[0];
          q = new Arquive(tokens);
          arquives.put(textFileName, q);
            }   
			

            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                inFile + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + inFile + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
	}

}

