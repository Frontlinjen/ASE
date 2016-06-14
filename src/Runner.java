import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Runner implements Runnable{

	private ScaleWrapper scale;
	private String ipAddress;
	private Socket socket;
	private String cpr;
	private AnsatDTO user;
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
			e.printStackTrace();
		}
		
	}
	private void operatorIdentifier(){ //punkt 1-4
		MySQLAnsatDAO database = new MySQLAnsatDAO();
		
		while(true){
			cpr = scale.waitForInput("Insert operator ID:", 8, null, null);
			System.out.println(cpr);
			String input;
			try {
				user = database.getAnsat(cpr);
				if(user==null)
				{
					scale.pushLongDisplay("User not found.");
					continue;
				}
			} catch (DALException e1) {
				scale.pushLongDisplay("Failed to connect to database");
			}
			
			input = scale.waitForInput(user.getOprNavn() + "?", 8, "Ok", null);
			if(input!=null){
				return;
			}
		}
	}
	
	private void afvejning(){ //punkt 5-16

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
		}
	}
	
	private void afvejningCore(int produktBatch){
		MySQLProduktBatchKompDAO pbkDAO = new MySQLProduktBatchKompDAO();
		MySQLProduktbatchDAO pbDAO = new MySQLProduktbatchDAO();
		String input = null;
		
		try {
			if(pbDAO.getProduktBatch(produktBatch).getStatus() == 1){
				scale.pushLongDisplay("Productbatch in progress");
				return;
			}
		} catch (DALException e1) {
			e1.printStackTrace();
		}
		
		try{
			List<ReceptKompDTO> list = pbDAO.getRaavareList(Integer.parseInt(input));
			for(int i = 0; i < list.size(); i++){
				ProduktBatchKompDTO pbk = new ProduktBatchKompDTO();
				while(true){
					input = scale.waitForInput("Clear scale", 8, "Ok", null);
					if(input == "Ok"){
						break;
					}
					else{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
					}
				}
				pbDAO.getProduktBatch(produktBatch).setStatus(1);
				scale.pushLongDisplay("AutoTara commencing");
				scale.tara();
				while(true){
					input = scale.waitForInput("Place container on scale", 8, "Ok", null);
					if(input == "Ok"){
						break;
					}
					else{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
					}
				}
				scale.pushLongDisplay("AutoTara commencing");
				double taraval = scale.tara();
				input = scale.waitForInput("Resource batch Number:", 8, null, null);
				pbk.setRaavarebatchId(Integer.parseInt(input));
				pbk.setTara(scale.tara() + taraval);
				pbk.setNetto(scale.getWeight());
				pbk.setCpr(cpr);
				pbk.setPbId(pbId);
				pbkDAO.addProduktBatchKomp(pbk);
			}
			pbDAO.getProduktBatch(produktBatch).setStatus(2);
			run(); //Ryk frem til start!
		}
		catch(DALException e){
			scale.pushLongDisplay("Error happened, trying again");
			afvejningCore(produktBatch);
		}
	}

}