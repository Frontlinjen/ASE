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
		
		while(true){
			//Ask ScaleWrapper for id
			String id = scale.waitForInput("Please insert your Operator ID:", null, null);
			try {
				scale.pushDisplay("Hello " + database.getAnsat(id).getOprNavn() + ", please confirm (1: yes/2: no)");
				String input = scale.waitForInput("Please confirm (Ok/Cancel)", "Ok", null);
				if(input == "Ok"){
					return;
				}
				else if(input == "Cancel"){
					operatorIdentifier();
				}
				
			} catch (DALException e) {
				if(id == null){
					scale.pushDisplay("Error when getting ID from scale");
				}
				else if(id.length() < 10){
					scale.pushDisplay("Id too short");
				}
				else if(id.length() > 10){
					scale.pushDisplay("Id too long");
				}
				else{
					scale.pushDisplay("User not found");
				}
			}
		}
	}
	
	private void afvejning(){ //punkt 5-16
		ScaleWrapper scale;
		try {
			scale = new ScaleWrapper(socket);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		MySQLProduktbatchDAO pbDAO = new MySQLProduktbatchDAO();
		MySQLReceptDAO rcDAO = new MySQLReceptDAO();
		String input = null;
		int produktBatchNr;
		
		scale.waitForInput("Please state the product batch number:", null, null);
		produktBatchNr = Integer.parseInt(scale.waitForInput("Please state the product batch number:", null, null));
		try{
		input = scale.waitForInput(rcDAO.getRecept(pbDAO.getProduktBatch(produktBatchNr).getReceptId()).getReceptNavn() + ", please confirm the recipe (Ok/Cancel)", "Ok", null);
		if(input == "Ok"){
			afvejningCore(produktBatchNr);
		}
		else if(input == "Cancel"){
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
		ScaleWrapper scale;
		try {
			scale = new ScaleWrapper(socket);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		MySQLProduktBatchKompDAO pbkDAO = new MySQLProduktBatchKompDAO();
		MySQLProduktbatchDAO pbDAO = new MySQLProduktbatchDAO();
		String input = null;
		try{
			for(int i = 0; i < pbkDAO.getSpecifiedProduktBatchKompList(Integer.parseInt(input)).size()/*Lav en SpecifiedProduktBatchKomponentList(String); i DAO*/; i++){
				input = scale.waitForInput("Please confirm that the scale is clear (Ok/Cancel)", "Ok", null);
				if(input == "Ok"){
				}
				else if(input == "Cancel"){
					//What do?
				}
				pbDAO.getProduktBatch(produktBatch).setStatus(1);
				scale.pushDisplay("AutoTara commencing, please retreat to a safe distance"); //Yes?
				double taraval = scale.tara();
				input = scale.waitForInput("Please place container on the scale and press Ok when ready (Ok/Cancel)", "Ok", null);
				if(input == "Ok"){
				}
				else if(input == "Cancel"){
					//What do?
				}
				scale.pushDisplay("AutoTara commencing, please retreat to a safe distance"); //Yes?
				taraval += scale.tara();
				input = scale.waitForInput("Please weigh component with resourcebatch id = " + pbkDAO.getProduktBatchKompList().get(i).getRaavarebatchId() + " in the container and press Ok when ready (Ok/Cancel)", "Ok", null);
				if(input == "Ok"){
				}
				else if(input == "Cancel"){
					//What do?
				}
				pbkDAO.getProduktBatchKompList().get(i).setTara(scale.tara() + taraval);
				pbkDAO.getProduktBatchKompList().get(i).setNetto(scale.getWeight());
			}
			pbDAO.getProduktBatch(produktBatch).setStatus(2);
		}
		catch(DALException e){
			scale.pushDisplay("Error happened, trying again...");
			afvejningCore(produktBatch);
		}
	}

}