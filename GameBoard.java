

package slider.puzzle.sliderpuzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TableRow;


public final class GameBoard {

   private static GameBoard board = null; // Singleton instance - can be
                                 // changed by calling
                                 // createGameBoard class method
   private List<Tile> tiles = null;
   private List<TileView> tileViews = null;
   private List<TableRow> tableRow = null;
   private Tile theBlankTile; // The empty square
   private Bitmap bitmap; // Picture used for puzzle
   private TableLayout parentLayout;
   private short gridSize; // This number represents the row and column count
                      // 3 = 3x3; 4 = 4x4; 5 = 5x5; etc.
   private Context context;
   private int boardWidth; // pixel count
   private int boardHeight; // pixel count
   private int moveCount; // number of tile clicks from the user (score)

   
   private GameBoard(Context context,
                 Bitmap bitmap,
                 TableLayout parentLayout,
                 int width,
                 int height,
                 short gridSize) {      
      this.context = context;
      this.boardWidth = width;
      this.boardHeight = height;
      this.bitmap = Bitmap.createScaledBitmap(bitmap,
            this.boardWidth,
            this.boardHeight,
            true);
      this.moveCount = 0;      
      this.parentLayout = parentLayout;
      this.gridSize = gridSize;
      init();
   }

   public static GameBoard createGameBoard(Context context,
                                 Bitmap bitmap,
                                 TableLayout parentLayout,
                                 int width,
                                 int height,
                                 short gridSize) {

      board = new GameBoard(context,
                       bitmap,
                       parentLayout,
                       width,
                       height,
                       gridSize);
       
      return board;
   }
   
 
   private void init() {
      initializeLists();  
      createTiles();
      createTileViews();
      shuffleTiles();
   }

 
   private void initializeLists() {
      if (tiles == null) {
         tiles = new ArrayList<Tile> (gridSize * gridSize);
      } else {
         
         for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).freeBitmap();
            tiles = new ArrayList<Tile> (gridSize * gridSize);
         }
      }
      tileViews = new ArrayList<TileView> (gridSize * gridSize);
      tableRow = new ArrayList<TableRow> (gridSize);

      for (int row = 0; row < gridSize; row++) {
         tableRow.add(new TableRow(context));            
      }
   }

  
   private void createTiles() {
      int   tile_width = bitmap.getWidth() / gridSize;
      int tile_height = bitmap.getHeight() / gridSize;

      for (short row = 0; row < gridSize; row++) {
         for (short column = 0; column < gridSize; column++) {
            Bitmap bm = Bitmap.createBitmap(bitmap,
                  column * tile_width,
                  row * tile_height,
                  tile_width,
                  tile_height);

            // if final, Tile -> blank
            if ((row == gridSize - 1) && (column == gridSize - 1)) {
               bm = Bitmap.createBitmap(tile_width,
                                  tile_height,
                                  bm.getConfig());
               bm.eraseColor(Color.BLACK);
               theBlankTile = new Tile(bm, row, column);
               tiles.add(theBlankTile);
            } else {
               tiles.add(new Tile(bm, row, column));
            }            
         } // end column        
      } // end row
      bitmap.recycle();
   }  

   
   private void createTileViews() {      
      for (short row = 0; row < gridSize; row++) {
         for (short column = 0; column < gridSize; column++) {
            TileView tv = new TileView(context, row, column);
            tileViews.add(tv);            
            tableRow.get(row).addView(tv);
         } // end column
         parentLayout.addView(tableRow.get(row));
      } // end row
   }

  
   public void shuffleTiles() {
      do {
         Collections.shuffle(tiles);
         
         // Place the blank tile at the end
         tiles.remove(theBlankTile);
         tiles.add(theBlankTile);
         
         for (short row = 0; row < gridSize; row++) {
            for (short column = 0; column < gridSize; column++) {
               tileViews.get(row * gridSize + column).setCurrentTile(
                     tiles.get(row * gridSize + column));
            }
         }
      } while (!isSolvable());
      moveCount = 0;
   }


   public static void notifyTileViewUpdate(TileView tv) {
      board.tileViewUpdate(tv);
   }


   private void tileViewUpdate(TileView tv) {
      swapTileWithBlank(tv);
   }
   
 
   public int getMoveCount() {
      return moveCount;
   }

  
   private boolean isSolvable() {
      short permutations = 0; 
      short currentTileViewLocationScore;
      short subsequentTileViewLocationScore;
     
     
      for (int i = 0; i < tiles.size() - 2; i++) {
         Tile tile = tiles.get(i);

       
         currentTileViewLocationScore = computeLocationValue(
               tile.getCorrectLocation());
         
       
         for (int j = i + 1; j < tiles.size() - 1; j++)
         {
            Tile tSub = tiles.get(j);
           
            subsequentTileViewLocationScore = computeLocationValue(
                  tSub.getCorrectLocation());
               
     
            if (currentTileViewLocationScore >
                  subsequentTileViewLocationScore) {
               permutations++;
            }
         }
      }
     
      return permutations % 2 == 0;
   }

  
   private boolean isCorrect() {
      // if a single tile is incorrect, return false
      for (Tile tile : tiles) {
         if (!tile.isCorrect()) {
            return false;
         }
      }
      return true;
   }

  
   @SuppressWarnings("deprecation")
private void swapTileWithBlank(TileView tv) {
      Tile tile = tv.getCurrentTile();
     
      TileView theBlankTileView = tileViews.get(
            computeLocationValue(theBlankTile.getCurrentLocation()));

      if (tile.getCurrentLocation().isAdjacent(
            theBlankTile.getCurrentLocation())) {
         
         // Animate tile movement
         if (tile.getCurrentLocation().getColumn() <
            theBlankTile.getCurrentLocation().getColumn()) {
            theBlankTileView.bringToFront();
            //LEFT
            theBlankTileView.startAnimation(AnimationUtils.loadAnimation(
                  this.context, R.anim.left_animation));
           
         } else if (tile.getCurrentLocation().getColumn() >
                  theBlankTile.getCurrentLocation().getColumn()) {
            theBlankTileView.bringToFront();
            //RIGHT
            theBlankTileView.startAnimation(AnimationUtils.loadAnimation(
                  this.context, R.anim.right_animation));
           
         } else if (tile.getCurrentLocation().getRow() <
                  theBlankTile.getCurrentLocation().getRow()) {
            theBlankTileView.bringToFront();
            //UP            
            theBlankTileView.startAnimation(AnimationUtils.loadAnimation(
                  this.context, R.anim.up_animation));
           
         } else if (tile.getCurrentLocation().getRow() >
                  theBlankTile.getCurrentLocation().getRow()) {
            theBlankTileView.bringToFront();
            //DOWN
            theBlankTileView.startAnimation(AnimationUtils.loadAnimation(
                  this.context, R.anim.down_animation));
         }        
         theBlankTileView.setCurrentTile(tile);
         tv.setCurrentTile(theBlankTile);
         moveCount++;
         
         parentLayout.invalidate();
      }            

      if (isCorrect()) {
         ((Activity)context).showDialog(PuzzleActivity.DIALOG_COMPLETED_ID);
      }
   }

   
   private short computeLocationValue(short row, short column) {
      return (short) (gridSize * row + column);
   }
   

   private short computeLocationValue(TileLocation location) {
      return computeLocationValue(location.getRow(), location.getColumn());
   }
   
  
   public void setNumbersVisible(boolean visible) {
      for (TileView tv : tileViews) {
         tv.setNumbersVisible(visible);
      }
   }
   
 
   public short getGridSize() {
      return gridSize;
   }
}

