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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Category cat = categories.get(position);
        h.tvEmoji.setText(cat.getEmoji());
        h.tvName.setText(cat.getName());

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(12f);
        int color = cat.getColor();
        int alphaColor = (color & 0x00FFFFFF) | 0x33000000;
        bg.setColor(alphaColor);
        h.iconBg.setBackground(bg);

        h.tvEmoji.setTextColor(color);
        h.itemView.setOnClickListener(v -> listener.onCategoryClick(cat));
    }

    @Override
    public int getItemCount() { return categories.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName;
        FrameLayout iconBg;
        ViewHolder(View v) {
            super(v);
            tvEmoji = v.findViewById(R.id.tv_category_emoji);
            tvName = v.findViewById(R.id.tv_category_name);
            iconBg = v.findViewById(R.id.category_icon_bg);
        }
    }
}
