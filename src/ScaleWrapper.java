
public class ScaleWrapper {

	public String waitForInput(String ask, String expected) {
		String send = "RM20 8 \"" + ask + "\" \"\" \"" + expected + "\" crlf";
		return null; //return response
	}

	public void pushDisplay(String message) {
		CommandParser parser = new CommandParser("D \"" + message + "\" crlf");
	}

	public double tara() {
		CommandParser parser = new CommandParser("T crlf"); //Skal returnere tara-værdien?
		return null; //return tara response
	}

	public void clearDisplay(){
		CommandParser parser = new CommandParser("DW crlf");
	}
	
	public double getWeight() {
		CommandParser parser = new CommandParser("S crlf");
		return null; //return the weight
	}

}
