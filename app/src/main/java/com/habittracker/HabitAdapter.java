package com.habittracker;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
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

        // Badge shows habit TYPE (Abitudine / Attività ricorrente / Attività)
        String type = habit.getHabitType();
        h.tvCategoryBadge.setText((type != null && !type.isEmpty()) ? type : "Abitudine");

        // Category color (from category name stored in habit)
        int catColor = getCategoryColor(habit.getCategory());
        int displayColor = habit.isCompletedToday() ? 0xFF4CAF50 : catColor;

        // Convert 14dp to pixels for a proper iOS-style squircle radius
        float density = h.itemView.getContext().getResources().getDisplayMetrics().density;
        float cornerPx = 14f * density;

        // Gradient icon background: top-left lighter, bottom-right slightly darker
        int lightColor = blendColor(displayColor, Color.WHITE, 0.18f);
        int darkColor = blendColor(displayColor, Color.BLACK, 0.15f);
        GradientDrawable iconBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{lightColor, displayColor, darkColor});
        iconBg.setShape(GradientDrawable.RECTANGLE);
        iconBg.setCornerRadius(cornerPx);
        h.iconContainer.setBackground(iconBg);

        // Badge background + text color
        float badgeCornerPx = 20f * density;
        int badgeColor = blendColor(displayColor, 0xFF000000, 0.0f);
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setShape(GradientDrawable.RECTANGLE);
        badgeBg.setCornerRadius(badgeCornerPx);
        badgeBg.setColor((displayColor & 0x00FFFFFF) | 0x26000000);
        badgeBg.setStroke((int)(1f * density), (displayColor & 0x00FFFFFF) | 0x55000000);
        h.tvCategoryBadge.setBackground(badgeBg);
        h.tvCategoryBadge.setTextColor(displayColor);

        // Completion state: dim entire row
        h.itemView.setAlpha(habit.isCompletedToday() ? 0.5f : 1.0f);

        // Tap = toggle
        h.itemView.setOnClickListener(v -> listener.onToggle(habit));

        // Long press = delete
        h.itemView.setOnLongClickListener(v -> {
            listener.onDelete(habit);
            return true;
        });
    }

    private int blendColor(int color, int overlay, float amount) {
        int r = (int) (Color.red(color) * (1 - amount) + Color.red(overlay) * amount);
        int g = (int) (Color.green(color) * (1 - amount) + Color.green(overlay) * amount);
        int b = (int) (Color.blue(color) * (1 - amount) + Color.blue(overlay) * amount);
        return Color.rgb(Math.min(255, r), Math.min(255, g), Math.min(255, b));
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
