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
                startActivityForResult(
                    new android.content.Intent(requireContext(), CategorySelectionActivity.class),
                    AddTypeBottomSheet.REQUEST_ADD_HABIT);
            }
            @Override
            public void onRecurringSelected() {
                showNameDialog("", "Attività ricorrente");
            }
            @Override
            public void onActivitySelected() {
                showNameDialog("", "Attività");
            }
        });
        sheet.show(getChildFragmentManager(), "add_type");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddTypeBottomSheet.REQUEST_ADD_HABIT && resultCode == android.app.Activity.RESULT_OK && data != null) {
            String catName = data.getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY_NAME);
            String catEmoji = data.getStringExtra(CategorySelectionActivity.EXTRA_CATEGORY_EMOJI);
            showNameDialog(catEmoji != null ? catEmoji : "💪", catName != null ? catName : "");
        }
    }

    private void showNameDialog(String defaultEmoji, String categoryName) {
        android.view.View dialogView = android.view.LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_habit, null);
        android.widget.EditText etName = dialogView.findViewById(R.id.et_habit_name);
        final String[] selectedEmoji = {defaultEmoji.isEmpty() ? EMOJIS[0] : defaultEmoji};
        android.widget.TextView tvEmoji = dialogView.findViewById(R.id.tv_selected_emoji);
        tvEmoji.setText(selectedEmoji[0]);
        tvEmoji.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Scegli emoji")
                    .setItems(EMOJIS, (d, which) -> { selectedEmoji[0] = EMOJIS[which]; tvEmoji.setText(selectedEmoji[0]); }).show();
        });
        new AlertDialog.Builder(requireContext())
                .setTitle(!categoryName.isEmpty() ? categoryName : "Nuova abitudine")
                .setView(dialogView)
                .setPositiveButton("Aggiungi", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        storage.addHabit(name, selectedEmoji[0], categoryName);
                        refresh();
                    }
                })
                .setNegativeButton("Annulla", null)
                .show();
    }
}
