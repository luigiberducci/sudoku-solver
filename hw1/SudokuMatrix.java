import java.math.BigInteger;

/**
 * Classe SudokuMatrix.
 */
public class SudokuMatrix 
{
	Main.gridSymbol[] valoriCelle;	//Array di valori v1,v2,...,v9 o null (valore ancora non determinato)
    boolean[][] candidatiScartati;	//Array di boolean[9], rappresenta vettore caratteristico dei candidati della cella
    								//se l'elemento i-esimo è false il valore i-esimo è un candidato della cella
    								//se l'elemento i-esimo è true il valore i-esimo non è un candidato della cella
    								//perchè è stato scartato,essendo già presente nella riga,nella colonna o nel box
    int[] contatoreCandidatiScartati;		//Array di interi che contano il numero di candidati scartati, 
    										//In altre parole è il numero di elementi true nel corrispondente elemento
    										//dell'array candidatiScartati
    int alreadyAssigned;				//Contatore delle celle già valorizzate
	public BigInteger ThreadCounter=new BigInteger("1");
    
    /**
     * Costruttore da array di interi
     * @param vector array di interi con simboli [0,9]
     */
    public SudokuMatrix( int[] vector ) 
    {
    	alreadyAssigned = 0;
    	valoriCelle = new Main.gridSymbol[ 81 ];
    	contatoreCandidatiScartati = new int[81];
        candidatiScartati = new boolean[81][9];

        //Valorizzo gli elementi della griglia sudoku
        for ( int i = 0 ; i < 81 ; i++ ) 
        {
        	if ( vector[ i ] != 0 ) 
        		setCell( i, Main.gridSymbol.values()[ vector[ i ] - 1 ]);
        }
             
        //Valorizzo le collezioni di supporto
        for ( int i = 0; i < 81; i++ )
        {
        	if ( valoriCelle[ i ] != null ) 
        		aggiornaCandidati( i );
        }
    }
         
    /**
     * Costruttore da altro SudokuMatrix
     * @param inputMatrix oggetto SudokuMatrix
     */
    public SudokuMatrix( SudokuMatrix inputMatrix ) 
    {
	    // costructor overload
	    valoriCelle = new Main.gridSymbol[ 81 ];
	    contatoreCandidatiScartati = new int[ 81 ];
	    candidatiScartati = new boolean[ 81 ][ 9 ];
	    alreadyAssigned = inputMatrix.alreadyAssigned;
	    
	    //Effettuo una copia del SudokuMatrix in input, elemento per elemento
	    for ( int i = 0 ; i < 81 ; i++ )
	    {
	    	valoriCelle[ i ] = inputMatrix.valoriCelle[ i ];
	       	contatoreCandidatiScartati[ i ] = inputMatrix.contatoreCandidatiScartati[ i ];
	        candidatiScartati[ i ] = inputMatrix.candidatiScartati[ i ].clone();
	    }
  	}
         
    /**
     * Verifica la legalità o meno dell'oggetto SudokuMatrix secondo i canoni del gioco Sudoku
     * Ogni elemento dell'array deve avere almeno un valore candidato,altrimenti la griglia non ha soluzione
     * per verificarlo controllo che il numero di candidati scartati non sia > 8 (ovvero =9, tutti i valori scartati)
     * @return 	true se la matrice è ancora un'istanza legale del Sudoku
     * 			false altrimenti.
     */
    public boolean checkValidita() 
    {
    	for ( int numCandidati : contatoreCandidatiScartati ) 
    	{
        	if ( numCandidati > 8 ) 
            	return false;
            
        }
    return true;
    }
             
