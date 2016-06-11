

import java.io.Serializable;

public class ProduktBatchKompDTO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int pbId; 	  // produktbatchets id
	int raavarebatchId;        // raavarebatchens id
	double tara;
	double netto;
	String cpr;					// cpr-nummer for operatøren

	public ProduktBatchKompDTO()
	{
		
	}
	
	public ProduktBatchKompDTO(int pbId, int raavarebatchId, double tara, double netto, String cpr)
	{
		this.pbId = pbId;
		this.raavarebatchId = raavarebatchId;
		this.tara = tara;
		this.netto = netto;
		this.cpr = cpr;
	}
	
	public int getPbId() { return pbId; }
	public void setPbId(int pbId) { this.pbId = pbId; }
	public int getRaavarebatchId() { return raavarebatchId; }
	public void setRaavarebatchId(int raavarebatchId) { this.raavarebatchId = raavarebatchId; }
	public double getTara() { return tara; }
	public void setTara(double tara) { this.tara = tara; }
	public double getNetto() { return netto; }
	public void setNetto(double netto) { this.netto = netto; }
	public String getCpr() { return cpr; }
	public void setCpr(String cpr) { this.cpr = cpr; }
	@Override
	public String toString() { 
		return pbId + "\t" + raavarebatchId +"\t" + tara +"\t" + netto + "\t" + cpr ; 
	}
}
