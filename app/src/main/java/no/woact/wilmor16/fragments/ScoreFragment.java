package no.woact.wilmor16.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.concurrent.ExecutionException;

import no.woact.wilmor16.R;
import no.woact.wilmor16.database.DatabaseSingleton;
import no.woact.wilmor16.database.HighscoreAdapter;

/**
 * Created by Morten
 */

public class ScoreFragment extends Fragment {

    private ListView mListHighscore;
    private Button mBtnReset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);

        mBtnReset = view.findViewById(R.id.btn_reset_highscores);
        mListHighscore = view.findViewById(R.id.list_highscore);

        // Reset scores
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseSingleton.getAppDatabase(getActivity()).resetHighscores();
                onResume();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            mListHighscore.setAdapter(
                    new HighscoreAdapter(getContext(), DatabaseSingleton.getAppDatabase(getActivity()).loadHighscores()));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (mListHighscore.getAdapter().getCount() == 0) {
            getView().findViewById(R.id.no_scores).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.no_scores).setVisibility(View.GONE);
        }
    }

}