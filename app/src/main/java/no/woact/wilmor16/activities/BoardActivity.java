package no.woact.wilmor16.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import no.woact.wilmor16.R;
import no.woact.wilmor16.fragments.BoardFragment;

/**
 * Created by Morten
 */

public class BoardActivity extends AppCompatActivity {

    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        if (savedInstanceState != null) {
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, "boardFragment");
        }
        else {
            mFragment = new BoardFragment();
            mFragment.setArguments(getIntent().getExtras());
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.constraintRoot, mFragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(state, "boardFragment", mFragment);
    }
}
