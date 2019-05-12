package com.madebyaron.betterhealth.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import java.util.*
import kotlin.collections.ArrayList

class DataApi(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

	companion object {
		private const val DATABASE_NAME = "betterhealth_data"
		private const val DATABASE_VERSION = 1

		private const val TABLE_DATA = "data"
		private const val COLUMN_DATA_ID = "id"
		private const val COLUMN_DATA_TYPE = "type"
		private const val COLUMN_DATA_DATE = "date"
		private const val COLUMN_DATA_AMOUNT = "amount"
	}

	override fun onCreate(db: SQLiteDatabase) {
		db.execSQL("""
			create table if not exists $TABLE_DATA (
				$COLUMN_DATA_ID integer primary key autoincrement,
				$COLUMN_DATA_TYPE integer not null,
				$COLUMN_DATA_DATE integer not null,
				$COLUMN_DATA_AMOUNT real not null
			);
		""".trimIndent())
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		db.execSQL("drop table if exists $TABLE_DATA;")
		onCreate(db)
	}

	fun addData(type: Int, date: Date, amount: Float, callback: (Data) -> Unit) {
		AddDataTask(writableDatabase, type, date, amount, callback).execute()
	}

	fun deleteData(id: Int, callback: (Int) -> Unit) {
		DeleteDataTask(writableDatabase, id, callback).execute()
	}

	fun getDataList(type: Int? = null, callback: (List<Data>) -> Unit) {
		GetDataListTask(writableDatabase, type, callback).execute()
	}

	fun getLastData(type: Int? = null, callback: (Data) -> Unit) {
		GetLastDataTask(writableDatabase, type, callback).execute()
	}

	fun getDataSum(type: Int, from: Date, to: Date, callback: (Float) -> Unit) {
		GetDataSumTask(writableDatabase, type, from, to, callback).execute()
	}

	private class AddDataTask(
			private val db: SQLiteDatabase,
			private val type: Int,
			private val date: Date,
			private val amount: Float,
			private val callback: (Data) -> Unit) :
			AsyncTask<Unit, Unit, Data>() {

		override fun doInBackground(vararg params: Unit?): Data {
			val values = ContentValues()
			values.put(COLUMN_DATA_TYPE, type)
			values.put(COLUMN_DATA_DATE, date.time)
			values.put(COLUMN_DATA_AMOUNT, amount)

			val id = db.insert(TABLE_DATA, null, values).toInt()

			return Data(id, type, date, amount)
		}

		override fun onPostExecute(result: Data) {
			callback(result)
		}
	}

	private class DeleteDataTask(
			private val db: SQLiteDatabase,
			private val id: Int,
			private val callback: (Int) -> Unit) :
			AsyncTask<Unit, Unit, Int>() {

		override fun doInBackground(vararg params: Unit?): Int {
			return db.delete(TABLE_DATA, "$COLUMN_DATA_ID = $id", null)
		}

		override fun onPostExecute(result: Int) {
			callback(result)
		}
	}

	private class GetDataListTask(
			private val db: SQLiteDatabase,
			private val type: Int?,
			private val callback: (List<Data>) -> Unit) :
			AsyncTask<Unit, Unit, List<Data>>() {

		override fun doInBackground(vararg params: Unit?): List<Data> {
			val cursor = db.query(TABLE_DATA,
					arrayOf(COLUMN_DATA_ID, COLUMN_DATA_TYPE, COLUMN_DATA_DATE, COLUMN_DATA_AMOUNT),
					if (type != null) "$COLUMN_DATA_TYPE = $type" else null,
					null, null, null,
					"$COLUMN_DATA_DATE DESC"
			)

			val dataList: MutableList<Data> = ArrayList(cursor.count)
			while (cursor.moveToNext()) {
				dataList.add(Data(
						cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_ID)),
						cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_TYPE)),
						Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATA_DATE))),
						cursor.getFloat(cursor.getColumnIndex(COLUMN_DATA_AMOUNT))
				))
			}

			cursor.close()

			return dataList
		}

		override fun onPostExecute(result: List<Data>) {
			callback(result)
		}
	}

	private class GetLastDataTask(
			private val db: SQLiteDatabase,
			private val type: Int?,
			private val callback: (Data) -> Unit) :
			AsyncTask<Unit, Unit, Data>() {

		override fun doInBackground(vararg params: Unit?): Data {
			val cursor = db.query(TABLE_DATA,
					arrayOf("MAX($COLUMN_DATA_DATE)", COLUMN_DATA_ID, COLUMN_DATA_TYPE, COLUMN_DATA_DATE, COLUMN_DATA_AMOUNT),
					if (type != null) "$COLUMN_DATA_TYPE = $type" else null,
					null, null, null, null
			)

			cursor.moveToNext()
			val data = Data(
					cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_ID)),
					cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_TYPE)),
					Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATA_DATE))),
					cursor.getFloat(cursor.getColumnIndex(COLUMN_DATA_AMOUNT))
			)
			cursor.close()

			return data
		}

		override fun onPostExecute(result: Data) {
			callback(result)
		}
	}

	private class GetDataSumTask(
			private val db: SQLiteDatabase,
			private val type: Int,
			private val from: Date,
			private val to: Date,
			private val callback: (Float) -> Unit) :
			AsyncTask<Unit, Unit, Float>() {

		override fun doInBackground(vararg params: Unit?): Float {
			val cursor = db.query(TABLE_DATA,
					arrayOf("SUM($COLUMN_DATA_AMOUNT)"),
					"""
						$COLUMN_DATA_TYPE = $type AND
						${from.time} <= $COLUMN_DATA_DATE AND
						$COLUMN_DATA_DATE <= ${to.time}
					""".trimIndent(),
					null, null, null, null
			)

			cursor.moveToNext()
			val sum = cursor.getFloat(0)
			cursor.close()

			return sum
		}

		override fun onPostExecute(result: Float) {
			callback(result)
		}
	}
}