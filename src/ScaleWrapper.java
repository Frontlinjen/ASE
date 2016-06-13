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
		logger = new PrintWriter(System.out);
		this.con = con;
		out = new PrintWriter(con.getOutputStream());
		in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String identifier = in.readLine();
		System.out.println(identifier);
		pushShortDisplay("Welcome");
	}
	public void sendString(String s, String... args)
	{
		String command = String.format(s,  args) + "\r\n";
		logger.write("Sending command: " + command + "\n");
		logger.flush();
		out.write(command);
		out.flush();
	}
	//MAX 24 CHARACTERS
	public String waitForInput(String ask, int type, String display, String unit) {
		display = display == null ? "" : display;
		unit = unit == null ? "" : unit;
		sendString("RM20 %s \"%s\" \"%s\" \"%s\"", Integer.toString(type), ask, display, unit);
		try {
			String result = in.readLine();
			System.out.println(result);
			switch(result)
			{
				case "RM20 B":
				{
					logger.write("Waiting for input from weight.\n");
					break;
				}
				case "RM20 I":
				{
					logger.write("Got RM20 I. Scale busy\n");
					return null;
				}
				case "RM20 L":
				{
					logger.write("Wrong arguments\n");
					return null;
				}
				default:{
					logger.write("Other error: " + result + "\n");
				}
			}
			String[] response = new CommandParser(in.readLine()).getTokens();
			System.out.println(String.join(",", response));
			if(response[0].equals("RM20"))
			{
				if(response[1].equals("A"))
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

	public void pushShortDisplay(String message) { //MAX 6/7 CHARACTERS
		sendString("D \"%s\"", message);
		try {
			switch(in.readLine())
			{
				case "D A":
				{
					logger.write("Successfully displayed message\n");
					break;
				}
				case "D I":
				{
					logger.write("Command not executable\n");
				}
				case "D L":
				{
					logger.write("Wrong parametre\n");
				}
				default:{
					logger.write("Other error\n");
				}
			}
		} catch (IOException e) {
			logger.write("Failed to write to socket" + e.getMessage() + "\n");
		}
	}

	public double tara() {
		sendString("T");
		try{
			String[] tokens = new CommandParser(in.readLine()).getTokens();
			switch(tokens[1]){
			case "S":{
				logger.write("Tared successfully, tare value:" + tokens[2] + tokens[3] + "\n");
				return Double.parseDouble(tokens[2]);
			}
			case "I":{
				logger.write("Tare not executed, trying again\n");
				Thread.sleep(2000);
				return tara();
			}
			case "+":{
				logger.write("Upper tare limit exceeded\n");
			}
			case "-":{
				logger.write("Lower tare limit exceeded\n");
			}
			default:{
				logger.write("Other error\n");
			}
			}
		}
		catch(IOException e){
			logger.write("Failed to write to socket" + e.getMessage() + "\n");
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
					logger.write("Displaying weight mode\n");
				}
				case "DW I":{
					logger.write("Unable to execute\n");
				}
				default:{
					logger.write("Other error\n");
				}
			}
		} catch (IOException e) {
			logger.write("Failed to write to socket" + e.getMessage() + "\n");
		}
	}
	
	public double getWeight() {
		sendString("S");
		try{
			String[] tokens = new CommandParser(in.readLine()).getTokens();
			switch(tokens[1]){
			case "S":{
				logger.write("Weighing successful, weight:" + tokens[2] + tokens[3] + "\n");
				return Double.parseDouble(tokens[2]);
			}
			case "I":{
				logger.write("Weighing not executed, trying again\n");
				Thread.sleep(2000);
				return getWeight();
			}
			case "+":{
				logger.write("Upper weight limit exceeded\n");
			}
			case "-":{
				logger.write("Lower weight limit exceeded\n");
			}
			default:{
				logger.write("Other error\n");
			}
			}
		}
		catch(IOException e){
			logger.write("Failed to write to socket" + e.getMessage() + "\n");
		} catch (InterruptedException e) {
			e.printStackTrace(logger);
		}
		return Double.NaN; //return the weight
	}
	
	public void pushLongDisplay(String message) {
		sendString("P111 \"%s\"", message);
		try {
			switch(in.readLine())
			{
				case "P111 A":
				{
					logger.write("Successfully displayed message\n");
					break;
				}
				case "P111 I":
				{
					logger.write("Command not executable\n");
				}
				case "P111 L":
				{
					logger.write("Wrong parametre\n");
				}
				default:{
					logger.write("Other error\n");
				}
			}
		} catch (IOException e) {
			logger.write("Failed to write to socket" + e.getMessage() + "\n");
		}
	}
}
