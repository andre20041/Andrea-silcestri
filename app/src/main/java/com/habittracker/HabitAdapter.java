package com.habittracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    public interface OnHabitClickListener {
        void onToggle(Habit habit);
        void onDelete(Habit habit);
    }

    private final List<Habit> habits;
    private final OnHabitClickListener listener;

    public HabitAdapter(List<Habit> habits, OnHabitClickListener listener) {
        this.habits = habits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit_dark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Habit habit = habits.get(position);
        h.tvEmoji.setText(habit.getEmoji());
        h.tvName.setText(habit.getName());
        h.tvStreak.setText("🔥 " + habit.getStreak() + " giorni");

        if (habit.isCompletedToday()) {
            h.btnCheck.setText("✓");
            h.btnCheck.setTextColor(0xFF4CAF50);
            h.itemView.setAlpha(0.65f);
        } else {
            h.btnCheck.setText("○");
            h.btnCheck.setTextColor(0xFFEF5350);
            h.itemView.setAlpha(1.0f);
        }

        h.btnCheck.setOnClickListener(v -> listener.onToggle(habit));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(habit));
    }

    @Override
    public int getItemCount() { return habits.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvStreak, btnCheck;
        ImageButton btnDelete;
        ViewHolder(View v) {
            super(v);
            tvEmoji = v.findViewById(R.id.tv_emoji);
            tvName = v.findViewById(R.id.tv_name);
            tvStreak = v.findViewById(R.id.tv_streak);
            btnCheck = v.findViewById(R.id.btn_check);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
}
