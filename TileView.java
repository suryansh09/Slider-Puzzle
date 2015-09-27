

package slider.puzzle.sliderpuzzle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.widget.TextView;


public final class TileView extends TextView {

   private Tile currentTile; // tile to be displayed
   private TileLocation myLocation; // permanent location on game board
   private String title; // the current tile's correct location (changes)
   private boolean numbersVisible = false; // should title be displayed
   
      public TileView(Context context, short row, short column) {
      super(context);
      this.myLocation = TileLocation.getInstance(row, column);
      super.setCursorVisible(false);
      super.setTypeface(Typeface.DEFAULT_BOLD);
      super.setTextColor(Color.RED);
   }
   
   @Override
   public boolean onTouchEvent(MotionEvent event) {
      GameBoard.notifyTileViewUpdate(this);
      return super.onTouchEvent(event);
   }  
   
  
   public boolean isTileCorrect() {
      return currentTile.isCorrect();
   }
   
   
   @SuppressWarnings("deprecation")
public void setCurrentTile(Tile tile) {  
      this.currentTile = tile;
      super.setBackgroundDrawable(new BitmapDrawable(tile.getBitmap()));
      this.currentTile.setCurrentLocation(myLocation);
      setTitle();
   }
   
   
   public Tile getCurrentTile() {
      return this.currentTile;
   }
   
   
   public void setNumbersVisible(boolean visible) {
      this.numbersVisible = visible;
      setTitle();
   }
   
   
   private void setTitle() {
      title = currentTile.getCorrectLocation().toString();
      if (numbersVisible) {
         super.setTextColor(Color.RED);
      } else {
         super.setTextColor(Color.TRANSPARENT);
      }
      super.setText(title);
   }
}
