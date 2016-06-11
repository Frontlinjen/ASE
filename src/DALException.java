

import java.io.Serializable;

public class DALException extends Exception implements Serializable
{
	public DALException()
	{
		
	}
	private static final long serialVersionUID = -5490114628089339322L;
	public DALException(String message) { super(message); }    
	public DALException(Exception e) { super(e.getMessage()); }
}
