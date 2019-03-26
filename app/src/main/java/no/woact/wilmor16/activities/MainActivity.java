package no.woact.wilmor16.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import no.woact.wilmor16.R;
import no.woact.wilmor16.fragments.LogoFragment;
import no.woact.wilmor16.fragments.MenuFragment;

/**
 * Created by Morten
 */

public class MainActivity extends AppCompatActivity {

    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, "menuFragment");
        }
        else mFragment = new MenuFragment();

        replaceFragment(R.id.constraintTop, new LogoFragment());
        replaceFragment(R.id.constraintMiddle, mFragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save the fragment's instance
        getSupportFragmentManager().putFragment(state, "menuFragment", mFragment);
    }

    protected void replaceFragment(int rID, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(rID, fragment);
        transaction.commit();
    }
}
