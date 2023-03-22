package com.mobile.ball_tapping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mobile.ball_tapping.component.Player;
import com.mobile.ball_tapping.component.PlayerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        ArrayList<Player> players = getPlayersFromPrefs(prefs);

        // Replace Comparator.comparingInt with a custom comparator
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return Integer.compare(p2.getScore(), p1.getScore());
            }
        });

        if (players.size() > 25) {
            players = new ArrayList<>(players.subList(0, 25));
        }

        PlayerAdapter playerAdapter = new PlayerAdapter(players);
        recyclerView.setAdapter(playerAdapter);
    }


    private ArrayList<Player> getPlayersFromPrefs(SharedPreferences prefs) {
        Map<String, ?> allEntries = prefs.getAll();
        ArrayList<Player> players = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("@Player:")) {
                String name = key.substring("@Player:".length());
                int score = (Integer) entry.getValue();
                players.add(new Player(name, score));
            }
        }

        return players;
    }
}