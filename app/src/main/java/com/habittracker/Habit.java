package com.habittracker;

import java.io.Serializable;

public class Habit implements Serializable {
    private String id;
    private String name;
    private String emoji;
    private boolean completedToday;
    private int streak;
    private String category;
    private String habitType;

    public Habit(String id, String name, String emoji) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.completedToday = false;
        this.streak = 0;
        this.category = "";
        this.habitType = "Abitudine";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public boolean isCompletedToday() { return completedToday; }
    public int getStreak() { return streak; }
    public String getCategory() { return category; }
    public String getHabitType() { return habitType; }

    public void setCompletedToday(boolean completedToday) { this.completedToday = completedToday; }
    public void setStreak(int streak) { this.streak = streak; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setHabitType(String habitType) { this.habitType = habitType != null ? habitType : "Abitudine"; }
}
