

import java.util.List;


public interface ProduktBatchKompDAO {
	ProduktBatchKompDTO getProduktBatchKomp(int pbId, int raavarebatchId) throws DALException;
	List<ProduktBatchKompDTO> getProduktBatchKompList() throws DALException;
	int createProduktBatchKomp(ProduktBatchKompDTO produktbatchkomponent) throws DALException;
	int updateProduktBatchKomp(ProduktBatchKompDTO produktbatchkomponent) throws DALException;
	List<ProduktBatchKompDTO> getSpecifiedProduktBatchKompList(int pbId) throws DALException;	
}

