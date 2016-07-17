package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

/**
 * Created by Shweta on 27/14/16.
 */
public class MyStockDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;
    private static final String LOG_TAG = MyStockDetailActivity.class.getSimpleName();
    private Cursor mCursor;
    private LineChart mChart;
    private String string_symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_graph);
        initChart();

        Intent intent = getIntent();
        Bundle args = new Bundle();
        string_symbol = intent.getStringExtra(QuoteColumns.SYMBOL);
        setTitle(string_symbol);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);
    }

    private void initChart() {
        mChart = (LineChart) findViewById(R.id.stockGraph);
        if (mChart != null) {
            mChart.setDragDecelerationFrictionCoef(0.9f);
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);
            mChart.setDrawGridBackground(false);
            mChart.setHighlightPerDragEnabled(true);
            mChart.animateX(800);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.BIDPRICE, QuoteColumns.CREATED},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{string_symbol},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        setData();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setData() {
        if (mCursor == null){
            return;
        }

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        int maxGraphPoints;
        if (mCursor.getCount()>10){
            maxGraphPoints = 10;
        }else{
            maxGraphPoints = mCursor.getCount();
        }
        mCursor.moveToFirst();

        for (int i = 0; i < maxGraphPoints; i++) {
            String price = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE));
            String time = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CREATED));
            try {
                float floatPrice = Float.parseFloat(price);
                xVals.add(String.valueOf(i));
                yVals.add(new Entry(floatPrice, i));
                Log.d(LOG_TAG, "Time : " + time);
                Log.d(LOG_TAG, "price : " + price);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            mCursor.moveToNext();
        }

        LineDataSet set1;

        Log.d(LOG_TAG, "mchart does not has any data");
        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "Bid Price");
        set1.setDrawFilled(true);

        // create a data object with the data sets
        LineData data = new LineData(xVals, set1);
        // set data
        mChart.setData(data);
        mChart.setDescription("Stock value over time");
        mChart.setPinchZoom(true);
        mChart.notifyDataSetChanged();
    }
}