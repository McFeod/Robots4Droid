package saves;

import android.app.Fragment;
import android.util.Log;
import com.github.mcfeod.robots4droid.HostActivity;

class SaveActivity extends HostActivity {
    @Override
    protected Fragment createFragment(){
        int saveNumber = getIntent().getIntExtra(SaveFragment.SAVED_GAME_NUMBER, -1);
        if(saveNumber < 0){
            Log.d("SaveActivity Error","No SAVED_GAME_NUMBER extra in Intent!");
            // throw exception?
            throw new RuntimeException("No SAVED_GAME_NUMBER extra in Intent!");
        }else{
            Log.d("SaveActivity Report","SaveFragment.newInstance() called");
            return SaveFragment.newInstance(saveNumber);
        }
    }
}
