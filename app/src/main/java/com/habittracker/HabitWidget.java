package com.habittracker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class HabitWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        HabitStorage storage = new HabitStorage(context);
        int done = storage.getCompletedCount();
        int total = storage.getTotalCount();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_habit);

        if (total == 0) {
            views.setTextViewText(R.id.widget_title, "Habit Tracker");
            views.setTextViewText(R.id.widget_progress, "Nessuna abitudine");
            views.setTextViewText(R.id.widget_percent, "");
        } else {
            int percent = (done * 100) / total;
            views.setTextViewText(R.id.widget_title, "Habit Tracker");
            views.setTextViewText(R.id.widget_progress, done + " / " + total + " abitudini");
            views.setTextViewText(R.id.widget_percent, percent + "%");
            views.setProgressBar(R.id.widget_progressbar, 100, percent, false);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
