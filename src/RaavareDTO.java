

import java.io.Serializable;

/**
 * Raavare Data Objekt
 * 
 * @author mn/sh/tb
 * @version 1.2
 */

public class RaavareDTO implements Serializable 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** i omraadet 1-99999999 vaelges af brugerne */
    int raavareId;                     
    /** min. 2 max. 20 karakterer */
    String raavareNavn;                        
	
    public RaavareDTO(){
    	
    }
    
	public RaavareDTO(int raavareId, String raavareNavn)
	{
		this.raavareId = raavareId;
		this.raavareNavn = raavareNavn;
	}
	
    public int getRaavareId() { return raavareId; }
    public void setRaavareId(int raavareId) { this.raavareId = raavareId; }
    public String getRaavareNavn() { return raavareNavn; }
    public void setRaavareNavn(String raavareNavn) { this.raavareNavn = raavareNavn; }
    @Override
	public String toString() { 
		return raavareId + "\t" + raavareNavn; 
	}
}