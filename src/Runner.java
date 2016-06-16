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

	ProduktBatchDTO pbDTO;
	ReceptDTO rebDTO;
	ReceptKompDTO rkDTO;
	RaavareBatchDTO rabDTO;
	
	@Override
	public void run() {
		System.out.println("Connecting to scale..");
		connectToScale();
		System.out.println("Connected!");
		System.out.println("Starting operator verification..");
		operatorIdentifier();
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
		AnsatDAO database = new MySQLAnsatDAO();
		while(true){
			cpr = scale.waitForInput("Insert operator ID:", 8, "0003030303", null);
			System.out.println("The weight has been taken in command by "  + cpr);
			
			try {
				user = database.getAnsat(cpr);
			} catch (DALException e1) {
				scale.pushLongDisplay("User not found");
				continue;
			}
			String input;
			input = scale.waitForInput(user.getOprNavn() + "?", 8, "Ok", null);
			if(input!=null && input.equals("Ok")){
				afvejning();
			}
		}
	}
	
	private void afvejning(){ //punkt 5-16

		ProduktBatchDAO pbDAO = new MySQLProduktbatchDAO();
		ReceptDAO rcDAO = new MySQLReceptDAO();
		String input = null;
		int pbId;
		
			while(true)
			{
				try{
					pbId = Integer.parseInt(scale.waitForInput("Product batch number:", 8, null, null));
					pbDTO = pbDAO.getProduktBatch(pbId);
					rebDTO = rcDAO.getRecept(pbDTO.getReceptId());
					scale.pushLongDisplay(rebDTO.getReceptNavn());
					input = scale.waitForInput("Please confirm", 8, "Ok", null);
					if(input != null && input.equals("Ok")){
						if(afvejningCore(pbId)==0) // No errors occurred during weight
						{
							break;
						}
					}
				}
				
				catch(DALException e){
					scale.pushLongDisplay("Recept not found");
				}
				catch(NumberFormatException e){
					scale.pushLongDisplay("Only numbers accepted");
				}
				scale.pushLongDisplay("Indtast produktnummer igen.");
			}
			
		}
	
	private int afvejningCore(int produktBatch){
		RaavareBatchDAO rabDAO = new MySQLRaavareBatchDAO();
		ReceptKompDAO rkDAO = new MySQLReceptKompDAO();
		ProduktBatchKompDAO pbkDAO = new MySQLProduktBatchKompDAO();
		ProduktBatchDAO pbDAO = new MySQLProduktbatchDAO();
		String input = null;
		List<ReceptKompDTO> ingredients = null;
		
				try {
					ingredients = pbDAO.getRaavareList(produktBatch);
				} catch (DALException e) {
					e.printStackTrace();
					
				}
		
				if(ingredients==null)
				{
					scale.waitForConfirmation("Failed to find raavarelist");
					System.out.println("Ingen raavarelist for produktBatch: " + produktBatch);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						//We can ignore this exception
					}
					return -1; //Ask for batch number again
				}
			for(int i = 0; i < ingredients.size(); i++){
				ProduktBatchKompDTO pbkDTO = new ProduktBatchKompDTO();
				scale.waitForConfirmation("Ryd vaegten");
				try {
					pbDTO.setStatus(2);
					pbDAO.updateProduktBatch(pbDTO);
				} catch (DALException e) {
					System.out.println(e.getMessage());
				}
				scale.pushLongDisplay("AutoTara commencing");
				scale.tara();
				scale.waitForConfirmation("Stil beholder på vaegten");
				scale.pushLongDisplay("AutoTara commencing");
				double taraval = scale.tara();
				while(true)
				{
					input = scale.waitForInput("Raavare " + ingredients.get(i).getRaavareId() + " batch nummer:", 8, null, null);
					try {
						rabDTO = rabDAO.getRaavareBatch(Integer.parseInt(input));
						pbkDTO.setRaavarebatchId(Integer.parseInt(input));
						break;
					} catch (NumberFormatException e1) {
						scale.pushLongDisplay("Indtast et tal");
					} catch (DALException e1) {
						scale.pushLongDisplay("Resource batch not found.");
					}
				}
				while(true)
				{
					scale.waitForConfirmation("Afvej varen");
					double weight = scale.getWeight();
					double nomnetto;
					double tolerance;
					try {
						nomnetto = rkDAO.getReceptKomp(rebDTO.getReceptId(), rabDTO.getRaavareId()).getNomNetto();
						tolerance = rkDAO.getReceptKomp(rebDTO.getReceptId(), rabDTO.getRaavareId()).getTolerance();
						if(!(weight < nomnetto*(tolerance/100 + 1)) || !(weight > nomnetto*(1 -tolerance/100))){
							scale.pushLongDisplay("Uden for tolerance");
						
							continue;
						}
						pbkDTO.setTara(taraval);
						pbkDTO.setNetto(weight);
						pbkDTO.setCpr(cpr);
						pbkDTO.setPbId(produktBatch);
						pbkDAO.addProductBatchKomponent(pbkDTO);
						scale.pushLongDisplay("Produktbatch udført");
						System.out.println("Saved: " + pbkDTO.toString());
						break;
					} catch (DALException e1) {
						scale.pushLongDisplay("Kunne ikke gemme afvejningen.");
					}
				}
				
				
			}
			try {
				pbDTO.setStatus(3);
				pbDAO.updateProduktBatch(pbDTO);
			} catch (DALException e) {
				e.printStackTrace();
			}
			return 0;
	}
}