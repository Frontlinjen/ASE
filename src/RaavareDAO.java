
import java.util.List;


public interface RaavareDAO {
	RaavareDTO getRaavare(int raavareId) throws DALException;
	List<RaavareDTO> getRaavareList() throws DALException;
	int createRaavare(RaavareDTO raavare) throws DALException;
	int updateRaavare(RaavareDTO raavare) throws DALException;
}