

import java.io.Serializable;

/**
 * Recept Data Objekt
 * 
 * @author mn/tb
 * @version 1.2
 */

public class ReceptDTO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Recept nr i omraadet 1-99999999 */
	int receptId;
	/** Receptnavn min. 2 max. 20 karakterer */
	String receptNavn;
	/** liste af kompenenter i recepten */
	
    public ReceptDTO()
    {
    	
    }
	
	public ReceptDTO(int receptId, String receptNavn)
	{
        this.receptId = receptId;
        this.receptNavn = receptNavn;
    }

    public int getReceptId() { return receptId; }
	public void setReceptId(int receptId) { this.receptId = receptId; }
	public String getReceptNavn() { return receptNavn; }
	public void setReceptNavn(String receptNavn) { this.receptNavn = receptNavn; }
	@Override
	public String toString() { 
		return receptId + "\t" + receptNavn; 
	}
}
