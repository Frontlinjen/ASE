

public class SQLStates {
	public static final int INTEGRITY_FAILED = 1452;
	public static final int DUPLICATE_PRIMARY = 1062;
	public static boolean isRecoverable(String s)
	{
		int e = Integer.parseInt(s);
		return isRecoverable(e);
	}
	public static boolean isRecoverable(int e)
	{
		return (e == INTEGRITY_FAILED) || (e == DUPLICATE_PRIMARY);
	}
	public static boolean isIntegrityFailure(String s)
	{
		return isIntegrityFailure(Integer.parseInt(s));
	}
	public static boolean isIntegrityFailure(int e)
	{
		return e==INTEGRITY_FAILED;
	}
	public static boolean isDuplicateFailure(String s)
	{
		return isDuplicateFailure(Integer.parseInt(s));
	}
	public static boolean isDuplicateFailure(int e)
	{
		return e==DUPLICATE_PRIMARY;
	}
}
