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
		System.out.println("Opretter forbindelse...");
		connectToScale();
		System.out.println("Forbindelse oprettet!");
		System.out.println("Identificerer bruger...");
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
					System.out.println("For tidlig Time Out");
				}
			}catch (IOException e) 
			{
				System.out.println("Kunne IKKE oprette socket: " + e.getMessage());
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
			cpr = scale.waitForInput("Angiv operator ID:", 8, null, null);
			System.out.println("Vaegten bliver brugt af "  + cpr);
			
			try {
				user = database.getAnsat(cpr);
				if(user.getTitel()<0)
				{
					scale.pushLongDisplay("Du er ikke ansat");
					continue;
				}
			} catch (DALException e1) {
				scale.pushLongDisplay("Kunne IKKE finde bruger");
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
					pbId = Integer.parseInt(scale.waitForInput("Productbatchnummer:", 8, null, null));
					pbDTO = pbDAO.getProduktBatch(pbId);
					rebDTO = rcDAO.getRecept(pbDTO.getReceptId());
					if(pbDTO.getStatus() == 2){
						scale.pushLongDisplay("Allerede begyndt");
						continue;
					}
					
					scale.pushLongDisplay(rebDTO.getReceptNavn());
					input = scale.waitForInput("Godkend: " + rebDTO.getReceptNavn(), 8, "Ok", null);
					if(input != null && input.equals("Ok")){
						pbDTO.setStatus(2);
						pbDAO.updateProduktBatch(pbDTO);
						if(afvejningCore(pbId)==0) // No errors occurred during weight
						{
							break;
						}
					}
				}
				
				catch(DALException e){
					scale.pushLongDisplay("Kunne IKKE finde recept");
				}
				catch(NumberFormatException e){
					scale.pushLongDisplay("Accepterer kun tal");
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
					scale.waitForConfirmation("Raavareliste IKKE fundet");
					System.out.println("Ingen raavareliste for produktBatch: " + produktBatch);
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
					pbDAO.updateProduktBatch(pbDTO);
				} catch (DALException e) {
					System.out.println(e.getMessage());
				}
				scale.pushLongDisplay("AutoTara begynder...");
				scale.tara();
				scale.waitForConfirmation("Stil beholder paa vaegt");
				scale.pushLongDisplay("AutoTara begynder...");
				double taraval = scale.tara();
				while(true)
				{
					scale.pushLongDisplay("Tast batchnr for raavareID:");
					input = scale.waitForInput("Raavare " + Integer.toString(ingredients.get(i).getRaavareId()), 8, null, null);
					try {
						rabDTO = rabDAO.getRaavareBatch(Integer.parseInt(input));
						pbkDTO.setRaavarebatchId(Integer.parseInt(input));
						break;
					} catch (NumberFormatException e1) {
						scale.pushLongDisplay("Accepterer kun tal");
					} catch (DALException e1) {
						scale.pushLongDisplay("Raavarebatch IKKE fundet");
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
						scale.pushLongDisplay("Produktbatchkomponent udfoert");
						System.out.println("Saved: " + pbkDTO.toString());
						break;
					} catch (DALException e1) {
						scale.pushLongDisplay("Afvejning IKKE gemt");
					}
				}
				
				
			}
			try {
				pbDTO.setStatus(3);
				pbDAO.updateProduktBatch(pbDTO);
				
			} catch (DALException e) {
				e.printStackTrace();
				scale.pushLongDisplay("Status IKKE opdateret");
			}
			return 0; //Done
	}
}