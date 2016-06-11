

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLReceptDAO implements ReceptDAO {

	@Override
	public ReceptDTO getRecept(int receptId) throws DALException {
		ResultSet rs = Connector.doQuery("SELECT * FROM recept WHERE recept_id = " + receptId + ";");
		try {
			if(!rs.first()) throw new DALException("Recepten med receptId " + receptId + " findes ikke");
	    	return new ReceptDTO (rs.getInt("recept_id"), rs.getString("recept_navn"));
	    }
	    catch (SQLException e) {throw new DALException(e); }
	}

	@Override
	public List<ReceptDTO> getReceptList() throws DALException {
		List<ReceptDTO> list = new ArrayList<ReceptDTO>();
		ResultSet rs = Connector.doQuery("SELECT * FROM recept;");
		try
		{
			while (rs.next()) 
			{
				list.add(new ReceptDTO(rs.getInt("recept_id"), rs.getString("recept_navn")));
			}
		}
		catch (SQLException e) { throw new DALException(e); }
		return list;
	}

	@Override
	public int createRecept(ReceptDTO recept) throws DALException {
		return Connector.doUpdate(
				"INSERT INTO recept(recept_id, recept_navn) VALUES " +
				"(" + recept.getReceptId() + ", '" + recept.getReceptNavn() + "');"
			);
	}

	@Override
	public int updateRecept(ReceptDTO recept) throws DALException {
		return Connector.doUpdate(
				"UPDATE recept SET recept_navn = '" + recept.getReceptNavn() + "' WHERE recept_id = " + recept.getReceptId()
				+ ";");
	}

}
