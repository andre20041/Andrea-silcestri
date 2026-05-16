package com.habittracker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategorySelectionActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_NAME = "category_name";
    public static final String EXTRA_CATEGORY_EMOJI = "category_emoji";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        RecyclerView rv = findViewById(R.id.rv_categories);
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        List<Category> categories = Category.getDefaultCategories();
        CategoryAdapter adapter = new CategoryAdapter(categories, cat -> {
            Intent intent = new Intent(this, HabitCreationActivity.class);
            intent.putExtra(HabitCreationActivity.EXTRA_CATEGORY_NAME, cat.getName());
            intent.putExtra(HabitCreationActivity.EXTRA_CATEGORY_EMOJI, cat.getEmoji());
            startActivityForResult(intent, 2002);
        });
        rv.setAdapter(adapter);

        findViewById(R.id.btn_cancel_category).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2002) {
            setResult(resultCode, data);
            finish();
        }
    }
}
