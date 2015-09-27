

package slider.puzzle.sliderpuzzle;


import android.graphics.Bitmap;


public final class Tile {
   
   private TileLocation currentLocation; // current spot on game board
   private final TileLocation correctLocation; // where it's trying to go
   private Bitmap bitmap; // the partial picture that when combined with
                         // the other Tiles will create the original
                                // picture
   
  
   public Tile(Bitmap bitmap, short correctRow, short correctColumn) {
      this.bitmap = bitmap;
      currentLocation = TileLocation.getInstance(correctRow, correctColumn);
      correctLocation = TileLocation.getInstance(correctRow, correctColumn);
   }
   
  
   public TileLocation getCurrentLocation() {
      return currentLocation;
   }
   
  
   public TileLocation getCorrectLocation() {
      return correctLocation;
   }
   
   
   public boolean isCorrect() {
      return currentLocation.equals(correctLocation);
   }
   
  
   public void setCurrentLocation(TileLocation location) {
      this.currentLocation = location;
   }
   
   
   public Bitmap getBitmap() {
      return bitmap;
   }
   
   
   public void freeBitmap() {
      bitmap.recycle();
      bitmap = null;
   }
}

