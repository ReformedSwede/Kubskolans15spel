
import java.util.Random;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * Generate solvable markings for the fifteen game
 * 
 * @author Uno Holmer 
 * @version 2015-01-23
 */
public class Scrambler implements Iterable<Integer> {
    public final static int BLANK_MARKING = 0;
    private int gameSize;
    private static Random random = new Random();
    private int[] marks;
    private int modCount = 0;
    
    Scrambler(int gameSize) {
        this.gameSize = gameSize;
        marks = new int[gameSize*gameSize];
        generateMarks();
    }
    
    /**
     * generateMarks  Compute a random solvable configuration for the fifteen game.
     */
    public void generateMarks() {
        // Start with the solution configuration.
        for ( int i = 1; i < marks.length; i++ )
            marks[i-1] = i;
        int blankpos = marks.length-1;
        marks[blankpos] = BLANK_MARKING;
        // Shuffle the configuration, respecting the rules of the game.
        for ( int i = 0; i < 10000; i++ ) {
            int randpos = randomNeighbour(blankpos);
            swap(marks,blankpos,randpos);
            blankpos = randpos;
        }
        modCount++;
    }
    
    /**
     * randomNeighbour  compute a random neighbour to cell position i.
     *
     * @param i  The position of a cell.
     * @return  The position of a random neighbour to i.
     */
    private int randomNeighbour(int i) {
        int[] neighbours = new int[4];
        int n = 0;
        if ( i%gameSize != 0 )           neighbours[n++] = i-1;
        if ( i >= gameSize )             neighbours[n++] = i-gameSize;
        if ( (i+1)%gameSize != 0 )       neighbours[n++] = i+1;
        if ( i < gameSize*(gameSize-1) ) neighbours[n++] = i+gameSize;
        return neighbours[random.nextInt(n)];
    }
    
    private void swap(int[] a,int i,int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
    
    public Iterator<Integer> iterator() {
        return new GameIterator();
    }
    
    private class GameIterator implements Iterator<Integer> {
        private int index = 0;
        private final int expectedModCount = modCount;
        
        public boolean hasNext() {
            if ( modCount != expectedModCount )
                throw new ConcurrentModificationException();
            return index < marks.length;
        }
          
        public Integer next() {
            if ( ! hasNext() )
                throw new NoSuchElementException();
            return marks[index++];
        }
    }
}















