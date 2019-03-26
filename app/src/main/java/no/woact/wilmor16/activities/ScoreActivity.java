package no.woact.wilmor16.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import no.woact.wilmor16.R;
import no.woact.wilmor16.fragments.ScoreFragment;

/**
 * Created by Morten
 */

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.constraintRoot, new ScoreFragment());
        transaction.commit();
    }
}
