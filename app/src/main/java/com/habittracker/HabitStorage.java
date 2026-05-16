package com.habittracker;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HabitStorage {
    private static final String PREFS_NAME = "HabitTrackerPrefs";
    private static final String KEY_HABITS = "habits";
    private static final String KEY_LAST_RESET = "last_reset";

    private final SharedPreferences prefs;

    public HabitStorage(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        checkDailyReset();
    }

    private void checkDailyReset() {
        String today = getTodayString();
        String lastReset = prefs.getString(KEY_LAST_RESET, "");
        if (!today.equals(lastReset)) {
            resetDailyCompletion();
            prefs.edit().putString(KEY_LAST_RESET, today).apply();
        }
    }

    private String getTodayString() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return cal.get(java.util.Calendar.YEAR) + "-" +
               cal.get(java.util.Calendar.MONTH) + "-" +
               cal.get(java.util.Calendar.DAY_OF_MONTH);
    }

    private void resetDailyCompletion() {
        List<Habit> habits = loadHabits();
        for (Habit h : habits) {
            h.setCompletedToday(false);
        }
        saveHabits(habits);
    }

    public List<Habit> loadHabits() {
        List<Habit> habits = new ArrayList<>();
        String json = prefs.getString(KEY_HABITS, "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Habit h = new Habit(
                        obj.getString("id"),
                        obj.getString("name"),
                        obj.getString("emoji")
                );
                h.setCompletedToday(obj.getBoolean("completedToday"));
                h.setStreak(obj.getInt("streak"));
                h.setCategory(obj.optString("category", ""));
                habits.add(h);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return habits;
    }

    public void saveHabits(List<Habit> habits) {
        JSONArray arr = new JSONArray();
        for (Habit h : habits) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", h.getId());
                obj.put("name", h.getName());
                obj.put("emoji", h.getEmoji());
                obj.put("completedToday", h.isCompletedToday());
                obj.put("streak", h.getStreak());
                obj.put("category", h.getCategory());
                arr.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putString(KEY_HABITS, arr.toString()).apply();
    }

    public void addHabit(String name, String emoji) {
        addHabit(name, emoji, "");
    }

    public void addHabit(String name, String emoji, String category) {
        List<Habit> habits = loadHabits();
        Habit h = new Habit(UUID.randomUUID().toString(), name, emoji);
        h.setCategory(category);
        habits.add(h);
        saveHabits(habits);
    }

    public void deleteHabit(String id) {
        List<Habit> habits = loadHabits();
        habits.removeIf(h -> h.getId().equals(id));
        saveHabits(habits);
    }

    public void toggleHabit(String id) {
        List<Habit> habits = loadHabits();
        for (Habit h : habits) {
            if (h.getId().equals(id)) {
                boolean nowDone = !h.isCompletedToday();
                h.setCompletedToday(nowDone);
                if (nowDone) h.setStreak(h.getStreak() + 1);
                else if (h.getStreak() > 0) h.setStreak(h.getStreak() - 1);
                break;
            }
        }
        saveHabits(habits);
    }

    public int getCompletedCount() {
        int count = 0;
        for (Habit h : loadHabits()) {
            if (h.isCompletedToday()) count++;
        }
        return count;
    }

    public int getTotalCount() {
        return loadHabits().size();
    }
}
