package com.ztwifi.wallet.dbutil;

import com.ztwifi.wallet.bean.LoginInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbUserInfo {

	private DBHelper dbHelp;

	public DbUserInfo(Context context) {
		dbHelp = new DBHelper(context);
	}

	
	/**
	 * 
	 * @param uId
	 * @return 查询当前是否有这个uId
	 */
	public synchronized boolean booleanInseartInFo(String uId) {
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		try {
			int c = 0;
			Cursor cursor = db
					.rawQuery(
							"select count(*) as a from userdata where uid = ? ",
							new String[] { uId});
			try {
				if (null != cursor) {
					if (cursor.moveToFirst()) {
						c = cursor.getInt(cursor.getColumnIndex("a"));
					}
				}
			} finally {
				cursor.close();
			}
			if (c > 0)
				return false;
			else
				return true;
		} finally {
			db.close();
		}
	}
	
	/**
	 * 
	 * @param uId
	 * @return 如果isLogin为1，返回ExpireAt的值，否则返回空串。
	 */
	public synchronized String booleanLogined(String uId) {
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		try {
			int c = 0;
			String d = "";
			Cursor cursor = db
					.rawQuery(
							"select nvl(isLogin,0) as a,expireAt as b from userdata where uid = ? ",
							new String[] { uId});
			try {
				if (null != cursor) {
					if (cursor.moveToFirst()) {
						c = cursor.getInt(cursor.getColumnIndex("a"));
						d = cursor.getString(cursor.getColumnIndex("b"));
					}
				}
			} finally {
				cursor.close();
			}
			if (c == 1)
				return d;
			else
				return "";
		} finally {
			db.close();
		}
	}
	

	/**
	 * 更新数据
	 * @param token 
	 */
	public void updateInFo(String uid, String isLogin, String token, String expireAt, String nick, String iconurl, int datalevel ) {
		SQLiteDatabase db = dbHelp.getWritableDatabase();
		try {

			db.execSQL(
					"update userdata set isLogin=?, token=?, expireAt=?, nick=?, iconurl=?, datalevel=? where uid=?",
					new Object[] { isLogin, token, expireAt, nick, iconurl, datalevel, uid });

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * 新增数据
	 */
	public void insertInFo(String uid, String isLogin, String token, String expireAt, String nick, String iconurl,int datalevel) {
		SQLiteDatabase db = dbHelp.getWritableDatabase();

		try {

			db.execSQL(
					"insert into userdata (uid, isLogin, token, expireAt, nick, iconurl,datalevel) values(?,?,?,?,?,?,?)",
					new Object[] { uid, isLogin, token, expireAt, nick, iconurl,datalevel });

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}
	
	public synchronized LoginInfo getInFo(String uid) {

		LoginInfo object = new LoginInfo();
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		try {
			Cursor cursor = db.rawQuery(
					"select * from userdata where uid = ?",
					new String[] { uid});
			try {
				if (null != cursor) {
					if (cursor.moveToFirst()) {
						object.setIsLogin(cursor.getInt(cursor.getColumnIndex("isLogin")));
						object.setUid(cursor.getString(cursor.getColumnIndex("uid")));
						object.setExpireAt(cursor.getString(cursor.getColumnIndex("expireAt")));
						object.setToken(cursor.getString(cursor.getColumnIndex("token")));
						object.setNick(cursor.getString(cursor.getColumnIndex("nick")));
						object.setIconurl(cursor.getString(cursor.getColumnIndex("iconurl")));
						object.setDatelevel((cursor.getInt(cursor.getColumnIndex("datalevel"))));

					}
				}
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			object = null;
		} finally {
			db.close();
		}
		return object;

	}

}
