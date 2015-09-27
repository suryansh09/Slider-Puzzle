

package slider.puzzle.sliderpuzzle;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity {

   @SuppressWarnings("deprecation")
@Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings);
   }
   
  
   public static boolean isNumbersVisible(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("show_numbers", false);
   }
   

   public static short getGridSize(Context context) {
      String gridSize = PreferenceManager.getDefaultSharedPreferences(context)
            .getString("grid_size", "3");  
      return Short.parseShort(gridSize);
   }
}

