package com.habittracker;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
                .inflate(R.layout.item_habit_dark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Habit habit = habits.get(position);

        h.tvEmoji.setText(habit.getEmoji());
        h.tvName.setText(habit.getName());

        // Category badge text
        String cat = habit.getCategory();
        h.tvCategoryBadge.setText((cat != null && !cat.isEmpty()) ? cat : "Abitudine");

        // Category color
        int catColor = getCategoryColor(cat);

        // Left icon background: colored rounded square
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.RECTANGLE);
        iconBg.setCornerRadius(16f);
        iconBg.setColor(habit.isCompletedToday() ? 0xFF4CAF50 : catColor);
        h.iconContainer.setBackground(iconBg);

        // Badge background + text color
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setShape(GradientDrawable.RECTANGLE);
        badgeBg.setCornerRadius(20f);
        badgeBg.setColor((catColor & 0x00FFFFFF) | 0x33000000);
        h.tvCategoryBadge.setBackground(badgeBg);
        h.tvCategoryBadge.setTextColor(catColor);

        // Completion state: dim entire row
        h.itemView.setAlpha(habit.isCompletedToday() ? 0.55f : 1.0f);

        // Tap = toggle
        h.itemView.setOnClickListener(v -> listener.onToggle(habit));

        // Long press = delete
        h.itemView.setOnLongClickListener(v -> {
            listener.onDelete(habit);
            return true;
        });
    }

    private int getCategoryColor(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) return 0xFF9E9E9E;
        for (Category cat : Category.getDefaultCategories()) {
            if (cat.getName().equals(categoryName)) return cat.getColor();
        }
        // fallback colors for non-standard categories
        if (categoryName.contains("ricorrente")) return 0xFF2196F3;
        if (categoryName.contains("Attività")) return 0xFF4CAF50;
        return 0xFF9E9E9E;
    }

    @Override
    public int getItemCount() { return habits.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvCategoryBadge;
        FrameLayout iconContainer;

        ViewHolder(View v) {
            super(v);
            tvEmoji = v.findViewById(R.id.tv_emoji);
            tvName = v.findViewById(R.id.tv_name);
            tvCategoryBadge = v.findViewById(R.id.tv_category_badge);
            iconContainer = v.findViewById(R.id.category_icon_container);
        }
    }
}
