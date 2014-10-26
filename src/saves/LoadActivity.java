package saves;


import android.support.v4.app.Fragment;

import com.github.mcfeod.robots4droid.HostActivity;

public class LoadActivity extends HostActivity {
    @Override
    protected Fragment createFragment() {
        return new MyListFragment();
    }
}
