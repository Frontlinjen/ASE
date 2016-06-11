

import java.io.Serializable;

public class RankDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int titel;
	String rank;
	
	public RankDTO()
	{
		
	}
	
	public RankDTO(int titel, String rank){
		this.titel = titel;
		this.rank = rank;
	}
	
	public int getTitel() {return titel;}
	public void setTitel(int titel) {this.titel = titel;}
	public String getRank() {return rank;}
	public void setRank(String rank) {this.rank = rank;}
	@Override
	public String toString(){
		return titel + "/t" + rank + "/t";
	}
	
}
