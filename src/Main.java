import java.io.IOException;
import java.net.Socket;

public class Main{

	public static void main(String[] args) {
		try {
			ConnectionHandler connector = new ConnectionHandler(0); //portnummer?
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		new Thread(new Runner()).start();
	}

}
