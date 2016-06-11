

import java.util.List;

public interface RankDAO {
	RankDTO getRank(int titel) throws DALException;
	List<RankDTO> getRankList() throws DALException;
}
