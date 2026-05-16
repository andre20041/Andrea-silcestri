package com.habittracker;

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
import java.util.List;

public class HabitsFragment extends Fragment {

    private HabitStorage storage;
    private HabitAdapter adapter;
    private List<Habit> habits;

    private static final String[] EMOJIS = {"💪","📚","🏃","🧘","💧","🥗","😴","✍️","🎯","🎨","🏋️","🚴"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_habits, container, false);

        storage = new HabitStorage(requireContext());
        RecyclerView rv = root.findViewById(R.id.rv_all_habits);
        habits = storage.loadHabits();

        adapter = new HabitAdapter(habits, new HabitAdapter.OnHabitClickListener() {
            @Override
            public void onToggle(Habit habit) {
                storage.toggleHabit(habit.getId());
                refresh();
            }
            @Override
            public void onDelete(Habit habit) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Elimina abitudine")
                        .setMessage("Elimina \"" + habit.getName() + "\"?")
                        .setPositiveButton("Elimina", (d, w) -> { storage.deleteHabit(habit.getId()); refresh(); })
                        .setNegativeButton("Annulla", null).show();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab_add_habit);
        fab.setOnClickListener(v -> showAddTypeSheet());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        habits.clear();
        habits.addAll(storage.loadHabits());
        adapter.notifyDataSetChanged();
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
                android.content.Intent i = new android.content.Intent(requireContext(), HabitCreationActivity.class);
                i.putExtra(HabitCreationActivity.EXTRA_CATEGORY_NAME, "Attività ricorrente");
                i.putExtra(HabitCreationActivity.EXTRA_CATEGORY_EMOJI, "🔄");
                startActivityForResult(i, AddTypeBottomSheet.REQUEST_ADD_HABIT);
            }
            @Override
            public void onActivitySelected() {
                android.content.Intent i = new android.content.Intent(requireContext(), HabitCreationActivity.class);
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
                refresh();
            }
        }
    }
}
