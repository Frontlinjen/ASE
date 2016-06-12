
public class Main {

	public static void main(String[] args) {
		
	}

	
	public void operatorIdentifier(){ //punkt 1-4
		MySQLAnsatDAO database = new MySQLAnsatDAO();
		String input = null;
		
		while(true){
			//Ask ScaleWrapper for id
			while(input == null){
				input = ScaleWrapper.getInput();
			}
			try {
				ScaleWrapper.pushDisplay("Hello " + database.getAnsat(input).getOprNavn() + ", please confirm (1: yes/2: no)");
				while(input == null){
					input = ScaleWrapper.getInput();
				}
				if(input == "1"){
					return;
				}
				else if(input == "2"){
					operatorIdentifier();
				}
				
			} catch (DALException e) {
				if(input.length() < 10){
					ScaleWrapper.pushDisplay("Id too short");
				}
				else if(input.length() > 10){
					ScaleWrapper.pushDisplay("Id too long");
				}
				else{
					ScaleWrapper.pushDisplay("User not found");
				}
			}
		}
	}
	
	public void afvejning(){ //punkt 5-16
		MySQLProduktbatchDAO pbDAO = new MySQLProduktbatchDAO();
		MySQLReceptDAO rcDAO = new MySQLReceptDAO();
		String input = null;
		int produktBatchNr;
		ScaleWrapper.pushDisplay("Please state the product batch number:");
		while(input == null){
			input = ScaleWrapper.getInput();
		}
		produktBatchNr = Integer.parseInt(input);
		try{
		ScaleWrapper.pushDisplay(rcDAO.getRecept(pbDAO.getProduktBatch(produktBatchNr).getReceptId()).getReceptNavn() + ", please confirm the recipe (1: yes/2: no)");
		while(input == null){
			input = ScaleWrapper.getInput();
		}
		if(input == "1"){
			afvejningCore(produktBatchNr);
		}
		else if(input == "2"){
			afvejning();
		}
		}
		catch(DALException e){
			ScaleWrapper.pushDisplay("Failed to find recipe, please check product batch number");
			afvejning();
			//Annuller mulighed?
		}
	}
	
	private void afvejningCore(int produktBatch){
		MySQLProduktBatchKompDAO pbkDAO = new MySQLProduktBatchKompDAO();
		MySQLProduktbatchDAO pbDAO = new MySQLProduktbatchDAO();
		String input = null;
		try{
			for(int i = 0; i < pbkDAO.getSpecifiedProduktBatchKompList(Integer.parseInt(input)).size()/*Lav en SpecifiedProduktBatchKomponentList(String); i DAO*/; i++){
				ScaleWrapper.pushDisplay("Please confirm that the ScaleWrapper is clear, press 1 to confirm");
				while(input != "1"){
					ScaleWrapper.getInput();
				}
				pbDAO.getProduktBatch(produktBatch).setStatus(1);
				ScaleWrapper.pushDisplay("AutoTara"); //Yes?
				ScaleWrapper.tara();
				ScaleWrapper.pushDisplay("Please place container on the ScaleWrapper and press 1 when ready");
				while(input != "1"){
					ScaleWrapper.getInput();
				}
				double containerWeight = ScaleWrapper.getWeight();
				ScaleWrapper.pushDisplay("AutoTara"); //Yes?
				ScaleWrapper.tara();
				ScaleWrapper.pushDisplay("Please weigh component with resourcebatch id = " + pbkDAO.getProduktBatchKompList().get(i).getRaavarebatchId() + " in the container and press 1 when ready");
				while(input != "1"){
					ScaleWrapper.getInput();
				}
				pbkDAO.getProduktBatchKompList().get(i).setTara(containerWeight);
				pbkDAO.getProduktBatchKompList().get(i).setNetto(ScaleWrapper.getWeight()-containerWeight);
			}
			pbDAO.getProduktBatch(produktBatch).setStatus(2);
		}
		catch(DALException e){
			ScaleWrapper.pushDisplay("Error happened, trying again...");
			afvejningCore(produktBatch);
		}
	}
}