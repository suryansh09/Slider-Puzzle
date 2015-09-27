

package slider.puzzle.sliderpuzzle;


public final class TileLocation {
   
   private short row;
   private short column;
   
   private TileLocation(short row, short column) {
      this.row = row;
      this.column = column;
   }
   
   public static TileLocation getInstance(short row, short column) {
           return new TileLocation(row, column);
   }
   
   public short getRow() {
      return row;
   }
   public short getColumn() {
      return column;
   }
   
   
  
   public boolean equals(TileLocation other) {
      return (other.getColumn() == this.getColumn()) &&
            (other.getRow() == this.getRow());
   }
   
   
   public boolean isAdjacent(TileLocation other) {
      return ((other.getRow() == row &&
               (other.getColumn() == column + 1
               || other.getColumn() == column - 1))
            || (other.getColumn() == column &&
               (other.getRow() == row + 1
               || other.getRow() == row - 1)));
   }  
   
   
   public String toString(){
      return new String((row + 1) + "-" + (column + 1));
   }
}

