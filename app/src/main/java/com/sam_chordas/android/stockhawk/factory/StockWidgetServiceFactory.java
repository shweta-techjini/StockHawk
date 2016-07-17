package com.sam_chordas.android.stockhawk.factory;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by Shweta on 7/6/16.
 */
public class StockWidgetServiceFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor itemData;

    public StockWidgetServiceFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void initData() {
        // To access the content provider in widget revert back the process identity
        final long identityToken = Binder.clearCallingIdentity();
        itemData = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        // Restore the identity
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return itemData == null ? 0 : itemData.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || itemData == null || !itemData.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_quote);
        remoteViews.setTextViewText(R.id.stock_symbol, itemData.getString(itemData.getColumnIndex(QuoteColumns.SYMBOL)));
        remoteViews.setTextViewText(R.id.bid_price, itemData.getString(itemData.getColumnIndex(QuoteColumns.BIDPRICE)));

        if (itemData.getInt(itemData.getColumnIndex(QuoteColumns.ISUP)) == 1) {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }
        if (Utils.showPercent) {
            remoteViews.setTextViewText(R.id.change, itemData.getString(itemData.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
        } else {
            remoteViews.setTextViewText(R.id.change, itemData.getString(itemData.getColumnIndex(QuoteColumns.CHANGE)));
        }

        Intent intent = new Intent();
        intent.putExtra(QuoteColumns.SYMBOL, itemData.getString(itemData.getColumnIndex(QuoteColumns.SYMBOL)));
        remoteViews.setOnClickFillInIntent(R.id.list_row, intent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
//        Log.d("Widget Service", "position called :: " + position);
        if (itemData.moveToPosition(position)) {
            return itemData.getLong(itemData.getColumnIndexOrThrow(QuoteColumns._ID));
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
