

import java.util.List;

public interface AnsatDAO {
	AnsatDTO getAnsat(String cpr) throws DALException;
	List<AnsatDTO> getAnsatList() throws DALException;
	int createAnsat(AnsatDTO ans) throws DALException;
	int updateAnsat(AnsatDTO ans) throws DALException;
	int deleteAnsat(AnsatDTO ans) throws DALException;
}
