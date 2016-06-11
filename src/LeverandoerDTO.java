

import java.io.Serializable;

public class LeverandoerDTO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int leverandoerId;             // i omraadet 1-99999999
	String leverandoerNavn;

	public LeverandoerDTO()
	{
		
	}
	
	public LeverandoerDTO(int leverandoerId, String leverandoerNavn)
	{
		this.leverandoerId = leverandoerId;
		this.leverandoerNavn = leverandoerNavn;
	}
	
	public int getLeverandoerId() { return leverandoerId; }
	public void setLeverandoerId(int leverandoerId) { this.leverandoerId = leverandoerId; }
	public String getLeverandoerNavn() { return leverandoerNavn; }
	public void setLeverandoerNavn(String leverandoerNavn) { this.leverandoerNavn = leverandoerNavn; }
	@Override
	public String toString() { 
		return leverandoerId + "\t" + leverandoerNavn; 
	}
}
