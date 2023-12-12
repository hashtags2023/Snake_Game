package com.gamecodeschool.c17snake;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardManager {

    private static final int NUM_TOP_SCORES = 5;
    private SharedPreferences leaderboardPrefs;

    public LeaderboardManager(Context context) {
        leaderboardPrefs = context.getSharedPreferences("LeaderboardPrefs", Context.MODE_PRIVATE);
    }

    public void updateLeaderboard(int newScore) {
        // Get the existing leaderboard
        List<Integer> leaderboard = getLeaderboard();

        // Add the new score to the leaderboard
        leaderboard.add(newScore);

        // Sort the leaderboard in descending order
        Collections.sort(leaderboard, Collections.reverseOrder());

        // Keep only the top scores
        leaderboard = leaderboard.subList(0, Math.min(leaderboard.size(), NUM_TOP_SCORES));

        // Save the updated leaderboard to SharedPreferences
        saveLeaderboard(leaderboard);
    }

    public List<Integer> getLeaderboard() {
        // Retrieve the leaderboard from SharedPreferences
        List<Integer> leaderboard = new ArrayList<>();
        for (int i = 0; i < NUM_TOP_SCORES; i++) {
            // Use a default value of 0 if the score is not found
            int score = leaderboardPrefs.getInt("score_" + i, 0);
            leaderboard.add(score);
        }
        return leaderboard;
    }

    private void saveLeaderboard(List<Integer> leaderboard) {
        // Save the leaderboard to SharedPreferences
        SharedPreferences.Editor editor = leaderboardPrefs.edit();
        for (int i = 0; i < leaderboard.size(); i++) {
            editor.putInt("score_" + i, leaderboard.get(i));
        }
        editor.apply();
    }
}