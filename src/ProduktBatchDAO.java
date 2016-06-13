

import java.util.List;


public interface ProduktBatchDAO {
	ProduktBatchDTO getProduktBatch(int pbId) throws DALException;
	List<ProduktBatchDTO> getProduktBatchList() throws DALException;
	int createProduktBatch(ProduktBatchDTO produktbatch) throws DALException;
	int updateProduktBatch(ProduktBatchDTO produktbatch) throws DALException;
	List<ReceptKompDTO> getRaavareList(int pbId) throws DALException;
}