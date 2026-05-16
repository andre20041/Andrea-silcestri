package com.habittracker;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
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

        h.tvName.setText(habit.getName());

        // Badge shows habit TYPE
        String type = habit.getHabitType();
        h.tvCategoryBadge.setText((type != null && !type.isEmpty()) ? type : "Abitudine");

        // Flat category color (solid, no gradient)
        int catColor = getCategoryColor(habit.getCategory());
        int displayColor = habit.isCompletedToday() ? 0xFF4CAF50 : catColor;

        h.iconCard.setCardBackgroundColor(displayColor);
        h.ivIcon.setImageResource(getCategoryIcon(habit.getCategory()));

        // Circular toggle
        h.ivToggle.setImageResource(habit.isCompletedToday()
                ? R.drawable.ic_toggle_done
                : R.drawable.ic_toggle_empty);

        // Badge pill: semi-transparent tint of the category color
        float density = h.itemView.getContext().getResources().getDisplayMetrics().density;
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setShape(GradientDrawable.RECTANGLE);
        badgeBg.setCornerRadius(20f * density);
        badgeBg.setColor((displayColor & 0x00FFFFFF) | 0x22000000);
        h.tvCategoryBadge.setBackground(badgeBg);
        h.tvCategoryBadge.setTextColor(displayColor);

        // Completion state
        h.itemView.setAlpha(habit.isCompletedToday() ? 0.5f : 1.0f);

        h.itemView.setOnClickListener(v -> listener.onToggle(habit));
        h.itemView.setOnLongClickListener(v -> {
            listener.onDelete(habit);
            return true;
        });
    }

    private int getCategoryColor(String name) {
        if (name == null || name.isEmpty()) return 0xFF9E9E9E;
        for (Category cat : Category.getDefaultCategories()) {
            if (cat.getName().equals(name)) return cat.getColor();
        }
        if (name.contains("ricorrente")) return 0xFF2196F3;
        if (name.contains("Attività")) return 0xFF4CAF50;
        return 0xFF9E9E9E;
    }

    private int getCategoryIcon(String name) {
        if (name == null) return R.drawable.ic_cat_star;
        switch (name) {
            case "Casa":             return R.drawable.ic_cat_home;
            case "Salute":           return R.drawable.ic_cat_health;
            case "Sport":            return R.drawable.ic_cat_sport;
            case "Apprendimento":    return R.drawable.ic_cat_book;
            case "Arte":             return R.drawable.ic_cat_palette;
            case "Meditazione":      return R.drawable.ic_cat_meditation;
            case "Finanza":          return R.drawable.ic_cat_finance;
            case "Nutrizione":       return R.drawable.ic_cat_food;
            case "Lavoro":           return R.drawable.ic_cat_work;
            case "Sociale":          return R.drawable.ic_cat_social;
            case "All'aperto":       return R.drawable.ic_cat_nature;
            case "Divertimento":     return R.drawable.ic_cat_fun;
            case "Abbandona":        return R.drawable.ic_cat_quit;
            default:                 return R.drawable.ic_cat_star;
        }
    }

    @Override
    public int getItemCount() { return habits.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategoryBadge;
        MaterialCardView iconCard;
        ImageView ivIcon, ivToggle;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_name);
            tvCategoryBadge = v.findViewById(R.id.tv_category_badge);
            iconCard = v.findViewById(R.id.category_icon_container);
            ivIcon = v.findViewById(R.id.iv_cat_icon);
            ivToggle = v.findViewById(R.id.iv_toggle);
        }
    }
}
