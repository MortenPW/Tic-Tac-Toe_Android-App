package no.woact.wilmor16.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Morten
 */

@Database(entities = {Highscore.class}, version = 1)
public abstract class DatabaseSingleton extends RoomDatabase {

    private static DatabaseSingleton mInstance;
    public abstract HighscoreDao highscoreDao();

    public static DatabaseSingleton getAppDatabase(Context context) {
        if (mInstance == null) {
            mInstance =
                    Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseSingleton.class, "highscore-database")
                            .build();
        }
        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    public List<Highscore> loadHighscores() throws ExecutionException, InterruptedException {
        return new RetrieveDataAsync().execute().get();
    }

    public static void insertHighscore(String name, int score) {
        new InsertDataAsync(new Highscore(name, score)).execute();
        new CleanDataAsync().execute(); // Ensure we delete old records
    }

    public void resetHighscores() {
        new ResetAllDataAsync().execute();
    }

    public static class RetrieveDataAsync extends AsyncTask<Void, Void, List<Highscore>> {
        @Override
        protected List<Highscore> doInBackground(Void... params) {
            return mInstance.highscoreDao().fetchHighscores();
        }
    }

    public static class InsertDataAsync extends AsyncTask<Void, Void, Boolean> {
        private final Highscore mHighscore;

        InsertDataAsync(Highscore highscore) {
            this.mHighscore = highscore;
        }

        // doInBackground runs on a worker thread
        @Override protected Boolean doInBackground(Void... objs) {
            mInstance.highscoreDao().insertSingleRecord(mHighscore);
            return null;
        }
    }

    public static class CleanDataAsync extends AsyncTask<Void, Void, Boolean> {
        // doInBackground runs on a worker thread
        @Override protected Boolean doInBackground(Void... objs) {
            mInstance.highscoreDao().cleanScores();
            return null;
        }
    }

    public static class ResetAllDataAsync extends AsyncTask<Void, Void, Boolean> {
        // doInBackground runs on a worker thread
        @Override protected Boolean doInBackground(Void... objs) {
            mInstance.highscoreDao().resetTable();
            return null;
        }
    }
}