    /**
     * Aggiorna le collezioni di supporto in base ai valori di valoriCelle
     * A seguito di un nuovo inserimento va quindi a scartare quel valore dagli elementi di riga,colonna o box
     * @param cella cella modificata
     */
    public void aggiornaCandidati( int cella ) {
            
    	final int value = valoriCelle[ cella ].ordinal();	//Valore espresso come int [0,9)
    	final int row = cella / 9;			//Riga della cella nel Sudoku
    	final int col = cella - row * 9;	//Colonna della cella nel Sudoku
    	final int box = (row/3)*27 + (col/3)*3;	//Box della cella nel Sudoku
            
    	int i, j, b;	//Indici ausiliari	
    					//( sarebbe possibile utilizzarne solo uno o accorpare i 3 for in un unico
    					//	ma ho preferito mantenere questa struttura per avere una buona legibilità del codice
    					//	e della logica che segue )
            
    	//Scorro gli elementi della riga
    	for ( int t = 0 ; t < 9 ; t++ )
    	{
    		i = row*9 + t;
    		if( valoriCelle[i] == null )
    		{
    			if( candidatiScartati[ i ][ value ] == false )
    			{
                	candidatiScartati[i][ value] = true;
    				contatoreCandidatiScartati[i]++;	
    			}   
    		}
    	}
            
    	//Scorro gli elementi della colonna
    	for ( int t = 0 ; t < 9 ; t++ )
    	{
    		j = col + t*9;
    		if( valoriCelle[j] == null )
    		{
    			if( candidatiScartati[ j ][ value ] == false )
    			{
                	candidatiScartati[j][ value] = true;
                	contatoreCandidatiScartati[j]++;
                }
            }
        }
    	//Scorro gli elementi del box    
    	for ( int t = 0 ; t < 9 ; t++ )
    	{
    		b = box + t + (t/3)*6;
    		if( valoriCelle[b] == null )
    		{
    			if( candidatiScartati[ b ][ value ] == false )
    			{
    				candidatiScartati[b][ value] = true;
    				contatoreCandidatiScartati[b]++;	
    			}
    		}   
    	}    
    }
             
    /**
     * Effettua riempimento dei "singoletti" ovvero quelle celle che hanno un unico candidato disponibile
     * e che quindi possiamo considerare come già valorizzate con tale candidato.
     * Per effettuare tale riempimento controllo le celle ancora vuote che hanno un numero di candidati scartati = 8
     * e quindi 1 solo candidato valido.    
     */
    public void riempiSingoletti() 
    {             
    	for( int index=0; index<81; index++)
    	{
    		if ( valoriCelle[ index ] == null ) 
    		{
    			if ( contatoreCandidatiScartati[ index ] == 8 ) 
    			{
    				for( int b=0; b<9; b++){						//Scorro i candidati disponibili ( =false )
    					if( candidatiScartati[index][b] == false )	//Trovato il primo e unico candidato,	
    					{
    						setCell( index, Main.gridSymbol.values()[ b ] );
    						aggiornaCandidati( index );	//Dato che ho inserito un nuovo valore, aggiorno collezioni di supporto
    					}
    				}   
    			}
    		}
    	}
    }
         
    /**
     * Verifica che il Sudoku non sia completo e quindi risolto.
     * Per verificarlo scorre tutti gli elementi della griglia e controlla che siano tutti valorizzati.    
     * @return	true, se il Sudoku è completo
     * 			false, altrimenti
     */
    public boolean checkSolution() 
    {    
    	if ( alreadyAssigned == 81 )
    		return true;
    	return false;    
    }
         
    /**
     * Metodo che imposta un valore ( value ) ad una cella ( cella )
     * @param cella		elemento dell'array a cui impostare valore
     * @param value		valore da impostare nella cella
     */       
    public void setCell(int cella , Main.gridSymbol value) 
    {    
    	valoriCelle[ cella ] = value;
    	alreadyAssigned++;
    }
         
    /**
     * Ricerca l'elemento della griglia con minor numero di candidati disponibili.    
     * Per ricercare il minimo scorre gli elementi cercando quelli con massimo numero di candidati scartati.
     * @return	posizione nell'array di tale elemento
     */
    public int minPossibilityCell() 
    {
    	int possibility = 0;
    	int pos = 0;    
    	for (int i = 0; i < 81; ++i) 
    	{
    		if ( valoriCelle[ i ] == null ) 
    		{            
    			if ( contatoreCandidatiScartati[ i ] > possibility ) 	//il contatore candidati funziona all'inverso
    			{
    				possibility = contatoreCandidatiScartati[ i ];		//più è alto più sono i valori non candidati alla cella
    				pos = i;									//Quindi per trovare il minimo cerco il valore max
    			}
    		}
    	}
    	return pos;   
    }

	public void calculateSolutionsSpace() 
	{
		BigInteger solSpace = new BigInteger("1");
		int candidatiDisponibili = 0;
		
		for ( int i = 0; i < valoriCelle.length; i++)
		{
			if ( valoriCelle[i] != null ) continue;
			candidatiDisponibili = 9 - contatoreCandidatiScartati[i];	
			solSpace = solSpace.multiply(new BigInteger(candidatiDisponibili+""));
		}
				
		System.out.println("Solution space: " + solSpace.toString() );
	}  
}