import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ScaleWrapper {

	Socket con;
	PrintWriter out;
	BufferedReader in;
	PrintWriter logger;
	public ScaleWrapper(Socket con) throws IOException
	{
		logger = new PrintWriter(Thread.currentThread().getName() + " " + con.getInetAddress().getHostAddress() + ".txt");
		this.con = con;
		out = new PrintWriter(con.getOutputStream());
		in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	}
	public void sendString(String s, String... args)
	{
		String command = String.format(s,  args) + "\r\n";
		logger.write("Sending command: " + command);
		out.write(command);
	}
	
	public String waitForInput(String ask, String display, String unit) {
		sendString("RM20 8 \"%s\" \"%s\" \"%s\"", ask, display, unit);
		try {
			switch(in.readLine())
			{
				case "RM20 B":
				{
					logger.write("Waiting for input from weight.");
					break;
				}
				case "RM20 I":
				{
					logger.write("Got RM20 I. Are there two connections to the same weight? Aborting...");
					return null;
				}
				case "RM20 L":
				{
					logger.write("Wrong arguments");
					return null;
				}
			}
			String[] response = new CommandParser(in.readLine()).getTokens();
			if(response[0] == "RM20")
			{
				if(response[1] == "A")
				{
					return response[2];
				}
				else
				{
					return null;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace(logger);
		}
		
		return null;
	}

	public void pushDisplay(String message) {
		sendString("D \"%s\"", message);
		try {
			switch(in.readLine())
			{
				case "D A":
				{
					logger.write("Successfully displayed message");
					break;
				}
				case "D I":
				{
					logger.write("Command not executable");
				}
				case "D L":
				{
					logger.write("Wrong parametre");
				}
			}
		} catch (IOException e) {
			logger.write("Failed to write to socket" + e.getMessage());
		}
	}

	public double tara() {
		sendString("T");
		try{
			String[] tokens = new CommandParser(in.readLine()).getTokens();
			switch(tokens[1]){
			case "S":{
				logger.write("Tared successfully, tare value:" + tokens[2] + tokens[3]);
				return Double.parseDouble(tokens[2]);
			}
			case "I":{
				logger.write("Tare not executed, trying again");
				Thread.sleep(2000);
				return tara();
			}
			case "+":{
				logger.write("Upper tare limit exceeded");
			}
			case "-":{
				logger.write("Lower tare limit exceeded");
			}
			}
		}
		catch(IOException e){
			logger.write("Failed to write to socket" + e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace(logger);
		}
		return Double.NaN;
	}

	public void displayShowWeight(){
		sendString("DW");
		try {
			switch(in.readLine())
			{
				case "DW A":{
					logger.write("Displaying weight mode");
				}
				case "DW I":{
					logger.write("Unable to execute");
				}
			}
		} catch (IOException e) {
			logger.write("Failed to write to socket" + e.getMessage());
		}
	}
	
	public double getWeight() {
		sendString("S");
		try{
			String[] tokens = new CommandParser(in.readLine()).getTokens();
			switch(tokens[1]){
			case "S":{
				logger.write("Weighing successful, weight:" + tokens[2] + tokens[3]);
				return Double.parseDouble(tokens[2]);
			}
			case "I":{
				logger.write("Weighing not executed, trying again");
				Thread.sleep(2000);
				return getWeight();
			}
			case "+":{
				logger.write("Upper weight limit exceeded");
			}
			case "-":{
				logger.write("Lower weight limit exceeded");
			}
			}
		}
		catch(IOException e){
			logger.write("Failed to write to socket" + e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace(logger);
		}
		return Double.NaN; //return the weight
	}

}
