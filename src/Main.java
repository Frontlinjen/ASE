public class Main{

	public static void main(String[] args) {
		final String IPAdresses[] = {
				"168.1.1.23",
				"localhost",	
		};
		for (String string : IPAdresses) {
			new Thread(new Runner(string)).start();	
		}
	}

}
