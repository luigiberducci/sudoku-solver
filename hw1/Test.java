import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class Test 
{
	static ForkJoinPool fjPool;		//Fork Join Pool che implementa la parallelizzazione
	
	/**
	 * Esecuzione del test: singolo o multiplo
	 * Test Singolo, a partire da un file .txt
	 * Test Multiplo, a partire da una directory contenente più file .txt
	 * @param inputPath		percorso di input
	 * @param fjP			Fork join pool
	 * @throws IOException
	 */
	public static void executeTest(String inputPath, ForkJoinPool fjP) throws IOException
	{
		fjPool = fjP;
		File inputFile = new File(inputPath);

		System.out.println("***SudokuSolver project***");
		if( inputFile.isDirectory() ) 
		{
	        long inizioTest = System.currentTimeMillis();

			System.out.println( "Execution of multiple tests" );
			System.out.println( "Test directory: " + inputFile );
			File[] listOfFiles = inputFile.listFiles( );
			Arrays.sort( listOfFiles );
			for(File file : listOfFiles)
			{
				if( !file.isHidden() )
					startTest( file );
			}
			
	        long fineTest = System.currentTimeMillis();
	        double totalTimeTest = ( ( fineTest - inizioTest ) / 1000.0 );

			System.out.println( "----------End of testing----------" );
			System.out.println( "Number of test executed: " + listOfFiles.length );
			System.out.println( "Total ammount of Time: " + totalTimeTest + " sec" );
			
		}
		else
		{
			System.out.println( "Execution of single test" );
			startTest( inputFile );
		}
		
	}
	
	/**
	 * Esecuzione del test: prima sequenziale e poi parallela
	 * con stampa dei tempi e calcolo dello SpeedUp
	 * @param file			file di input con codifica del Sudoku
	 * @throws IOException
	 */
	public static void startTest( File file ) throws IOException{
		System.out.println(file.getName());
		int[] sudokuIntMatrix = parseFile2Int( file );
		System.out.println("***Start test***");
        System.out.println("Input file: " + file.getName());
        
        long inizioSequenziale = System.currentTimeMillis();
        runSequentialExecution(sudokuIntMatrix);
        long fineSequenziale = System.currentTimeMillis();
        double timeSequenziale = ( ( fineSequenziale - inizioSequenziale ) / 1000.0 );
        System.out.println( "	Time: " + timeSequenziale + " sec" );
        		       
        System.out.println();

		long inizioParallelo = System.currentTimeMillis();
        runParallelExecution(sudokuIntMatrix, fjPool);
        long fineParallelo = System.currentTimeMillis();
        double timeParallelo = ( ( fineParallelo - inizioParallelo ) / 1000.0 );
        System.out.println( "	Time: " + timeParallelo + " sec" );

    
        System.out.println();
        System.out.println( "Speedup: " + ( timeSequenziale / timeParallelo) );
        System.out.println();
	}
	
	/**
	 * Esecuzione del risolutore sequenziale
	 * @param m 	array di interi che rappresenta la griglia di Sudoku
	 */
	public static void runSequentialExecution( int[] m )
	{
        mySequentialSolver seqSolver = new mySequentialSolver( m );
        System.out.println( "Fill rate (ratio): " + seqSolver.myMatrix.alreadyAssigned + "/81" );
        System.out.println( "Fill rate (percent): " + ( ( seqSolver.myMatrix.alreadyAssigned * 100 ) / 81 ) + " %" );
        System.out.println();
        System.out.println( "	Sequential execution" );        
        seqSolver.solve();
        System.out.println( "	Solutions founded: " + seqSolver.solCounter );


	}
	
	/**
	 * Esecuzione del risolutore parallelo
	 * @param vector 	array di interi che rappresenta la griglia di Sudoku
	 * @param fjPool	Frok Join Pool che implementa la parallelizzazione
	 */
	public static void runParallelExecution( int[] vector , ForkJoinPool fjPool)
	{
        myParallelSolver parSolver = new myParallelSolver( vector );
        System.out.println( "	Parallel execution" );
        System.out.println( "	Solutions founded: " + fjPool.invoke( parSolver ) );
		System.out.println( "    	# Forked Solvers :"+(parSolver.Threads.intValue()+1));

	}	
	
	/**
     * Estrazione di un array di interi a partire da un file che codifica la griglia di Sudoku
     * @param inputFile	percorso del file codificato
     * @return	array di interi
     * @throws IOException
     */
	public static int[] parseFile2Int( File inputFile ) throws IOException
	{
    	int[] mys = new int[81] ;
        FileReader f;
        f = new FileReader( inputFile );
        BufferedReader b;
        b = new BufferedReader( f );
        String s;
        int i = 0;
        while( true ) 
        {
            s = b.readLine();
            if( s == null )	break;
            for( int k = 0 ; k < s.length() ; k++ )
            {
            	String str = String.valueOf( s.charAt( k ) );
            	int val;
            	if( str.equals( "." ) )
            		val = 0;
            	else
            		val = Integer.parseInt( str );
                mys[i] = val;
                i++;
            }
        }
        b.close();
		return mys;
    }	
}
