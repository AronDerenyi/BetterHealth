package com.madebyaron.betterhealth.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.util.*;
import java.util.function.Consumer;

public class StepsDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "betterhealth_steps";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_STEPS = "steps";
    private static final String COLUMN_STEPS_ID = "id";
    private static final String COLUMN_STEPS_DATE = "date";
    private static final String COLUMN_STEPS_COUNT = "count";

    public StepsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_STEPS + " (\n" +
                COLUMN_STEPS_ID + " integer primary key autoincrement,\n" +
                COLUMN_STEPS_DATE + " integer not null,\n" +
                COLUMN_STEPS_COUNT + " integer not null\n" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_STEPS + ";");
        onCreate(db);
    }

    public void addSteps(Date date, int count, Runnable callback) {
        new AddStepsTask(getWritableDatabase(), date, count, callback).execute();
    }

    public void getStepCount(Date from, Date to, Consumer<Integer> callback) {
        new GetStepCountTask(getWritableDatabase(), from, to, callback).execute();
    }

    private static class AddStepsTask extends AsyncTask<Void, Void, Void> {

        private final SQLiteDatabase db;
        private final Date date;
        private final int count;
        private final Runnable callback;

        private AddStepsTask(SQLiteDatabase db, Date date, int count, Runnable callback) {
            this.db = db;
            this.date = date;
            this.count = count;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_STEPS_DATE, date.getTime());
            values.put(COLUMN_STEPS_COUNT, count);

            db.insert(TABLE_STEPS, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void ignored) {
            callback.run();
        }
    }

    private static class GetStepCountTask extends AsyncTask<Void, Void, Integer> {

        private final SQLiteDatabase db;
        private final Date from;
        private final Date to;
        private final Consumer<Integer> callback;

        private GetStepCountTask(SQLiteDatabase db, Date from, Date to, Consumer<Integer> callback) {
            this.db = db;
            this.from = from;
            this.to = to;
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Cursor cursor = db.query(TABLE_STEPS,
                    new String[]{"SUM(" + COLUMN_STEPS_COUNT + ")" },
                    from.getTime() + " <= " + COLUMN_STEPS_DATE + " AND " +
                            COLUMN_STEPS_DATE + " <= " + to.getTime(),
                    null, null, null, null
            );

            cursor.moveToNext();
            int sum = cursor.getInt(0);
            cursor.close();

            return sum;
        }

        @Override
        protected void onPostExecute(Integer result) {
            callback.accept(result);
        }
    }
}