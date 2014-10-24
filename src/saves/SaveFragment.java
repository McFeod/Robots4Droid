package saves;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.github.mcfeod.robots4droid.R;

class SaveFragment extends Fragment {
    public static final String SAVED_GAME_NUMBER =
            "com.github.mcfeod.robots4droid.saved_game";

    private SavedGame mGame;
    private int mGameNumber;
    private TextView mSaveInfo;


    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        //
        mGameNumber = getArguments().getInt(SAVED_GAME_NUMBER);
        mGame = SaveManager.INSTANCE.mGames.get(mGameNumber);
        getActivity().setTitle("Save Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceBundle){
        View v = inflater.inflate(R.layout.fragment_save, parent, false);
        mSaveInfo = (TextView)v.findViewById(R.id.savedLevelInfo);
        mSaveInfo.setText(mGame.toString());

        Button deleteButton = (Button)v.findViewById(R.id.deleteSaveButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                SaveManager.INSTANCE.removeSave(mGameNumber, activity.getApplicationContext());
                activity.finish();
            }
        });

        Button playButton = (Button)v.findViewById(R.id.playSaveButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveManager.INSTANCE.rememberGame(mGameNumber);
                getActivity().finish();
            }
        });

        return v;
    }
    /*Используется для вызова фрагмента с передачей параметров*/
    public static Fragment newInstance(int saveNumber){
        Bundle args = new Bundle();
        args.putInt(SAVED_GAME_NUMBER, saveNumber);
        SaveFragment fragment = new SaveFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
