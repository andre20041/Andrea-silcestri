package com.habittracker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HabitStorage storage;
    private HabitAdapter adapter;
    private List<Habit> habits;
    private TextView tvProgress;
    private RecyclerView recyclerView;

    private static final String[] EMOJIS = {"💪", "📚", "🏃", "🧘", "💧", "🥗", "😴", "✍️", "🎯", "🎨"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = new HabitStorage(this);
        tvProgress = findViewById(R.id.tv_progress);
        recyclerView = findViewById(R.id.recycler_habits);

        habits = storage.loadHabits();
        adapter = new HabitAdapter(habits, new HabitAdapter.OnHabitClickListener() {
            @Override
            public void onToggle(Habit habit) {
                storage.toggleHabit(habit.getId());
                refreshData();
                updateWidget();
            }

            @Override
            public void onDelete(Habit habit) {
                new AlertDialog.Builder(MainActivity.this)
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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showAddDialog());

        updateProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        habits.clear();
        habits.addAll(storage.loadHabits());
        adapter.notifyDataSetChanged();
        updateProgress();
    }

    private void updateProgress() {
        int done = storage.getCompletedCount();
        int total = storage.getTotalCount();
        if (total == 0) {
            tvProgress.setText("Nessuna abitudine ancora. Aggiungine una!");
        } else {
            tvProgress.setText("Oggi: " + done + "/" + total + " completate");
        }
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_habit, null);
        EditText etName = dialogView.findViewById(R.id.et_habit_name);

        final String[] selectedEmoji = {EMOJIS[0]};
        TextView tvEmoji = dialogView.findViewById(R.id.tv_selected_emoji);
        tvEmoji.setText(selectedEmoji[0]);
        tvEmoji.setOnClickListener(v -> showEmojiPicker(tvEmoji, selectedEmoji));

        new AlertDialog.Builder(this)
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

    private void showEmojiPicker(TextView tvEmoji, String[] selectedEmoji) {
        new AlertDialog.Builder(this)
                .setTitle("Scegli emoji")
                .setItems(EMOJIS, (d, which) -> {
                    selectedEmoji[0] = EMOJIS[which];
                    tvEmoji.setText(selectedEmoji[0]);
                })
                .show();
    }

    private void updateWidget() {
        Intent intent = new Intent(this, HabitWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(this)
                .getAppWidgetIds(new ComponentName(this, HabitWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}
