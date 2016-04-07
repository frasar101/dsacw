import sss.Facade;
import sss.config.Algorithms;
import sss.config.Encryptors;
import sss.config.RandomSources;
import sss.crypto.data.Share;

public class SSS_Client {

	public static void main(String[] args) {
		int n = 5, t = 3;
		RandomSources r = RandomSources.SHA1;
		Encryptors e = Encryptors.ChaCha20;
		Algorithms a = Algorithms.CSS;
		try {
			Facade f = new Facade(n, t, r, e, a);
			String secret = "SET10106 DSA Coursework";
			byte[] bytes = secret.getBytes();
			Share[] shares = f.split(bytes);

			bytes = f.join(shares);
			String output = new String(bytes); 
			System.out.println(output);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
