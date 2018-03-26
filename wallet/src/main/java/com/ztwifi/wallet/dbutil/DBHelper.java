package com.ztwifi.wallet.dbutil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "wallet.db", null, 18);
	}
	
	String sql1 = "create table if not exists "
			+ "userdata"
			+ "(uid text, isLogin integer, token text, expireAt text, nick text, iconurl text, datalevel integer)";
	String deleteTableSql1 = "drop table if exists userdata";
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(deleteTableSql1);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(deleteTableSql1);
		onCreate(db);
	}

}
