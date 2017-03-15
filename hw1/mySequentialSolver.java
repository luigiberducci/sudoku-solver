import java.math.BigInteger;

/**
 * Risolutore sequenziale del gioco Sudoku.
 */
public class mySequentialSolver {
	public SudokuMatrix  myMatrix;
    BigInteger  solCounter;

     
    /**
     * Costruttore da array di int
     * @param v array di int con numeri [0,9]
     */
    public mySequentialSolver(int[] v) 
    {
    	myMatrix = new SudokuMatrix( v );
    	myMatrix.calculateSolutionsSpace();
    }
    /**
     * Costruttore da altro SudokuMatrix
     * @param sM oggetto SudokuMatrix da copiare
     */
    public mySequentialSolver( SudokuMatrix sM ) 
    {
    	myMatrix = sM;
    }
     
    /**
     * Launcher dell'esecuzione della risoluzione del Sudoku
     */
    public void solve() 
    {
            solCounter = recursiveSolve( myMatrix );
    }
     
    /**
     * Risoluzione dell'oggetto SudokuMatrix passato in input
     * secondo i canoni del gioco Sudoku
     * @param myMatrix 	oggetto SudokuMatrix da risolvere
     * @return numero di soluzioni
     */
    private BigInteger recursiveSolve( SudokuMatrix myMatrix ) {
        BigInteger solCount = new BigInteger( "0" );

        if ( !(myMatrix.checkValidita() ) ) //Se è stata violata la consistenza dell'oggetto SudokuMatrix, 
            return new BigInteger( "0" ) ;	//sono arrivato ad una configurazione che non ammette soluzioni ( conto 0 soluzioni )
                
        myMatrix.riempiSingoletti();
         
        if ( myMatrix.checkSolution() )		//Se l'oggetto SudokuMatrix è risolto,
            return new BigInteger( "1" ) ;	//conto 1 soluzione
                 
        int posMinimo = myMatrix.minPossibilityCell();		//Estraggo la posizione dell'elemento con minor numero di candidati validi
         
        for ( int b = 0; b < 9; b++ ) {		//Scorro array booleano dei candidati scartati per individuare quelli validi (=false)
        	if( ! myMatrix.candidatiScartati[ posMinimo ][ b ] ){	
        		SudokuMatrix copyMatrix = new SudokuMatrix( myMatrix );				//Copio l'oggetto SudokuMatrix su cui lavoro
        		copyMatrix.setCell( posMinimo , Main.gridSymbol.values()[ b ] );	//Modifico la cella con il candidato scelto ( b-esimo )
        		copyMatrix.aggiornaCandidati( posMinimo );							//Aggiorno le collezioni di supporto
                solCount = solCount.add( recursiveSolve( copyMatrix ) );			//Richiamo risoluzione sulla matrice modificata
        	}
        }
        return solCount;
    }
}