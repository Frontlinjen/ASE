
public class Main {

	public static void main(String[] args) {
		
	}

	
	public void operatorIdentifier(){
		MySQLAnsatDAO database = new MySQLAnsatDAO();
		String id = null;
		boolean loggedIn = false;
		
		while(true){
			//Ask scale for id
			id = scale.getInput();
			while(id != null){
				id = scale.getInput();
			}
			try {
				scale.PushDisplay("Hello " + database.getAnsat(id).getOprNavn() + ", authorize please... (1: yes/2: no)");
				String auth = null;
				while(auth != null){
					auth = scale.getInput();
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
	
}
