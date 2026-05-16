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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.tvEmoji.setText(habit.getEmoji());
        holder.tvName.setText(habit.getName());
        holder.tvStreak.setText("🔥 " + habit.getStreak());

        if (habit.isCompletedToday()) {
            holder.btnCheck.setText("✓");
            holder.itemView.setAlpha(0.7f);
        } else {
            holder.btnCheck.setText("○");
            holder.itemView.setAlpha(1.0f);
        }

        holder.btnCheck.setOnClickListener(v -> listener.onToggle(habit));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(habit));
    }

    @Override
    public int getItemCount() { return habits.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvStreak;
        TextView btnCheck;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_emoji);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStreak = itemView.findViewById(R.id.tv_streak);
            btnCheck = itemView.findViewById(R.id.btn_check);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
