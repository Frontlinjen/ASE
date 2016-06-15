

import java.util.List;


public interface ProduktBatchKompDAO {
	ProduktBatchKompDTO getProduktBatchKomp(int pbId, int raavarebatchId) throws DALException;
	List<ProduktBatchKompDTO> getProduktBatchKompList() throws DALException;
	int addProductBatchKomponent(ProduktBatchKompDTO produktbatchkomponent) throws DALException;
	int updateProduktBatchKomp(ProduktBatchKompDTO produktbatchkomponent) throws DALException;
	
}

