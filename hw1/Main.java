import java.io.IOException;
import java.util.concurrent.ForkJoinPool;


public class Main 
{
	static final ForkJoinPool fjPool = new ForkJoinPool();
	
	public static void  main( String[] args ) throws IOException 
	{
		String path = args[0];
        Test.executeTest( path, fjPool);
    }
	
	/**
	 * Tipo enum di valori sudoku [1,9]
	 */
	public enum gridSymbol 
	{
		v1,v2,v3,v4,v5,v6,v7,v8,v9;
	}
}