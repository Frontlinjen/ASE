

import java.io.Serializable;

/**
 * Operatoer Data Access Objekt
 * 
 * @author mn/tb
 * @version 1.2
 */


public class AnsatDTO implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Operatoer-identifikationsnummer (cpr).*/
	String cpr;                     
	/** Operatoernavn (opr_navn) min. 2 max. 20 karakterer */
	String oprNavn;                
	/** Operatoer-initialer min. 2 max. 3 karakterer */
	String ini;                               
	/** Operatoer password min. 7 max. 8 karakterer */
	String password;          
	/** Den ansattes titel, som afgør hvilke rettigheder han har.*/
	int titel;
	public AnsatDTO()
	{

	}
	public AnsatDTO(String cpr, String oprNavn, String ini, String password, int titel)
	{
		this.cpr = cpr;
		this.oprNavn = oprNavn;
		this.ini = ini;
		this.password = password;
		this.titel = titel;
	}
	
    public AnsatDTO(AnsatDTO ans)
    {
    	this.cpr = ans.getCpr();
    	this.oprNavn = ans.getOprNavn();
    	this.ini = ans.getIni();
    	this.password = ans.getPassword();
    	this.titel = ans.getTitel();
    }
    
    public String getCpr() { return cpr; }
	public void setCpr(String cpr) { this.cpr = cpr; }
	public String getOprNavn() { return oprNavn; }
	public void setOprNavn(String oprNavn) { this.oprNavn = oprNavn; }
	public String getIni() { return ini; }
	public void setIni(String ini) { this.ini = ini; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public int getTitel() { return titel; }
	public void setTitel(int titel) { this.titel = titel; }
	@Override
	public String toString() { return cpr + "\t" + oprNavn + "\t" + ini + "\t" + password + "\t" + titel; }
}
