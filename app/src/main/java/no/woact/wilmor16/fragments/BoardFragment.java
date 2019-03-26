package no.woact.wilmor16.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import no.woact.wilmor16.R;
import no.woact.wilmor16.activities.ScoreActivity;
import no.woact.wilmor16.database.DatabaseSingleton;
import no.woact.wilmor16.database.Highscore;
import no.woact.wilmor16.models.BoardModel;

/**
 * Created by Morten
 */

public class BoardFragment extends Fragment {

    private TextView mRoundTimer, mPlayersTurn, mPlayer1Timer, mPlayer2Timer,
             mPlayer1Name, mPlayer2Name, mTextViewPlayer1Score, mTextViewPlayer2Score;

    private List<Button> mButtons;

    private Handler mHandler, mDelayHandler;
    private Runnable mRunnable, mDelayRunnable;

    private BoardModel mModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        mHandler = new Handler();
        mDelayHandler = new Handler();
        mModel = new BoardModel();

        mModel.getmPlayer1().setmHuman(getArguments().getBoolean("toggle_human"));
        mModel.getmPlayer2().setmHuman(getArguments().getBoolean("toggle_human2"));

        mPlayer1Name = view.findViewById(R.id.player1_name);
        mModel.getmPlayer1().setmName(getArguments().getString("input_name"));
        mPlayer1Name.setText(mModel.getmPlayer1().getmName());

        mPlayer2Name = view.findViewById(R.id.player2_name);
        mModel.getmPlayer2().setmName(getArguments().getString("input_name2"));
        mPlayer2Name.setText(mModel.getmPlayer2().getmName());

        mPlayersTurn = view.findViewById(R.id.players_turn);
        mRoundTimer = view.findViewById(R.id.round_timer);
        mPlayer1Timer = view.findViewById(R.id.player1_time);
        mPlayer2Timer = view.findViewById(R.id.player2_time);

        mTextViewPlayer1Score = view.findViewById(R.id.player1_score);
        mTextViewPlayer2Score = view.findViewById(R.id.player2_score);

        mButtons = new ArrayList<>();
        mButtons.add((Button) view.findViewById(R.id.btn_0));
        mButtons.add((Button) view.findViewById(R.id.btn_1));
        mButtons.add((Button) view.findViewById(R.id.btn_2));
        mButtons.add((Button) view.findViewById(R.id.btn_3));
        mButtons.add((Button) view.findViewById(R.id.btn_4));
        mButtons.add((Button) view.findViewById(R.id.btn_5));
        mButtons.add((Button) view.findViewById(R.id.btn_6));
        mButtons.add((Button) view.findViewById(R.id.btn_7));
        mButtons.add((Button) view.findViewById(R.id.btn_8));

        // Init controller
        eventController();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // Model logic
            mModel.setmPlayerTurn(savedInstanceState.getInt("turn"));
            mModel.getmPlayer1().setmTime(savedInstanceState.getDouble("player1_time", 0));
            mModel.getmPlayer2().setmTime(savedInstanceState.getDouble("player2_time", 0));
            mModel.getmPlayer1().setmScore(savedInstanceState.getInt("player1_score"));
            mModel.getmPlayer2().setmScore(savedInstanceState.getInt("player2_score"));
            mModel.setmBoardPosition(savedInstanceState.getIntArray("boardPosition"));
            mModel.setmPlayer1StartedLast(savedInstanceState.getBoolean("starting_player"));
            mModel.getmPlayer1().setmHuman(savedInstanceState.getBoolean("player1_human"));
            mModel.getmPlayer2().setmHuman(savedInstanceState.getBoolean("player2_human"));
            mModel.setmGameOver(savedInstanceState.getBoolean("game_over"));
            mModel.getmPlayer1().setmName(savedInstanceState.getString("player1_name"));
            mModel.getmPlayer2().setmName(savedInstanceState.getString("player2_name"));

