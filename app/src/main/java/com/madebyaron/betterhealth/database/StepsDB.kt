package com.madebyaron.betterhealth.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import java.util.*

class StepsDB(context: Context) : SQLiteOpenHelper(context,
	DATABASE_NAME, null,
	DATABASE_VERSION
) {

	companion object {
		private const val DATABASE_NAME = "betterhealth_steps"
		private const val DATABASE_VERSION = 1

		private const val TABLE_STEPS = "steps"
		private const val COLUMN_STEPS_ID = "id"
		private const val COLUMN_STEPS_DATE = "date"
		private const val COLUMN_STEPS_COUNT = "count"
	}

	override fun onCreate(db: SQLiteDatabase) {
		db.execSQL("""
			create table if not exists $TABLE_STEPS (
				$COLUMN_STEPS_ID integer primary key autoincrement,
				$COLUMN_STEPS_DATE integer not null,
				$COLUMN_STEPS_COUNT integer not null
			);
		""".trimIndent())
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		db.execSQL("drop table if exists $TABLE_STEPS;")
		onCreate(db)
	}

	fun addSteps(date: Date, count: Int, callback: () -> Unit) {
		AddStepsTask(
			writableDatabase,
			date,
			count,
			callback
		).execute()
	}

	fun getStepCount(from: Date, to: Date, callback: (Int) -> Unit) {
		GetStepCountTask(
			writableDatabase,
			from,
			to,
			callback
		).execute()
	}

	private class AddStepsTask(
			private val db: SQLiteDatabase,
			private val date: Date,
			private val count: Int,
			private val callback: () -> Unit) :
			AsyncTask<Unit, Unit, Unit>() {

		override fun doInBackground(vararg params: Unit?) {
			val values = ContentValues()
			values.put(COLUMN_STEPS_DATE, date.time)
			values.put(COLUMN_STEPS_COUNT, count)

			db.insert(TABLE_STEPS, null, values).toInt()
		}

		override fun onPostExecute(result: Unit) {
			callback()
		}
	}

	private class GetStepCountTask(
			private val db: SQLiteDatabase,
			private val from: Date,
			private val to: Date,
			private val callback: (Int) -> Unit) :
			AsyncTask<Unit, Unit, Int>() {

		override fun doInBackground(vararg params: Unit?): Int {
			val cursor = db.query(
				TABLE_STEPS,
					arrayOf("SUM($COLUMN_STEPS_COUNT)"),
					"""
						${from.time} <= $COLUMN_STEPS_DATE AND
						$COLUMN_STEPS_DATE <= ${to.time}
					""".trimIndent(),
					null, null, null, null
			)

			cursor.moveToNext()
			val sum = cursor.getInt(0)
			cursor.close()

			return sum
		}

		override fun onPostExecute(result: Int) {
			callback(result)
		}
	}
}