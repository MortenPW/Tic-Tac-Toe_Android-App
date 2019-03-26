package no.woact.wilmor16.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Morten
 */

@Dao
public interface HighscoreDao {

    @Query("SELECT * FROM highscore")
    List<Highscore> fetchAllData();

    @Query("SELECT * FROM highscore ORDER BY score DESC LIMIT 10")
    List<Highscore> fetchHighscores();

    @Query("SELECT * FROM highscore where name LIKE :name")
    Highscore findByName(String name);

    @Query("SELECT COUNT(*) from highscore")
    int countScores();

    @Insert
    void insertSingleRecord(Highscore highscore);

    @Delete
    void delete(Highscore highscore);

    @Query("DELETE FROM highscore WHERE uid NOT IN" +
            "(SELECT uid FROM highscore ORDER BY score DESC LIMIT 10)")
    void cleanScores();

    @Query("DELETE FROM highscore")
    void resetTable();
}