            restoreBoardPositionView();
        }

        // Controller logic
        if (mModel.getmPlayerTurn() > 0) {
            startTimer();
        }
        else updateTimerView();

        // Set all views
        eventUpdater();

        // If AI
        if (!mModel.isHumanTurn()) {
            aiController();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Model logic
        state.putDouble("player1_time", mModel.getmPlayer1().getmTime());
        state.putDouble("player2_time", mModel.getmPlayer2().getmTime());
        state.putInt("turn", mModel.getmPlayerTurn());
        state.putInt("player1_score", mModel.getmPlayer1().getmScore());
        state.putInt("player2_score", mModel.getmPlayer2().getmScore());
        state.putIntArray("boardPosition", mModel.getmBoardPosition());
        state.putBoolean("player1_human", mModel.getmPlayer1().ismHuman());
        state.putBoolean("player2_human", mModel.getmPlayer2().ismHuman());
        state.putBoolean("starting_player", mModel.ismPlayer1StartedLast());
        state.putBoolean("game_over", mModel.game_over());
        state.putString("player1_name", mModel.getmPlayer1().getmName());
        state.putString("player2_name", mModel.getmPlayer2().getmName());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Kill delay timer to avoid timers crashing app
        mDelayHandler.removeCallbacks(mDelayRunnable);
    }

    // Basically our game loop only updating time- the rest is event-driven from button-clicks
    // Controller logic
    private void startTimer() {
        // Chronometer does not natively support milliseconds
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mModel.timeUpdater();
                updateTimerView();
                mHandler.postDelayed(this, 50);
            }
        };
        mRunnable.run();
    }

    // Controller logic
    private void stopTimer() {
        mHandler.removeCallbacks(mRunnable);
    }

    // View logic
    private void updateTimerView() {
        // Setting both views to initialize correctly from activity pauses
        double player1_time = mModel.getmPlayer1().getmTime();
        double player2_time = mModel.getmPlayer2().getmTime();

        mPlayer1Timer.setText(String.format(
                Locale.getDefault(), "%.2f", player1_time / 1000.0));

        mPlayer2Timer.setText(String.format(
                Locale.getDefault(), "%.2f", player2_time / 1000.0));

        mRoundTimer.setText(String.format(
                Locale.getDefault(), "%.2f", (player1_time + player2_time) / 1000.0));
    }

    // View logic
    private void playersScoreView() {
        String player1_score_formatted = "(" + String.valueOf(mModel.getmPlayer1().getmScore()) + ")";
        String player2_score_formatted = "(" + String.valueOf(mModel.getmPlayer2().getmScore()) + ")";
        mTextViewPlayer1Score.setText(player1_score_formatted);
        mTextViewPlayer2Score.setText(player2_score_formatted);
    }

    // View logic
    private void playersTurnView() {
        int turn = mModel.getmPlayerTurn();
        String player = "";

        if (turn == 1) {
            player = mModel.getmPlayer1().getmName();
            mPlayersTurn.setTextColor(getResources().getColor(R.color.player1_color));
        }
        else if (turn == 2) {
            player = mModel.getmPlayer2().getmName();
            mPlayersTurn.setTextColor(getResources().getColor(R.color.player2_color));
        }
        player += getString(R.string.turn);
        mPlayersTurn.setText(player);
    }

    // View logic
    private void endGameView() {
        int result = mModel.gameOverConditions(mModel.getmBoardPosition());

        if (result >= 0) {
            String player = "";

            // Tie
            if (result == 0) {
                mPlayersTurn.setTextColor(getResources().getColor(R.color.colorPrimary));
                mPlayersTurn.setText(getString(R.string.tie));
            }
            else {
                // Player 1 won
                if (result == 1) {
                    player = mPlayer1Name.getText().toString();
                    mPlayersTurn.setTextColor(getResources().getColor(R.color.player1_color));

                // Player 2 won
                } else if (result == 2) {
                    player = mPlayer2Name.getText().toString();
                    mPlayersTurn.setTextColor(getResources().getColor(R.color.player2_color));
                }
                player += getString(R.string.won);
                mPlayersTurn.setText(player);
            }
        }
    }

    // Controller and view logic
    private void requestNextMatch() {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        nextMatch();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked

                        // If not new high score, go back to menu
                        if (!newHighscore()) getActivity().onBackPressed();

                        break;
                }
            }
        };

        // Using alert dialog as snackbars disappear after some time
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.another_match).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener);

        // Disable back button on dialog
        builder.setCancelable(false);

        mDelayHandler.postDelayed(mDelayRunnable = new Runnable() {
            @Override
            public void run() {
                // Show dialog
                AlertDialog alertDialog = builder.show();

                // Disable clicking outside of dialog
                alertDialog.setCanceledOnTouchOutside(false);
            }
        }, 1000);
    }

    private boolean newHighscore() {
        // Added no history to board- avoids going back to board after rejecting
        // new game and being sent to high score activity
        boolean newHighscore = false;

        String name;
        int score;

        // Check highest score
        // TODO: Hva om begge har ny high score?
        if (mModel.getmPlayer1().getmScore() >= mModel.getmPlayer2().getmScore()) {
            name = mModel.getmPlayer1().getmName();
            score = mModel.getmPlayer1().getmScore();
        }
        else {
            name = mModel.getmPlayer2().getmName();
            score = mModel.getmPlayer2().getmScore();
        }

        // Check if new high score
        List<Highscore> highscores = null;
        try {
            highscores = DatabaseSingleton.getAppDatabase(getActivity()).loadHighscores();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (highscores != null && score != 0) {
            if (highscores.size() >= 10) {
                for (Highscore highscore : highscores) {
                    if (score > highscore.getScore()) {
                        newHighscore = true;
                        break;
                    }
                }
            }
            else {
                newHighscore = true;
            }
            if (newHighscore) {
                newHighscoreAlert(name, score);
            }
        }
        return newHighscore;
    }

    private void newHighscoreAlert(final String name, final int score) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.new_highscore);
        String text = String.format(getResources()
                .getString(R.string.new_highscore_message), name);

        builder.setMessage(text)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Send scores
                        DatabaseSingleton.insertHighscore(name, score);
                        Intent intent = new Intent(getActivity(), ScoreActivity.class);
                        startActivity(intent);
                    }
                });

        // Disable back button on dialog
        builder.setCancelable(false);

        // Show dialog
        AlertDialog alertDialog = builder.show();

        // Disable clicking outside of dialog
        alertDialog.setCanceledOnTouchOutside(false);
    }

    // Controller and view logic
    private void nextMatch() {
        mModel.nextMatch();
        eventUpdater();
        restoreBoardPositionView();
        startTimer();

        // If AI
        if (!mModel.isHumanTurn()) {
            aiController();
        }
    }

    // View logic
    private void restoreBoardPositionView() {
        int[] boardPosition = mModel.getmBoardPosition();

        for (int i = 0; i <= mButtons.size() -1; ++i) {
            Button b = mButtons.get(i);

            if (boardPosition[i] == 1) {
                b.setEnabled(false);
                b.setText(getString(R.string.player1_symbol));
                b.setTextColor(getResources().getColor(R.color.player1_color));
            }

            else if (boardPosition[i] == 2) {
                b.setEnabled(false);
                b.setText(getString(R.string.player2_symbol));
                b.setTextColor(getResources().getColor(R.color.player2_color));
            }

            else {
                b.setEnabled(true);
                b.setText(getString(R.string.empty_symbol));
            }
        }
    }

    // Controller logic
    public void aiController() {
        // Simulate thinking time
        Random rand = new Random();
        int n = rand.nextInt(1000) + 151; // 150-1000ms

        mDelayHandler.postDelayed(mDelayRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mModel.game_over()) {
                    // Make move after n ms
                    if (mModel.getAvailablePositions(
                            mModel.getmBoardPosition()).size() == mButtons.size()) {
                        // Random opening
                        makeSelection(mButtons.get(new Random().nextInt(mButtons.size())));
                    }
                    else {
                        makeSelection(mButtons.get(mModel.findBestMove(mModel.getmBoardPosition())));
                    }
                }
            }
        }, n);
    }

    // Controller logic
    private void eventController() {
        for (final Button b : mButtons) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventReceiver(b);
                }
            });
        }
    }

    // Controller and view logic
    private void eventReceiver(Button b) {
        // Enable buttons if not bots turn
        if (mModel.isHumanTurn()) {
            makeSelection(b);
        }
    }

    // Controller and view logic
    private void makeSelection(Button b) {
        b.setEnabled(false);

        if (mModel.getmPlayerTurn() == 1) {
            b.setText(getString(R.string.player1_symbol));
            b.setTextColor(getResources().getColor(R.color.player1_color));
            mModel.setBoardPositionMove(mButtons.indexOf(b), 1);

            // Next players turn
            mModel.setmPlayerTurn(2);

            // If player 2 not human, send control to ai
            if (!mModel.getmPlayer2().ismHuman()) {
                aiController();
            }
        } else {
            b.setText(getString(R.string.player2_symbol));
            b.setTextColor(getResources().getColor(R.color.player2_color));
            mModel.setBoardPositionMove(mButtons.indexOf(b), 2);

            // Next players turn
            mModel.setmPlayerTurn(1);

            // If player 2 not human, send control to ai
            if (!mModel.getmPlayer1().ismHuman()) {
                aiController();
            }
        }
        eventUpdater();
    }

    // Event-driven updater
    private void eventUpdater() {
        mModel.gameOverUpdater();

        if (mModel.game_over()) {
            stopTimer();

            for (Button b : mButtons) {
                b.setEnabled(false);
            }

            // Next match?
            requestNextMatch();
        }

        // View logic
        playersScoreView();
        playersTurnView();
        endGameView();
    }
}
