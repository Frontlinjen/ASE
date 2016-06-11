

import java.util.List;

public interface LeverandoerDAO {
	LeverandoerDTO getLeverandoer(int leverandoerId) throws DALException;
	List<LeverandoerDTO> getLeverandoerList() throws DALException;
	int createLeverandoer(LeverandoerDTO leverandoer) throws DALException;
	int updateLeverandoer(LeverandoerDTO leverandoer) throws DALException;
}

