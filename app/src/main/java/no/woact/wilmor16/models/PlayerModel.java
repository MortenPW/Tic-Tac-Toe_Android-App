package no.woact.wilmor16.models;

/**
 * Created by Morten
 */

public class PlayerModel {
    private String mName;
    private double mTime;
    private int mScore;
    private boolean mHuman;

    public PlayerModel() {
        mName = "";
        mTime = 0;
        mScore = 0;
        mHuman = false;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String name) {
        this.mName = name;
    }

    public double getmTime() {
        return mTime;
    }

    public void setmTime(double time) {
        this.mTime = time;
    }

    public int getmScore() {
        return mScore;
    }

    public void setmScore(int score) {
        this.mScore = score;
    }

    public boolean ismHuman() {
        return mHuman;
    }

    public void setmHuman(boolean human) {
        this.mHuman = human;
    }

    public void addScore() {
        mScore++;
    }

    public void addTime(long milliSeconds) {
        mTime += milliSeconds;
    }

    public void resetTime() {
        mTime = 0;
    }
}
