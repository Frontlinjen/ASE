

import java.util.List;


public interface ReceptKompDAO {
	ReceptKompDTO getReceptKomp(int receptId, int raavareId) throws DALException;
	List<ReceptKompDTO> getReceptKompList(int receptId) throws DALException;
	List<ReceptKompDTO> getReceptKompList() throws DALException;
	int createReceptKomp(ReceptKompDTO receptkomponent) throws DALException;
	int updateReceptKomp(ReceptKompDTO receptkomponent) throws DALException;
}
