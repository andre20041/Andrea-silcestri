package com.habittracker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayFragment extends Fragment {

    private HabitStorage storage;
    private HabitAdapter habitAdapter;
    private List<Habit> habits;
    private View emptyState;
    private RecyclerView rvHabits;

    private static final String[] EMOJIS = {"💪","📚","🏃","🧘","💧","🥗","😴","✍️","🎯","🎨","🏋️","🚴"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_today, container, false);

        storage = new HabitStorage(requireContext());
        emptyState = root.findViewById(R.id.empty_state);
        rvHabits = root.findViewById(R.id.rv_habits_today);

        setupCalendar(root);
        setupHabits();

        FloatingActionButton fab = root.findViewById(R.id.fab_add_today);
        fab.setOnClickListener(v -> showAddDialog());

        return root;
    }

    private void setupCalendar(View root) {
        RecyclerView rvCalendar = root.findViewById(R.id.rv_calendar);
        rvCalendar.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Calendar> days = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        int todayPos = 15;

        // generate 31 days (15 before, today, 15 after)
        Calendar start = (Calendar) today.clone();
        start.add(Calendar.DAY_OF_MONTH, -15);
        for (int i = 0; i < 31; i++) {
            Calendar day = (Calendar) start.clone();
            day.add(Calendar.DAY_OF_MONTH, i);
            days.add(day);
        }

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, todayPos, todayPos, (pos, day) -> {});
        rvCalendar.setAdapter(calendarAdapter);
        rvCalendar.scrollToPosition(todayPos - 3);
    }

    private void setupHabits() {
        habits = storage.loadHabits();
        habitAdapter = new HabitAdapter(habits, new HabitAdapter.OnHabitClickListener() {
            @Override
            public void onToggle(Habit habit) {
                storage.toggleHabit(habit.getId());
                refreshData();
                updateWidget();
            }
            @Override
            public void onDelete(Habit habit) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Elimina abitudine")
                        .setMessage("Vuoi eliminare \"" + habit.getName() + "\"?")
                        .setPositiveButton("Elimina", (d, w) -> {
                            storage.deleteHabit(habit.getId());
                            refreshData();
                            updateWidget();
                        })
                        .setNegativeButton("Annulla", null)
                        .show();
            }
        });
        rvHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHabits.setAdapter(habitAdapter);
        updateEmptyState();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        habits.clear();
        habits.addAll(storage.loadHabits());
        habitAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (habits.isEmpty()) {
            rvHabits.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvHabits.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null);
        android.widget.EditText etName = dialogView.findViewById(R.id.et_habit_name);
        final String[] selectedEmoji = {EMOJIS[0]};
        android.widget.TextView tvEmoji = dialogView.findViewById(R.id.tv_selected_emoji);
        tvEmoji.setText(selectedEmoji[0]);
        tvEmoji.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Scegli emoji")
                    .setItems(EMOJIS, (d, which) -> {
                        selectedEmoji[0] = EMOJIS[which];
                        tvEmoji.setText(selectedEmoji[0]);
                    }).show();
        });
        new AlertDialog.Builder(requireContext())
                .setTitle("Nuova abitudine")
                .setView(dialogView)
                .setPositiveButton("Aggiungi", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        storage.addHabit(name, selectedEmoji[0]);
                        refreshData();
                        updateWidget();
                    }
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    private void updateWidget() {
        if (getActivity() == null) return;
        Intent intent = new Intent(requireContext(), HabitWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(requireContext())
                .getAppWidgetIds(new ComponentName(requireContext(), HabitWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        requireActivity().sendBroadcast(intent);
    }
}
