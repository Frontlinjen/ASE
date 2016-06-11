

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MySQLRaavareBatchDAO implements RaavareBatchDAO{

	@Override
	public RaavareBatchDTO getRaavareBatch(int raavarebatchId) throws DALException {
		ResultSet rs = Connector.doQuery("SELECT * FROM raavarebatch WHERE raavarebatch_id = " + raavarebatchId + ";");
		try{
			if (!rs.first()) throw new DALException("Raavarebatchen med raavarebatchId " + raavarebatchId + " findes ikke");
			return new RaavareBatchDTO(rs.getInt("raavarebatch_id"), rs.getInt("raavare_id"), rs.getInt("leverandoer_id"), rs.getDouble("maengde"));
		}
		catch (SQLException e) {throw new DALException(e); }
	}

	@Override
	public List<RaavareBatchDTO> getRaavarebatchList() throws DALException {
		List<RaavareBatchDTO> list = new ArrayList<RaavareBatchDTO>();
		ResultSet rs = Connector.doQuery("SELECT * FROM raavarebatch;");
		try
		{
			while (rs.next()) 
			{
				list.add(new RaavareBatchDTO(rs.getInt("raavarebatch_id"), rs.getInt("raavare_id"), rs.getInt("leverandoer_id"), rs.getDouble("maengde")));
			}
		}
		catch (SQLException e) { throw new DALException(e); }
		return list;
	}

	@Override
	public int createRaavareBatch(RaavareBatchDTO raavarebatch) throws DALException {
		return Connector.doUpdate("INSERT INTO raavarebatch(raavarebatch_id, raavare_id, leverandoer_id, maengde) VALUES (" + raavarebatch.getRaavarebatchId() + ","
				+ raavarebatch.getRaavareId() + "," + raavarebatch.getLeverandoerId() + "," + raavarebatch.getMaengde() + ");");
	}

	@Override
	public int updateRaavareBatch(RaavareBatchDTO raavarebatch) throws DALException {
		return Connector.doUpdate("UPDATE raavarebatch SET raavare_id = '" + raavarebatch.getRaavareId() + "', leverandoer_id = '"
				+ raavarebatch.getLeverandoerId() + "', maengde = '" + raavarebatch.getMaengde() + "' WHERE raavarebatch_id = " + raavarebatch.getRaavarebatchId() + ";");
	}

}
