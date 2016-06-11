
public class Main {

	public static void main(String[] args) {
		
	}

	
	public void operatorIdentifier(){ //punkt 1-4
		MySQLAnsatDAO database = new MySQLAnsatDAO();
		String input = null;
		
		while(true){
			//Ask scale for id
			while(input == null){
				input = scale.getInput();
			}
			try {
				scale.PushDisplay("Hello " + database.getAnsat(input).getOprNavn() + ", please confirm (1: yes/2: no)");
				while(input == null){
					input = scale.getInput();
				}
				if(auth == "1"){
					return;
				}
				else if(auth == "2"){
					operatorIdentifier();
				}
				
			} catch (DALException e) {
				if(id.length() < 10){
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
	
	public void afvejning(){ //punkt 5-16
		MySQLProduktbatchDAO pbDAO = new MySQLProduktbatchDAO();
		MySQLReceptDAO rcDAO = new MySQLReceptDAO();
		String input = null;
		scale.pushDisplay("Please state the product batch number:");
		while(input == null){
			input = scale.getInput();
		}
		try{
		scale.pushDisplay(rcDAO.getRecept(pbDAO.getProduktBatch(Integer.parseInt(input)).getReceptId()).getReceptNavn() + ", please confirm the recipe (1: yes/2: no)");
		while(input == null){
			input = scale.getInput();
		}
		if(auth == "1"){
			afvejningCore();
		}
		else if(auth == "2"){
			afvejning();
		}
		}
		catch(DALException e){
			scale.pushDisplay("Failed to find recipe, please check product batch number");
			afvejning();
			//Annuller mulighed?
		}
	}
	
	private void afvejningCore(){
		MySQLProduktBatchKompDAO pbkDAO = new MySQLProduktBatchKompDAO();
		String input = null;
		try{
			for(int i = 0; i < pbkDAO.getSpecifiedProduktBatchKompList(input).size()/*Lav en SpecifiedProduktBatchKomponentList(String); i DAO*/; i++){
				scale.pushDisplay("Please confirm that the scale is clear, press 1 to confirm");
				while(input != "1"){
					scale.getInput();
				}
				pbDAO.setStatus(input, "Under Produktion"); //Lav pbDAO.setStatus(int, String); i DAO 
				scale.pushDisplay("AutoTara"); //Yes?
				scale.tara();
				scale.pushDisplay("Please place container on the scale and press 1 when ready");
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
			pbkDAO.setStatus("Afsluttet");
		}
		catch(DALException e){
			scale.PushDisplay("Error happened, trying again...");
			afvejningCore();
	}
}
