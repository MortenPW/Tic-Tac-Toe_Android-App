package no.woact.wilmor16.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import no.woact.wilmor16.R;
import no.woact.wilmor16.activities.BoardActivity;
import no.woact.wilmor16.activities.ScoreActivity;

/**
 * Created by Morten
 */

public class MenuFragment extends Fragment {

    private EditText mInputName, mInputName2;
    private ToggleButton mToggleHuman, mToggleHuman2;

    private JokeFragment mJokeFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mInputName = view.findViewById(R.id.input_name);
        mInputName2 = view.findViewById(R.id.input_name2);

        mToggleHuman = view.findViewById(R.id.toggle_human);
        mToggleHuman2 = view.findViewById(R.id.toggle_human2);

        inputComponents();

        // Button play
        final Button btn_play = view.findViewById(R.id.btn_play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BoardActivity.class);

                if (mToggleHuman.isChecked()) {
                    intent.putExtra("toggle_human", true);

                    if (mInputName.getText().toString().length() <= 0 ||
                            mInputName.getText().toString().toLowerCase().contains(getString(R.string.bot_name).toLowerCase())) {
                        mInputName.setText(getString(R.string.player_name));
                    }
                }
                if (mToggleHuman2.isChecked()) {
                    intent.putExtra("toggle_human2", true);

                    if (mInputName2.getText().toString().length() <= 0 ||
                            mInputName2.getText().toString().toLowerCase().contains(getString(R.string.bot_name).toLowerCase())) {
                        mInputName2.setText(getString(R.string.player_name));
                    }
                }
                intent.putExtra("input_name", mInputName.getText().toString());
                intent.putExtra("input_name2", mInputName2.getText().toString());
                startActivity(intent);

                if (mJokeFragment != null) {
                    mJokeFragment.requestJoke();
                }
            }
        });

        // Button score
        final Button btn_score = view.findViewById(R.id.btn_score);
        btn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScoreActivity.class));

                if (mJokeFragment != null) {
                    mJokeFragment.requestJoke();
                }
            }
        });

        // Fragment in fragment
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mJokeFragment = new JokeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.constraintBottom, mJokeFragment);
            transaction.commit();
        }
        else {
            EmptyFragment emptyFragment = new EmptyFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.constraintBottom, emptyFragment);
            transaction.commit();
        }
        return view;
    }

    // Load input components
    private void inputComponents() {
        // Text field and toggle
        mInputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (mInputName.getText().toString().equals(getString(R.string.player_name))) {
                        mInputName.setText("");
                    }
                } else {
                    if (mInputName.getText().length() <= 0 ||
                        mInputName.getText().toString().toLowerCase().contains(getString(R.string.bot_name).toLowerCase())) {
                            mInputName.setText(R.string.player_name);
                    }
                }
            }
        });

        mToggleHuman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mInputName.setFocusableInTouchMode(true);
                    mInputName.setText(R.string.player_name);
                }
                else {
                    mInputName.setFocusable(false);
                    mInputName.setText(R.string.bot_name);
                }
            }
        });

        // Text field 2 and toggle
        mInputName2.setFocusable(false); // Init to false
        mInputName2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (mInputName2.getText().toString().equals(getString(R.string.player_name))) {
                        mInputName2.setText("");
                    }
                } else {
                    if (mInputName2.getText().length() <= 0 ||
                            mInputName2.getText().toString().toLowerCase().contains(getString(R.string.bot_name).toLowerCase())) {
                        mInputName2.setText(R.string.player_name);
                    }
                }
            }
        });

        mToggleHuman2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mInputName2.setFocusableInTouchMode(true);
                    mInputName2.setText(R.string.player_name);
                }
                else {
                    mInputName2.setFocusable(false);
                    mInputName2.setText(R.string.bot_name);
                }
            }
        });
    }
}