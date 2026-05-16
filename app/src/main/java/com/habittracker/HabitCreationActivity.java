package com.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;

public class HabitCreationActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_NAME = "cat_name";
    public static final String EXTRA_CATEGORY_EMOJI = "cat_emoji";
    public static final String RESULT_HABIT_NAME = "habit_name";
    public static final String RESULT_HABIT_EMOJI = "habit_emoji";
    public static final String RESULT_CATEGORY = "habit_category";
    public static final String RESULT_TRACKING = "habit_tracking";

    private static final int STEP_TRACKING = 0;
    private static final int STEP_DEFINE = 1;

    private int currentStep = STEP_TRACKING;
    private String selectedTracking = "yes_no";
    private String categoryName, categoryEmoji;

    private ViewFlipper viewFlipper;
    private TextView btnPrev, btnNext;
    private View[] dots;
    private EditText etName, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_creation);

        categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        categoryEmoji = getIntent().getStringExtra(EXTRA_CATEGORY_EMOJI);
        if (categoryEmoji == null) categoryEmoji = "💪";
        if (categoryName == null) categoryName = "";

        viewFlipper = findViewById(R.id.view_flipper);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        etName = findViewById(R.id.et_habit_name);
        etDescription = findViewById(R.id.et_description);

        dots = new View[]{
            findViewById(R.id.dot_1),
            findViewById(R.id.dot_2),
            findViewById(R.id.dot_3),
            findViewById(R.id.dot_4)
        };

        // Tracking type buttons
        View btnYesNo = findViewById(R.id.btn_yes_no);
        View btnNumeric = findViewById(R.id.btn_numeric);
        View btnTimer = findViewById(R.id.btn_timer);

        btnYesNo.setOnClickListener(v -> { selectedTracking = "yes_no"; highlightTracking(btnYesNo, btnNumeric, btnTimer); });
        btnNumeric.setOnClickListener(v -> { selectedTracking = "numeric"; highlightTracking(btnNumeric, btnYesNo, btnTimer); });
        btnTimer.setOnClickListener(v -> { selectedTracking = "timer"; highlightTracking(btnTimer, btnYesNo, btnNumeric); });

        btnPrev.setOnClickListener(v -> navigatePrev());
        btnNext.setOnClickListener(v -> navigateNext());

        updateUI();
    }

    private void highlightTracking(View selected, View... others) {
        selected.setBackgroundResource(R.drawable.btn_red_filled);
        for (View v : others) v.setBackgroundResource(R.drawable.btn_red_filled);
    }

    private void navigateNext() {
        if (currentStep == STEP_TRACKING) {
            currentStep = STEP_DEFINE;
            updateUI();
        } else if (currentStep == STEP_DEFINE) {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Inserisci il nome dell'abitudine");
                etName.requestFocus();
                return;
            }
            Intent result = new Intent();
            result.putExtra(RESULT_HABIT_NAME, name);
            result.putExtra(RESULT_HABIT_EMOJI, categoryEmoji);
            result.putExtra(RESULT_CATEGORY, categoryName);
            result.putExtra(RESULT_TRACKING, selectedTracking);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    private void navigatePrev() {
        if (currentStep == STEP_DEFINE) {
            currentStep = STEP_TRACKING;
            updateUI();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void updateUI() {
        viewFlipper.setDisplayedChild(currentStep);
        btnPrev.setVisibility(currentStep > 0 ? View.VISIBLE : View.INVISIBLE);
        btnNext.setText(currentStep == STEP_DEFINE ? "SALVA" : "PROSSIMO");

        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(i <= currentStep ? R.drawable.dot_filled : R.drawable.dot_empty);
        }
    }

    @Override
    public void onBackPressed() {
        navigatePrev();
    }
}
