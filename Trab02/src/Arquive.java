import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.lang.String;
import java.util.Map;

public class Arquive {

	
	String nome = new String();
	// contatins algorithm and associated digest in HEX
	LinkedHashMap<String , String>  digests= new LinkedHashMap<String, String>();
	String state;
	
	public Arquive(String[] line) 
	{
		// standard form there is no additional digest
		nome = line[0];
		state= "UNKNOW";
		if(line.length > 2) {
		digests.put(line[1],line[2]);
		// gestire la possibilita'di altri digest nel medesimo file
			for(int i=3; i< (line.length-1); i++) {
				digests.put(line[i],line[i+1]);
			}
		}else {
			state ="NOT FOUND";
		}
	}
	
	public Arquive(String name, String Digest, String Algo, String status) {
		nome = name;
		if(!digests.containsKey(Algo)) {
			digests.put(Algo, Digest);
		}
		state= new String(status);
	}
	
	public void PrintArquive() {
		System.out.print(nome+" "+state+" ");
		System.out.println(Arrays.asList(digests));
	}
	
	public void PrintArquive(String digType) {
		System.out.println(nome+" "+digType+" "+digests.get(digType)+" ("+state+")");
		
	}
	
	public String Check(String digest, String Algo, Arquive p) {
		String thisDigest ;
		
		if(nome.contentEquals(p.getNome())) { // same file
		
			if(digests.containsKey(Algo)) {
			thisDigest =digests.get(Algo);
				if(thisDigest.equals(digest)) {
					state = "OK";
				}else {
					state = "NOT_OK";
				}
			}else {
					state = "NOT_FOUND";
				}
	   }else { // different file

		   if(digests.containsKey(Algo)) {
				thisDigest =digests.get(Algo);
				String thatDigest = p.getDigest(Algo);
					if(thisDigest.equals(thatDigest)) {
						state = "COLLISION";
					}
		   }
	   }
		return state;
	}

	public String getNome() {
		
		return nome;
	}
	
	public void SetStatus(String S) {
		state = new String(S);
	}
	public String getStatus() {
		return state;
	}

	public String getDigest(String digestAlgorithm) {
		
		return digests.get(digestAlgorithm);
	}
	public void addDigest(String algo, String dig) {
		digests.put(algo,dig);
	}
}
 