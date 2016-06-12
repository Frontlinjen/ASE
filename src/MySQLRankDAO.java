

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MySQLRankDAO implements RankDAO{

	@Override
	public RankDTO getRank(int titel) throws DALException {
		ResultSet rs = DatabaseConnector.doQuery("SELECT * FROM rank WHERE titel = " + titel + ";");
		try {
			if(!rs.first()) throw new DALException("Ranken med titel " + titel + " findes ikke");
	    	return new RankDTO (rs.getInt("titel"), rs.getString("rank"));
	    }
	    catch (SQLException e) {throw new DALException(e); }
	}

	@Override
	public List<RankDTO> getRankList() throws DALException {
		List<RankDTO> list = new ArrayList<RankDTO>();
		ResultSet rs = DatabaseConnector.doQuery("SELECT * FROM rank;");
		try
		{
			while (rs.next()) 
			{
				list.add(new RankDTO(rs.getInt("titel"), rs.getString("rank")));
			}
		}
		catch (SQLException e) { throw new DALException(e); }
		return list;
	}

}
