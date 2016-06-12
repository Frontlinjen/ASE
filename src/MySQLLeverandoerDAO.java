

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MySQLLeverandoerDAO implements LeverandoerDAO {

	@Override
	public LeverandoerDTO getLeverandoer(int leverandoerId) throws DALException {
		ResultSet rs = DatabaseConnector.doQuery("SELECT * FROM leverandoer WHERE leverandoer_id = " + leverandoerId + "';");
		
		try{
			if(!rs.first()) 
				throw new DALException("Leverandoeren med leverandoer_id " + leverandoerId + " findes ikke.");
			return new LeverandoerDTO(rs.getInt("leverandoer_id"), rs.getString("leverandoer_navn"));
		}
		catch(SQLException e) 
		{
			throw new DALException(e);
		}
	}

	@Override
	public List<LeverandoerDTO> getLeverandoerList() throws DALException {
		ResultSet rs = DatabaseConnector.doQuery("SELECT * FROM leverandoer;");
		return populateList(rs);
	}
	public List<LeverandoerDTO> populateList(ResultSet rs) throws DALException
	{
		try{
			if(!rs.first()) 
				throw new DALException("Ingen leverandoerer blev fundet");
			List<LeverandoerDTO> leverandoer = new ArrayList<LeverandoerDTO>();
			do
			{
				LeverandoerDTO lev = new LeverandoerDTO(rs.getInt("leverandoer_id"), rs.getString("leverandoer_navn"));
				leverandoer.add(lev);
			}while(rs.next());
			return leverandoer;
		}
		catch(SQLException e) 
		{
			throw new DALException(e);
		}
	}
	

	@Override
	public int createLeverandoer(LeverandoerDTO leverandoer) throws DALException {
		return DatabaseConnector.doUpdate(String.format("INSERT INTO leverandoer(leverandoer_id, leverandoer_navn) VALUES(" + leverandoer.getLeverandoerId() + ", '" +
		leverandoer.getLeverandoerNavn() + ");"));
	}

	@Override
	public int updateLeverandoer(LeverandoerDTO leverandoer) throws DALException {
		return DatabaseConnector.doUpdate(String.format("UPDATE leverandoer SET leverandoer_navn = " + leverandoer.getLeverandoerNavn() +
		"WHERE raavare_id = " + leverandoer.getLeverandoerId() + "';"));
	}

	
}
