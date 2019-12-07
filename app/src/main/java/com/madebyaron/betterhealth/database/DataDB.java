package com.madebyaron.betterhealth.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import androidx.core.util.Consumer;

import com.madebyaron.betterhealth.model.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class DataDB extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "betterhealth_data";
    private static int DATABASE_VERSION = 1;

    private static String TABLE_DATA = "data";
    private static String COLUMN_DATA_ID = "id";
    private static String COLUMN_DATA_TYPE = "type";
    private static String COLUMN_DATA_DATE = "date";
    private static String COLUMN_DATA_AMOUNT = "amount";

    public DataDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_DATA + "(\n" +
                COLUMN_DATA_ID + " integer primary key autoincrement,\n" +
                COLUMN_DATA_TYPE + " integer not null,\n" +
                COLUMN_DATA_DATE + " integer not null,\n" +
                COLUMN_DATA_AMOUNT + " real not null\n" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_DATA + ";");
        onCreate(db);
    }

    public void addData(int type, Date date, float amount, Consumer<Data> callback) {
        new AddDataTask(getWritableDatabase(), type, date, amount, callback).execute();
    }

    public void deleteData(int id, Consumer<Integer> callback) {
        new DeleteDataTask(getWritableDatabase(), id, callback).execute();
    }

    public void getDataList(Integer type, Consumer<List<Data>> callback) {
        new GetDataListTask(getWritableDatabase(), type, callback).execute();
    }

    public void getDataList(Consumer<List<Data>> callback) {
        getDataList(null, callback);
    }

    public void getLastData(Integer type, Consumer<Data> callback) {
        new GetLastDataTask(getWritableDatabase(), type, callback).execute();
    }

    public void getLastData(Consumer<Data> callback) {
        getLastData(null, callback);
    }

    public void getDataSum(int type, Date from, Date to, Consumer<Float> callback) {
        new GetDataSumTask(getWritableDatabase(), type, from, to, callback).execute();
    }

    private static class AddDataTask extends AsyncTask<Void, Void, Data> {

        private final SQLiteDatabase db;
        private final int type;
        private final Date date;
        private final Float amount;
        private final Consumer<Data> callback;

        private AddDataTask(SQLiteDatabase db, int type, Date date, Float amount, Consumer<Data> callback) {
            this.db = db;
            this.type = type;
            this.date = date;
            this.amount = amount;
            this.callback = callback;
        }

        @Override
        protected Data doInBackground(Void... voids) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATA_TYPE, type);
            values.put(COLUMN_DATA_DATE, date.getTime());
            values.put(COLUMN_DATA_AMOUNT, amount);

            int id = (int) db.insert(TABLE_DATA, null, values);

            return new Data(id, type, date, amount);
        }

        @Override
        protected void onPostExecute(Data data) {
            callback.accept(data);
        }
    }

    private static class DeleteDataTask extends AsyncTask<Void, Void, Integer> {

        private final SQLiteDatabase db;
        private final int id;
        private final Consumer<Integer> callback;

        private DeleteDataTask(SQLiteDatabase db, int id, Consumer<Integer> callback) {
            this.db = db;
            this.id = id;
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return db.delete(TABLE_DATA, COLUMN_DATA_ID + " = " + id, null);
        }

        @Override
        protected void onPostExecute(Integer result) {
            callback.accept(result);
        }
    }

    private static class GetDataListTask extends AsyncTask<Void, Void, List<Data>> {

        private final SQLiteDatabase db;
        private final Integer type;
        private final Consumer<List<Data>> callback;

        private GetDataListTask(SQLiteDatabase db, Integer type, Consumer<List<Data>> callback) {
            this.db = db;
            this.type = type;
            this.callback = callback;
        }

        @Override
        protected List<Data> doInBackground(Void... voids) {
            Cursor cursor = db.query(TABLE_DATA,
                    new String[]{COLUMN_DATA_ID, COLUMN_DATA_TYPE, COLUMN_DATA_DATE, COLUMN_DATA_AMOUNT},
                    type != null ? COLUMN_DATA_TYPE + " = " + type : null,
                    null, null, null,
                    COLUMN_DATA_DATE + " DESC"
            );

            List<Data> dataList = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                dataList.add(new Data(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_TYPE)),
                        new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATA_DATE))),
                        cursor.getFloat(cursor.getColumnIndex(COLUMN_DATA_AMOUNT))
                ));
            }

            cursor.close();

            return dataList;
        }

        @Override
        protected void onPostExecute(List<Data> result) {
            callback.accept(result);
        }
    }

    private static class GetLastDataTask extends AsyncTask<Void, Void, Data> {

        private final SQLiteDatabase db;
        private final Integer type;
        private final Consumer<Data> callback;

        private GetLastDataTask(SQLiteDatabase db, Integer type, Consumer<Data> callback) {
            this.db = db;
            this.type = type;
            this.callback = callback;
        }

        @Override
        protected Data doInBackground(Void... voids) {
            Cursor cursor = db.query(TABLE_DATA,
                    new String[]{"MAX(" + COLUMN_DATA_DATE + ")", COLUMN_DATA_ID, COLUMN_DATA_TYPE, COLUMN_DATA_DATE, COLUMN_DATA_AMOUNT},
                    type != null ? COLUMN_DATA_TYPE + " = " + type : null,
                    null, null, null, null
            );

            cursor.moveToNext();
            Data data = new Data(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_ID)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_TYPE)),
                    new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATA_DATE))),
                    cursor.getFloat(cursor.getColumnIndex(COLUMN_DATA_AMOUNT))
            );
            cursor.close();

            return data;
        }

        @Override
        protected void onPostExecute(Data result) {
            callback.accept(result);
        }
    }

    private static class GetDataSumTask extends AsyncTask<Void, Void, Float> {

        private final SQLiteDatabase db;
        private final int type;
        private final Date from;
        private final Date to;
        private final Consumer<Float> callback;

        private GetDataSumTask(SQLiteDatabase db, int type, Date from, Date to, Consumer<Float> callback) {
            this.db = db;
            this.type = type;
            this.from = from;
            this.to = to;
            this.callback = callback;
        }

        @Override
        protected Float doInBackground(Void... voids) {
            Cursor cursor = db.query(TABLE_DATA,
                    new String[]{"SUM(" + COLUMN_DATA_AMOUNT + ")" },
                    COLUMN_DATA_TYPE + " = " + type + " AND " +
                            from.getTime() + " <= " + COLUMN_DATA_DATE + " AND " +
                            COLUMN_DATA_DATE + " <= " + to.getTime(),
                    null, null, null, null
            );

            cursor.moveToNext();
            float sum = cursor.getFloat(0);
            cursor.close();

            return sum;
        }

        @Override
        protected void onPostExecute(Float result) {
            callback.accept(result);
        }
    }
}