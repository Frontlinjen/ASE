import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.print.attribute.standard.PDLOverrideSupported;

public class Runner implements Runnable{

	private ScaleWrapper scale;
	private String ipAddress;
	private Socket socket;
	private String cpr;
	private AnsatDTO user;

	ProduktBatchDTO pbDTO;
	ReceptDTO rbDTO;
	
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
			cpr = scale.waitForInput("Insert operator ID:", 8, "1234567890", null);
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
		
		int pbId = Integer.parseInt(scale.waitForInput("Product batch number:", 8, null, null));
		try{
			pbDTO = pbDAO.getProduktBatch(pbId);
			rbDTO = rcDAO.getRecept(pbDTO.getReceptId());
			scale.pushLongDisplay(rbDTO.getReceptNavn());
		input = scale.waitForInput("Please confirm", 8, "Ok", null);
		if(input != null){
			try {
				if(pbDAO.getProduktBatch(pbId).getStatus() == 2){
					scale.pushLongDisplay("Productbatch in progress");
				} else {
					afvejningCore(pbId);
				}
			} catch (DALException e1) {
				e1.printStackTrace();
			}
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
		
		try{
			List<ReceptKompDTO> list = null;
			do
			{
				if(list!=null)
				{
					scale.pushLongDisplay("Failed to find raavarelist");
					System.out.println("Ingen raavarelist..");
				}
				list = pbDAO.getRaavareList(pbDTO.getPbId());
			}while(list.size()==0);
			for(int i = 0; i < list.size(); i++){
				System.out.println("test");
				ProduktBatchKompDTO pbk = new ProduktBatchKompDTO();
				System.out.println("test2");
				while(true){
					input = scale.waitForInput("Clear scale", 8, "Ok", null);
					if(input != null){
						break;
					}
					else{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
					}
				}
				pbDAO.getProduktBatch(produktBatch).setStatus(2);
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
				pbk.setPbId(pbDTO.getPbId());
				pbkDAO.addProduktBatchKomp(pbk);
			}
			pbDAO.getProduktBatch(produktBatch).setStatus(3);
			run(); //Ryk frem til start!
		}
		catch(DALException e){
			scale.pushLongDisplay("Error happened, trying again");
			e.printStackTrace();
			afvejningCore(produktBatch);
		}
	}

}