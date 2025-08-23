package com.example.sqliteproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccessModifier extends SQLiteOpenHelper {

    public static final String USERNAME = "USERNAME";

    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";

    public static final String LOGININFO = "LOGININFO";
    public static final String ID = "ID";

    public DatabaseAccessModifier(@Nullable Context context)
    {
        super(context, "userinfo.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + LOGININFO + " " +
                "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USERNAME + " TEXT, " +
                EMAIL + " TEXT, " +
                PASSWORD + " TEXT " +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(LoginInfo loginInfo){
        SQLiteDatabase db = this.getWritableDatabase();

//        StringBuilder sb = new StringBuilder();
//        sb.append("b");
//        sb.append("r");
//        sb.append("y");
//        sb.append("a");
//        sb.append("n");
//
//        System.out.println(sb);

        ContentValues cv = new ContentValues();
        cv.put(USERNAME, loginInfo.getUsername());
        cv.put(EMAIL, loginInfo.getEmail());
        cv.put(PASSWORD, loginInfo.getPassword());

        long insert = db.insert(LOGININFO, null, cv);

        if(insert == -1){
            return false;
        }
        else{
            return true;
        }

    }


    public boolean deleteOne(LoginInfo loginInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + LOGININFO + " WHERE " + ID + " = " + loginInfo.getId();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            return true;
        }
        else{
            return false;
        }
    }


    public List<LoginInfo> getResultList(){
        List<LoginInfo> resultset = new ArrayList<>();

        String queryAll = "SELECT * FROM " + LOGININFO;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryAll, null);

        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String username = cursor.getString(1);
                String email = cursor.getString(2);
                String password = cursor.getString(3);

                LoginInfo loginInfo = new LoginInfo(
                        id, username, email, password
                );
                resultset.add(loginInfo);
            }
            while (cursor.moveToNext());
        }
        else
        { }

        cursor.close();
        db.close();
        return resultset;

        }

    public LoginInfo getLatestUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + LOGININFO + " ORDER BY " + ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        LoginInfo loginInfo = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String username = cursor.getString(1);
            String email = cursor.getString(2);
            String password = cursor.getString(3);

            loginInfo = new LoginInfo(id, username, email, password);
        }
        cursor.close();
        db.close();
        return loginInfo;
    }

    public boolean checkUser(String username, String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + LOGININFO +
                " WHERE " + USERNAME + "=? AND " + EMAIL + "=? AND " + PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

}

