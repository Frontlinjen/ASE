

import java.util.List;


public interface ReceptDAO {
	ReceptDTO getRecept(int receptId) throws DALException;
	List<ReceptDTO> getReceptList() throws DALException;
	int createRecept(ReceptDTO recept) throws DALException;
	int updateRecept(ReceptDTO recept) throws DALException;
}
