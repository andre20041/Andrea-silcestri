package com.habittracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddTypeBottomSheet extends BottomSheetDialogFragment {

    public static final int REQUEST_ADD_HABIT = 1001;

    public interface OnTypeSelectedListener {
        void onHabitSelected();
        void onRecurringSelected();
        void onActivitySelected();
    }

    private OnTypeSelectedListener listener;

    public void setOnTypeSelectedListener(OnTypeSelectedListener l) {
        this.listener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_type, container, false);

        view.findViewById(R.id.item_abitudine).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onHabitSelected();
        });

        view.findViewById(R.id.item_ricorrente).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onRecurringSelected();
        });

        view.findViewById(R.id.item_attivita).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onActivitySelected();
        });

        return view;
    }
}
