package com.mariomassad.knightucf;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ScheduleDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "Schedule.db";
	public static final String TABLE_NAME = "destinations";
	
	public ScheduleDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_DESTINATION_TABLE="CREATE TABLE " + TABLE_NAME + " (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"description TEXT, " + 
				"location TEXT, " +
				"start_time INTEGER, " + 
				"end_time INTEGER, " +
				"days TEXT, " +
				"coordinates TEXT)";
		db.execSQL(CREATE_DESTINATION_TABLE);
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}
}
