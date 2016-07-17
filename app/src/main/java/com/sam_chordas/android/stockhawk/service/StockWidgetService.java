package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.factory.StockWidgetServiceFactory;

/**
 * Created by Shweta on 7/6/16.
 */
public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockWidgetServiceFactory(getApplicationContext());
    }

}
