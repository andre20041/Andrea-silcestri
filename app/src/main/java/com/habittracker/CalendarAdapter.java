package com.habittracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    public interface OnDayClickListener { void onDayClick(int position, Calendar day); }

    private final List<Calendar> days;
    private int selectedPosition;
    private final OnDayClickListener listener;
    private final int todayPosition;

    private static final String[] DAY_NAMES = {"Dom","Lun","Mar","Mer","Gio","Ven","Sab"};

    public CalendarAdapter(List<Calendar> days, int selectedPosition, int todayPosition, OnDayClickListener listener) {
        this.days = days;
        this.selectedPosition = selectedPosition;
        this.todayPosition = todayPosition;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Calendar day = days.get(position);
        h.tvDayName.setText(DAY_NAMES[day.get(Calendar.DAY_OF_WEEK) - 1]);
        h.tvDayNumber.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));

        boolean isSelected = position == selectedPosition;

        if (isSelected) {
            h.vPillBg.setBackgroundResource(R.drawable.bg_calendar_pill_selected);
            h.tvDayName.setTextColor(0xFFFFFFFF);
            h.tvDayNumber.setTextColor(0xFFFFFFFF);
            h.vTodayDot.setVisibility(View.VISIBLE);
        } else {
            h.vPillBg.setBackgroundResource(R.drawable.bg_calendar_pill_dark);
            h.tvDayName.setTextColor(0xFF666666);
            h.tvDayNumber.setTextColor(0xFF999999);
            h.vTodayDot.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = h.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            listener.onDayClick(selectedPosition, day);
        });
    }

    @Override
    public int getItemCount() { return days.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber;
        View vPillBg, vTodayDot;
        View vCircleBg, vTodayRing;

        ViewHolder(View v) {
            super(v);
            tvDayName = v.findViewById(R.id.tv_day_name);
            tvDayNumber = v.findViewById(R.id.tv_day_number);
            vPillBg = v.findViewById(R.id.v_pill_bg);
            vTodayDot = v.findViewById(R.id.v_today_dot);
            vCircleBg = v.findViewById(R.id.v_circle_bg);
            vTodayRing = v.findViewById(R.id.v_today_ring);
        }
    }
}
