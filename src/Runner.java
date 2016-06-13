import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Runner implements Runnable{

	private ScaleWrapper scale;
	private String ipAddress;
	private Socket socket;
	@Override
	public void run() {
		connectToScale();
		operatorIdentifier();
		afvejning();
	}
	
	public Runner(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	private void connectToScale()
	{
		do
		{
			try {
				socket = new Socket(ipAddress, 8000);
			} catch(UnknownHostException e) //Address was not found
			{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					System.out.println("Timed out too early.");
				}
			}catch (IOException e) 
			{
				System.out.println("Failed to create socket: " + e.getMessage());
			}
		}while(socket==null);
		
	}
	private void operatorIdentifier(){ //punkt 1-4
		
		MySQLAnsatDAO database = new MySQLAnsatDAO();
		String input = null;
		
		while(true){
			//Ask ScaleWrapper for id
			while(input == null){
				input = scale.getInput();
			}
			try {
				scale.pushDisplay("Hello " + database.getAnsat(input).getOprNavn() + ", please confirm (1: yes/2: no)");
				while(input == null){
					input = scale.getInput();
				}
				if(input == "1"){
					return;
				}
				else if(input == "2"){
					operatorIdentifier();
				}
				
			} catch (DALException e) {
				if(input.length() < 10){
					scale.pushDisplay("Id too short");
				}
				else if(input.length() > 10){
					scale.pushDisplay("Id too long");
				}
				else{
					scale.pushDisplay("User not found");
				}
			}
		}
	}
	
	private void afvejning(){ //punkt 5-16
		ScaleWrapper scale = new ScaleWrapper();
		MySQLProduktbatchDAO pbDAO = new MySQLProduktbatchDAO();
		MySQLReceptDAO rcDAO = new MySQLReceptDAO();
		String input = null;
		int produktBatchNr;
		
		scale.pushDisplay("Please state the product batch number:");
		while(input == null){
			input = scale.getInput();
		}
		produktBatchNr = Integer.parseInt(input);
		try{
		scale.pushDisplay(rcDAO.getRecept(pbDAO.getProduktBatch(produktBatchNr).getReceptId()).getReceptNavn() + ", please confirm the recipe (1: yes/2: no)");
		while(input == null){
			input = scale.getInput();
		}
		if(input == "1"){
			afvejningCore(produktBatchNr);
		}
		else if(input == "2"){
			afvejning();
		}
		}
		catch(DALException e){
			scale.pushDisplay("Failed to find recipe, please check product batch number");
			afvejning();
			//Annuller mulighed?
		}
	}
	
	private void afvejningCore(int produktBatch){
		ScaleWrapper scale = new ScaleWrapper();
		MySQLProduktBatchKompDAO pbkDAO = new MySQLProduktBatchKompDAO();
		MySQLProduktbatchDAO pbDAO = new MySQLProduktbatchDAO();
		String input = null;
		try{
			for(int i = 0; i < pbkDAO.getSpecifiedProduktBatchKompList(Integer.parseInt(input)).size()/*Lav en SpecifiedProduktBatchKomponentList(String); i DAO*/; i++){
				scale.pushDisplay("Please confirm that the ScaleWrapper is clear, press 1 to confirm");
				while(input != "1"){
					scale.getInput();
				}
				pbDAO.getProduktBatch(produktBatch).setStatus(1);
				scale.pushDisplay("AutoTara"); //Yes?
				scale.tara();
				scale.pushDisplay("Please place container on the ScaleWrapper and press 1 when ready");
				while(input != "1"){
					scale.getInput();
				}
				double containerWeight = scale.getWeight();
				scale.pushDisplay("AutoTara"); //Yes?
				scale.tara();
				scale.pushDisplay("Please weigh component with resourcebatch id = " + pbkDAO.getProduktBatchKompList().get(i).getRaavarebatchId() + " in the container and press 1 when ready");
				while(input != "1"){
					scale.getInput();
				}
				pbkDAO.getProduktBatchKompList().get(i).setTara(containerWeight);
				pbkDAO.getProduktBatchKompList().get(i).setNetto(scale.getWeight()-containerWeight);
			}
			pbDAO.getProduktBatch(produktBatch).setStatus(2);
		}
		catch(DALException e){
			scale.pushDisplay("Error happened, trying again...");
			afvejningCore(produktBatch);
		}
	}

}