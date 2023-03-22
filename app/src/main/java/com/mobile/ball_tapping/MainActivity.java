package com.mobile.ball_tapping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mobile.ball_tapping.component.CircleView;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private TextView scoreText;
    private TextView timerText;
    private GridLayout circleGrid;
    private Button startButton;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String SCORE_KEY = "score";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        circleGrid = findViewById(R.id.circleGrid);
        startButton = findViewById(R.id.startButton);

        setupGame();
        startButton.setOnClickListener(v -> startGame());
    }

    private void setupGame() {
        ArrayList<CircleView> circleViews = new ArrayList<>();
        if (circleGrid.getChildCount() == 0) {
            for (int i = 0; i < 4; i++) {
                CircleView circleView = new CircleView(this, null);
                circleGrid.addView(circleView);
                circleViews.add(circleView);
            }
        } else {
            for (int i = 0; i < circleGrid.getChildCount(); i++) {
                CircleView circleView = (CircleView) circleGrid.getChildAt(i);
                circleView.setColor(Color.GRAY);
                circleViews.add(circleView);
            }
        }
    }


    private void startGame() {
        Random random = new Random();
        int[] score = {0};
        scoreText.setText("Score: 0");

        ArrayList<CircleView> circleViews = new ArrayList<>();
        for (int i = 0; i < circleGrid.getChildCount(); i++) {
            CircleView circleView = (CircleView) circleGrid.getChildAt(i);
            circleView.setColor(Color.GRAY);
            circleViews.add(circleView);
        }

        CountDownTimer timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format("00:%02d", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                showEndDialog(scoreText);
            }
        };

        timer.start();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleView circleView = (CircleView) v;
                circleView.setColor(Color.GREEN);
                circleView.setOnClickListener(null);
                score[0]++;
                scoreText.setText("Score: " + score[0]);
                lightRandomCircle(circleViews, random, this);

                boolean allCirclesClicked = true;
                for (CircleView cView : circleViews) {
                    if (cView.getColor() == Color.GRAY || cView.getColor() == Color.YELLOW) {
                        allCirclesClicked = false;
                        break;
                    }
                }

                if (allCirclesClicked) {
                    timer.cancel();
                    timerText.setText("00:00");
                    showEndDialog(scoreText);
                }
            }
        };

        lightRandomCircle(circleViews, random, clickListener);
    }

    private void lightRandomCircle(ArrayList<CircleView> circleViews, Random random, View.OnClickListener clickListener) {
        ArrayList<CircleView> unlitCircles = new ArrayList<>();

        for (CircleView circleView : circleViews) {
            if (circleView.getColor() == Color.GRAY) {
                unlitCircles.add(circleView);
            }
        }

        if (unlitCircles.isEmpty()) {
            return;
        }

        int index = random.nextInt(unlitCircles.size());
        CircleView circleView = unlitCircles.get(index);
        circleView.setColor(Color.YELLOW);
        circleView.setOnClickListener(clickListener);
    }

    private void showEndDialog(TextView scoreText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");
        builder.setMessage("Your score is: " + scoreText.getText().toString());
        builder.setPositiveButton("Restart", (dialog, which) -> {
            dialog.dismiss();
            removeScore();
            setupGame();
            scoreText.setText("Score: 0");
        });
        builder.setNegativeButton("Next Level", (dialog, which) -> {
            dialog.dismiss();
            int score = Integer.parseInt(scoreText.getText().toString().split(" ")[1]);
            saveScore(score);
            Intent intent = new Intent(this, Level2Activity.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.show();

    }

    private void saveScore(int score) {
        int previousScore = prefs.getInt(SCORE_KEY, 0);
        int newScore = previousScore + score;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SCORE_KEY, newScore);
        editor.apply();
    }

    private void removeScore() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(SCORE_KEY);
        editor.apply();
    }

}