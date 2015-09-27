

package slider.puzzle.sliderpuzzle;

import java.io.FileNotFoundException;
import java.io.IOException;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;


public final class PuzzleActivity extends Activity {

   public static final int IMAGEREQUESTCODE = 8242008;
   public static final int DIALOG_PICASA_ERROR_ID = 0;
   public static final int DIALOG_GRID_SIZE_ID = 1;
   public static final int DIALOG_COMPLETED_ID = 2;
   private GameBoard board;
   private Bitmap bitmap; // temporary holder for puzzle picture
   private boolean numbersVisible = false; // Whether a title is displayed that
                                  // shows the correct location of the
                                 // tiles.

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//sets the orientation to portrait
      setContentView(R.layout.board);
      selectImageFromGallery();
   }    

   
   private void selectImageFromGallery() {
      Intent galleryIntent = new Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
      startActivityForResult(galleryIntent, IMAGEREQUESTCODE);
   }

   
   @SuppressWarnings("deprecation")
@Override
   protected final void onActivityResult(final int requestCode,
         final int resultCode,
         final Intent i) {
      super.onActivityResult(requestCode, resultCode, i);

      if (resultCode == RESULT_OK) {
         switch (requestCode) {
         case IMAGEREQUESTCODE:
            Uri imageUri = i.getData();
           
            try {
            	DisplayMetrics metric=new DisplayMetrics();
            	getWindowManager().getDefaultDisplay().getMetrics(metric);
            	Bitmap bm=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            	
               bitmap = Bitmap.createScaledBitmap(bm, (int)(metric.widthPixels*metric.density), (int)(metric.heightPixels*metric.density), true);
            } catch (FileNotFoundException e) {
            
               showDialog(DIALOG_PICASA_ERROR_ID);
            } catch (IOException e) {
               e.printStackTrace();
               finish();
            } catch (IllegalArgumentException e) {
               showDialog(DIALOG_PICASA_ERROR_ID);
            }
           
            createGameBoard(SettingsActivity.getGridSize(this));
            break;
         } // end switch
      } // end if
   }
   
   

 
   private final void createGameBoard(short gridSize) {
      DisplayMetrics metrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(metrics);
       
      TableLayout tableLayout;
      tableLayout = (TableLayout) findViewById(R.id.parentLayout);    
      tableLayout.removeAllViews();
     
      board = GameBoard.createGameBoard(this,
            bitmap,
            tableLayout,
            (int) (metrics.widthPixels * metrics.density),
            (int) (metrics.heightPixels * metrics.density),
            gridSize);
      board.setNumbersVisible(numbersVisible);
//      bitmap.recycle(); // free memory for this copy of the picture since the
                     // picture is stored by the GameBoard class
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.arranger_menu, menu);
      return true;
   }

  
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      boolean returnVal = true;

      switch (item.getItemId()) {
      case R.id.new_picture :
         selectImageFromGallery();
         break;
      case R.id.reshuffle:
         board.shuffleTiles();
         break;
      case R.id.settings:
         Intent i = new Intent(this, SettingsActivity.class);
         startActivity(i);
         break;
      default:
         returnVal = super.onOptionsItemSelected(item);
      }

      return returnVal;
   }
   
   @Override
   protected void onResume() {
      super.onResume();
      numbersVisible = SettingsActivity.isNumbersVisible(this);
     
      if (board == null) {
         return;
      }      
     
      board.setNumbersVisible(numbersVisible);      
      // Check if the size of the board has changed, since this puzzle was
      // started.  If so, create a new board.
      if (board.getGridSize() != SettingsActivity.getGridSize(this)) {
         short gridSize = SettingsActivity.getGridSize(this);
         createGameBoard(gridSize);
      }
   }

   @Override
   protected Dialog onCreateDialog(int id) {
      Dialog dialog;
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      switch(id) {
      case DIALOG_PICASA_ERROR_ID:
         builder.setMessage(R.string.picasa_error)
         .setCancelable(false)
         .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
               // After message is displayed, have the user pick again.
               selectImageFromGallery();
            }
         });
         dialog = builder.create();
         break;
      case DIALOG_COMPLETED_ID:
         builder.setMessage(createCompletionMessage())
         .setCancelable(false)
         .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
               board.shuffleTiles();
            }
         });
         dialog = builder.create();
         break;
      default:
         dialog = null;
      }
      return dialog;
   }
   
 
   @Override
   protected void onPrepareDialog(int id, Dialog dialog) {
      switch (id) {
      case DIALOG_COMPLETED_ID:
         ((AlertDialog) dialog).setMessage(createCompletionMessage());
         break;
      }
   }
   
 
   private String createCompletionMessage() {
      String completeMsg =
            getResources().getString(R.string.congratulations) + " "
            + String.valueOf(board.getMoveCount());
      String[] ins = getResources().getStringArray(R.array.insi);
      completeMsg += "\n";
      int insi = (int) Math.floor(Math.random() * ins.length);
      completeMsg += ins[insi];
     
      return completeMsg;
   }
} 