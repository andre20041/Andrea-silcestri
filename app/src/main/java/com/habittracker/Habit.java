package com.habittracker;

import java.io.Serializable;

public class Habit implements Serializable {
    private String id;
    private String name;
    private String emoji;
    private boolean completedToday;
    private int streak;

    public Habit(String id, String name, String emoji) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.completedToday = false;
        this.streak = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public boolean isCompletedToday() { return completedToday; }
    public int getStreak() { return streak; }

    public void setCompletedToday(boolean completedToday) { this.completedToday = completedToday; }
    public void setStreak(int streak) { this.streak = streak; }
    public void setName(String name) { this.name = name; }
}
