package no.woact.wilmor16.models;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Morten
 */

public class BoardModel {

    private long mTimer;
    private int[] mBoardPosition;

    private int mPlayerTurn;
    private boolean mPlayer1StartedLast, mGameOver;

    private PlayerModel mPlayer1, mPlayer2;

    public BoardModel() {
        mPlayer1 = new PlayerModel();
        mPlayer2 = new PlayerModel();

        mBoardPosition = new int[9];

        // Random starting player
        Random rand = new Random();
        int starting = rand.nextInt(2) + 1;

        if (starting == 1) {
            mPlayerTurn = 1;
            mPlayer1StartedLast = true;
        }
        else if (starting == 2) {
            mPlayerTurn = 2;
            mPlayer1StartedLast = false;
        }
        mGameOver = false;
        mTimer = SystemClock.elapsedRealtime();
    }

    public PlayerModel getmPlayer1() {
        return mPlayer1;
    }

    public PlayerModel getmPlayer2() {
        return mPlayer2;
    }

    public int getmPlayerTurn() {
        return mPlayerTurn;
    }

    public void setmPlayerTurn(int mPlayerTurn) {
        this.mPlayerTurn = mPlayerTurn;
    }

    public boolean game_over() {
        return mGameOver;
    }

    public void setmGameOver(boolean mGameOver) {
        this.mGameOver = mGameOver;
    }

    public int[] getmBoardPosition() {
        return mBoardPosition;
    }

    public void setmBoardPosition(int[] mBoardPosition) {
        this.mBoardPosition = mBoardPosition;
    }

    public void setBoardPositionMove(int position, int player) {
        this.mBoardPosition[position] = player;
    }

    public boolean ismPlayer1StartedLast() {
        return mPlayer1StartedLast;
    }

    public void setmPlayer1StartedLast(boolean mPlayer1StartedLast) {
        this.mPlayer1StartedLast = mPlayer1StartedLast;
    }

    public void nextMatch() {
        mPlayer1.resetTime();
        mPlayer2.resetTime();

        mBoardPosition = new int[9];

        // Ensure players start each their turn
        if (mPlayer1StartedLast) {
            mPlayerTurn = 2;
        }
        else mPlayerTurn = 1;

        mPlayer1StartedLast = !mPlayer1StartedLast;

        mGameOver = false;
        mTimer = SystemClock.elapsedRealtime();
    }

    public void timeUpdater() {
        long endTime = SystemClock.elapsedRealtime();
        long elapsedMilliSeconds = endTime - mTimer;

        // Add time to player holding turn
        if (mPlayerTurn == 1) {
            mPlayer1.addTime(elapsedMilliSeconds);
        }
        else {
            mPlayer2.addTime(elapsedMilliSeconds);
        }

        // Reset
        mTimer = SystemClock.elapsedRealtime();
    }

    public List<Integer> getAvailablePositions(int[] positions) {
        List<Integer> availablePositions = new ArrayList<>();
        for (int position = 0; position <= positions.length - 1; ++position) {
            if (positions[position] == 0) {
                availablePositions.add(position);
            }
        }
        return availablePositions;
    }

    public void gameOverUpdater() {
        // Previous turn is winner
        int check = gameOverConditions(this.mBoardPosition);

        // Avoid re-adding score of a finished game during activity restart
        if (check > 0 && !mGameOver) {
            if (mPlayerTurn == 2) {
                mPlayer1.addScore();
            }

            else if (mPlayerTurn == 1) {
                mPlayer2.addScore();
            }
        }
        if (check >= 0) mGameOver = true;
    }

    // Functional style for easy test-evaluations and minimax-algorithm valuations
    // (copying such a small array creates minimal overhead- the juice is worth the squeeze)
    public int gameOverConditions(int[] positions) {
        // Board reference (can be improved with magic squares):
        //                  0 1 2
        //                  3 4 5
        //                  6 7 8

        // Check each player for winning positions (might occur even if board is full)
        for (int i = 1; i <= 2; ++i) {
            if (rowsWon(positions, i)
                || columnsWon(positions, i)
                || diagonalsWon(positions, i)) {

                // We have a winner
                return i;
            }
        }
        return boardFull(positions);
    }

    private int boardFull(int[] positions) {
        // Check if board is full
        int count = 0;
        for (int i = 0; i <= positions.length - 1; ++i) {
            if (positions[i] != 0) {
                ++count;
            }
        }
        if (count == positions.length) {
            return 0;
        }
        return -1;
    }

    private boolean rowsWon(int[] positions, int player) {
        // Vertically
        for (int i = 0; i <= 6; i += 3) {
            if (positions[i] == player &&
                    positions[i+1] == player &&
                    positions[i+2] == player) {
                return true;
            }
        }
        return false;
    }

    private boolean columnsWon(int[] positions, int player) {
        // Horizontally
        for (int i = 0; i <= 2; ++i) {
            if (positions[i] == player &&
                    positions[i+3] == player &&
                    positions[i+6] == player) {
                return true;
            }
        }
        return false;
    }

    private boolean diagonalsWon(int[] positions, int player) {
        // Diagonally
        if (positions[0] == player &&
                positions[4] == player &&
                positions[8] == player) {
            return true;
        }

        else if (positions[2] == player &&
                positions[4] == player &&
                positions[6] == player) {
            return true;
        }
        return false;
    }

    public boolean isHumanTurn() {
        // Check if human
        if (mPlayerTurn == 1) {
            // This would be replaced with a list if more than two players
            return getmPlayer1().ismHuman();
        }
        else {
            return getmPlayer2().ismHuman();
        }
    }

    // Not really the best move at all times- but it's funny when it seems to play with us
    public int findBestMove(int[] boardPosition) {
        int bestValue = Integer.MIN_VALUE;
        int bestMove = -1;

        int player = mPlayerTurn;
        int opponent = (player == 1) ? 2 : 1;

        for (int i = 0; i <= boardPosition.length -1; ++i) {
            if (boardPosition[i] == 0) {
                boardPosition[i] = player;

                int value = minimax(boardPosition, 0, opponent);

                // Find move corresponding to highest value
                if (value >= bestValue) {
                    bestValue = value;
                    bestMove = i;
                }
                boardPosition[i] = 0; // Reset
            }
        }
        return bestMove;
    }

    private int evaluateScore(int winner) {
        if (winner == 0) {
            return 0;
        } else if (winner == mPlayerTurn) {
            return 1;
        } else {
            return -1;
        }
    }

    private int minimax(int[] boardPosition, int depth, int player) {
        // If terminal state
        int score = gameOverConditions(boardPosition);
        if (score >= 0) {
            return evaluateScore(score);
        }

        boolean isMax = player == mPlayerTurn;
        int opponent = (player == 1) ? 2 : 1;
        int bestValue = (isMax) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i <= boardPosition.length -1; ++i) {
            if (boardPosition[i] == 0) {
                boardPosition[i] = player;

                // Maximizing
                if (isMax) {
                    bestValue = Math.max(bestValue,
                            minimax(boardPosition, depth + 1, opponent));
                }

                // Minimizing
                else {
                    bestValue = Math.min(bestValue,
                            minimax(boardPosition, depth + 1, opponent));
                }
                boardPosition[i] = 0; // Reset
            }
        }
        return bestValue;
    }
}