package saves;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.mcfeod.robots4droid.HostActivity;

public class SaveWindowActivity extends HostActivity {
    private static final String TAG = "SaveWindowActivity";
    @Override
    protected Fragment createFragment(){
        int saveNumber = getIntent().getIntExtra(SaveFragment.SAVED_GAME_NUMBER, -1);
        if(saveNumber < 0){
            Log.d(TAG,"No SAVED_GAME_NUMBER extra in Intent!");
            throw new RuntimeException("No SAVED_GAME_NUMBER extra in Intent!");
        }else{
            Log.d(TAG,"SaveFragment.newInstance() called");
            return SaveFragment.newInstance(saveNumber);
        }
    }
}
