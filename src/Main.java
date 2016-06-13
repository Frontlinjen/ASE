public class Main{

	public static void main(String[] args) {
		final String IPAdresses[] = {
				"169.254.2.2",
				//"localhost",	
		};
		for (String string : IPAdresses) {
			new Thread(new Runner(string)).start();	
		}
	}

}
