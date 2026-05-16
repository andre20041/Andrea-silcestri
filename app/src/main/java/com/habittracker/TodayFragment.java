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
        fab.setOnClickListener(v -> showAddTypeSheet());

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

    private void showAddTypeSheet() {
        AddTypeBottomSheet sheet = new AddTypeBottomSheet();
        sheet.setOnTypeSelectedListener(new AddTypeBottomSheet.OnTypeSelectedListener() {
            @Override
            public void onHabitSelected() {
                android.content.Intent i = new android.content.Intent(requireContext(), CategorySelectionActivity.class);
                i.putExtra(HabitCreationActivity.EXTRA_HABIT_TYPE, "Abitudine");
                startActivityForResult(i, AddTypeBottomSheet.REQUEST_ADD_HABIT);
            }
            @Override
            public void onRecurringSelected() {
                Intent i = new Intent(requireContext(), HabitCreationActivity.class);
                i.putExtra(HabitCreationActivity.EXTRA_CATEGORY_NAME, "Attività ricorrente");
                i.putExtra(HabitCreationActivity.EXTRA_CATEGORY_EMOJI, "🔄");
                startActivityForResult(i, AddTypeBottomSheet.REQUEST_ADD_HABIT);
            }
            @Override
            public void onActivitySelected() {
                Intent i = new Intent(requireContext(), HabitCreationActivity.class);
                i.putExtra(HabitCreationActivity.EXTRA_CATEGORY_NAME, "Attività");
                i.putExtra(HabitCreationActivity.EXTRA_CATEGORY_EMOJI, "✅");
                startActivityForResult(i, AddTypeBottomSheet.REQUEST_ADD_HABIT);
            }
        });
        sheet.show(getChildFragmentManager(), "add_type");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddTypeBottomSheet.REQUEST_ADD_HABIT && resultCode == android.app.Activity.RESULT_OK && data != null) {
            String name = data.getStringExtra(HabitCreationActivity.RESULT_HABIT_NAME);
            String emoji = data.getStringExtra(HabitCreationActivity.RESULT_HABIT_EMOJI);
            String category = data.getStringExtra(HabitCreationActivity.RESULT_CATEGORY);
            if (name != null && !name.isEmpty()) {
                String type = data.getStringExtra(HabitCreationActivity.RESULT_HABIT_TYPE);
                storage.addHabit(name, emoji != null ? emoji : "💪", category != null ? category : "", type);
                refreshData();
                updateWidget();
            }
        }
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
