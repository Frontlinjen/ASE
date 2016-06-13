import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Runner implements Runnable{

	private ScaleWrapper scale;
	private String ipAddress;
	private Socket socket;
	private String cpr;
	private int pbId;
	@Override
	public void run() {
		connectToScale();
		operatorIdentifier();
		System.out.println("Starting afvejning");
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
		try {
			scale = new ScaleWrapper(socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void operatorIdentifier(){ //punkt 1-4
		
		MySQLAnsatDAO database = new MySQLAnsatDAO();
		
		while(true){
			cpr = scale.waitForInput("Insert operator ID:", 8, null, null);
			System.out.println(cpr);
			String input = scale.waitForInput("Confirm " + cpr, 8, "Ok", null);
			if(input!=null){
				return;
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
		
		pbId = Integer.parseInt(scale.waitForInput("Product batch number:", 8, null, null));
		try{
			scale.pushLongDisplay(rcDAO.getRecept(pbDAO.getProduktBatch(pbId).getReceptId()).getReceptNavn());
		input = scale.waitForInput("Please confirm", 8, "Ok", null);
		if(input != null){
			afvejningCore(pbId);
		}
		else{
			afvejning();
		}
		}
		catch(DALException e){
			scale.pushLongDisplay("Failed to find recipe");
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
		
		try {
			if(pbDAO.getProduktBatch(produktBatch).getStatus() == 1){
				scale.pushLongDisplay("Productbatch in progress");
			}
		} catch (DALException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			List<ReceptKompDTO> list = pbDAO.getRaavareList(Integer.parseInt(input));
			for(int i = 0; i < list.size(); i++){
				ProduktBatchKompDTO pbk = new ProduktBatchKompDTO();
				input = scale.waitForInput("Clear scale", 8, "Ok", null);
				if(input == "Ok"){
				}
				else if(input == "Cancel"){
					//What do?
				}
				pbDAO.getProduktBatch(produktBatch).setStatus(1);
				scale.pushLongDisplay("AutoTara commencing"); //Yes?
				scale.tara();
				input = scale.waitForInput("Place container on scale", 8, "Ok", null);
				if(input == "Ok"){
				}
				else if(input == "Cancel"){
					//What do?
				}
				scale.pushLongDisplay("AutoTara commencing"); //Yes?
				double taraval = scale.tara();
				input = scale.waitForInput("Resource batch Number:", 8, null, null);
				pbk.setRaavarebatchId(Integer.parseInt(input));
				pbk.setTara(scale.tara() + taraval);
				pbk.setNetto(scale.getWeight());
				pbk.setCpr(cpr);
				pbk.setPbId(pbId);
			}
			pbDAO.getProduktBatch(produktBatch).setStatus(2);
		}
		catch(DALException e){
			scale.pushLongDisplay("Error happened, trying again");
			afvejningCore(produktBatch);
		}
	}

}