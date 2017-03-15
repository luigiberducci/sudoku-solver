import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings("serial")
/**
 * Risolutore parallelo del gioco Sudoku 
 * usando paradigma Fork-Join.
 */
public class myParallelSolver extends RecursiveTask<BigInteger> {	
	private SudokuMatrix  myMatrix;					//Oggetto SudokuMatrix da risolvere
	private static boolean ACTIVE_CUTOFF = true;	//Flag che attiva/disattiva l'ottimizzazione con cutoff
	private static int SEQ_CUTOFF = 30;				//Limite del cutoff sequenziale
	private boolean ACTIVE_REUSE_THREAD=true;         // flag di attivazione per l'ottimizzazione del thread padre
	BigInteger Threads=new BigInteger("1");         //contatore  forked solvers

	/**
	 * Costruttore da array di interi
	 * @param vector array di interi da cui creare oggetto SudokuMatrix
	 */
	myParallelSolver( int[] vector ){

		myMatrix = new SudokuMatrix( vector );

	}
	
	/**
	 * Costruttore da altro oggetto SudokuMatrix
	 * @param sM	oggetto sudokuMatrix da copiare
	 */
	myParallelSolver( SudokuMatrix sM ){
		myMatrix = sM;


	}
	
	@Override
	/**
     * Risoluzione parallelizzata dell'oggetto SudokuMatrix
     * secondo i canoni del gioco Sudoku
     * @return numero di soluzioni
     */
	protected BigInteger compute() {	
		BigInteger solCount = new BigInteger("0");
		
		if ( !(myMatrix.checkValidita() ) ) //Se è stata violata la consistenza dell'oggetto SudokuMatrix, 
            return new BigInteger( "0" ) ;	//sono arrivato ad una configurazione che non ammette soluzioni ( conto 0 soluzioni )
                
        myMatrix.riempiSingoletti();
         
        if ( myMatrix.checkSolution() )		//Se l'oggetto SudokuMatrix è risolto,
            return new BigInteger( "1" ) ;	//conto 1 soluzione
          
		if( ACTIVE_CUTOFF )		//Controllo se è abilitata l'ottimizzazione con cutoff sequenziale
		{
			if ( myMatrix.alreadyAssigned > SEQ_CUTOFF )	//Controllo se è stato raggiunto il limite
			{																		//Se è stato raggiunto
			mySequentialSolver seqExecution = new mySequentialSolver( myMatrix );	//creo un'esecuzione sequenziale
			seqExecution.solve();													//e la lancio
			solCount = solCount.add( seqExecution.solCounter );
			return solCount;
			}
		}         
	        
        int posMinimo = myMatrix.minPossibilityCell();		//Estraggo la posizione dell'elemento con minor numero di candidati validi
		ArrayList<myParallelSolver> helpersList = new ArrayList<myParallelSolver>();	//Collezione di helper threads
		ArrayList<SudokuMatrix> copiesList = new ArrayList<SudokuMatrix>();	//Collezione di helper threads
	   
		//Fase di creazione degli helper threads
		for ( int b = 0; b < 9; b++ ) 
		{														//Scorro array booleano dei candidati scartati per
			if( ! myMatrix.candidatiScartati[ posMinimo ][b] ){	//individuare i candidati validi ( =false )
				SudokuMatrix copy = new SudokuMatrix( myMatrix );		//Creo una copia della sudokuMatrix su cui lavoro	
				copy.setCell( posMinimo, Main.gridSymbol.values()[b] );	//e la modifico con il valore scelto tra i candidati
	    		copy.aggiornaCandidati(posMinimo);		//Aggiorno le collezioni di supporto della nuova SudokuMatrix
	            copiesList.add( copy );					//Aggiungo la matrice ottenuta alla lista di copie da risolvere
			}									
	    }
		
		//Fase di forking

		for( int b = 0; b < copiesList.size() ; b++ )	//Scorro la lista delle SudokuMatrix da risolvere
			{											
				SudokuMatrix currentCopy = copiesList.get( b );
				if( b == (copiesList.size()-1) && ACTIVE_REUSE_THREAD )	 //Se è l'ultima,
				{														//ottimizzo eseguendola sul thread corrente
					myMatrix = new SudokuMatrix( currentCopy );
					solCount = solCount.add( this.compute() );
				}
				else
				{

					myParallelSolver helper = new myParallelSolver( currentCopy );	//Altrimenti, creo un helper thread
					helper.fork();													//e eseguo il fork
					helpersList.add( helper );										//Aggiungendolo infine alla lista degli helper threads
				}
			}
				
		//Fase di joining
		for( myParallelSolver solver : helpersList )	//Scorro la lista degli helper threads
		{
			solCount = solCount.add(solver.join());    // per ciascuno richiamo il join
			Threads = Threads.add(solver.Threads);     // e aggiungo il  fork di ogni helper
		}
	    
        return solCount;		
	}
}