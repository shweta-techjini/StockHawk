package com.sam_chordas.android.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockWidgetService;

/**
 * Created by Shweta on 7/6/16.
 */
public class MyStocksWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // Could be multiple widgets
        for (int appWidgetId : appWidgetIds){
            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            // set the widget services
            Intent widgetService = new Intent(context, StockWidgetService.class);
            widgetService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.widget_listview, widgetService);
            views.setEmptyView(R.id.widget_listview, R.id.empty_view);

            // set the item click for each item in the list
            Intent stockDetailTemplate = new Intent(context, MyStockDetailActivity.class);
            PendingIntent stockDetailPendingintent = PendingIntent.getActivity(context, 1, stockDetailTemplate, 0);
            views.setPendingIntentTemplate(R.id.widget_listview, stockDetailPendingintent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
