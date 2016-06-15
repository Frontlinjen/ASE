

import java.util.List;

public interface RaavareBatchDAO {
	RaavareBatchDTO getRaavareBatch(int raavarebatchId) throws DALException;
	List<RaavareBatchDTO> getRaavarebatchList() throws DALException;
	int createRaavareBatch(RaavareBatchDTO raavarebatch) throws DALException;
	int updateRaavareBatch(RaavareBatchDTO raavarebatch) throws DALException;
}