package com.example.sqliteproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseAccessModifier extends SQLiteOpenHelper {

    public static final String USERNAME = "USERNAME";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String IMAGE = "IMAGE"; // ✅ Profile image column
    public static final String LOGININFO = "LOGININFO";
    public static final String ID = "ID";

    // ✅ DB Version = 2 (includes image column)
    public DatabaseAccessModifier(@Nullable Context context) {
        super(context, "userinfo.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + LOGININFO + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME + " TEXT, " +
                EMAIL + " TEXT, " +
                PASSWORD + " TEXT, " +
                IMAGE + " BLOB" + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + LOGININFO + " ADD COLUMN " + IMAGE + " BLOB");
        }
    }

    /** ✅ Add new user (with or without profile image) **/
    public boolean addOne(LoginInfo loginInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USERNAME, loginInfo.getUsername());
        cv.put(EMAIL, loginInfo.getEmail());
        cv.put(PASSWORD, loginInfo.getPassword());
        cv.put(IMAGE, loginInfo.getImage()); // Store image (nullable)

        long insert = db.insert(LOGININFO, null, cv);
        db.close();
        return insert != -1;
    }

    /** ✅ Update profile image for a specific user **/
    public boolean updateUserImage(int userId, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IMAGE, imageBytes);
        int result = db.update(LOGININFO, cv, ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    /** ✅ Get image for a specific user **/
    public byte[] getUserImage(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LOGININFO, new String[]{IMAGE}, ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        byte[] image = null;
        if (cursor != null && cursor.moveToFirst()) {
            image = cursor.getBlob(0);
            cursor.close();
        }
        db.close();
        return image;
    }

    /** ✅ Get the last signed-up user (used if no login system yet) **/
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
            byte[] image = cursor.getBlob(4);

            loginInfo = new LoginInfo(id, username, email, password, image);
        }
        cursor.close();
        db.close();
        return loginInfo;
    }

    /** ✅ Check if user exists during login **/
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

    /** ✅ New: Get user details by email and password (for login) **/
    public LoginInfo getUserByEmailAndPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LOGININFO, null,
                EMAIL + "=? AND " + PASSWORD + "=?",
                new String[]{email, password}, null, null, null);

        LoginInfo loginInfo = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE));
            loginInfo = new LoginInfo(id, username, email, password, image);
            cursor.close();
        }
        db.close();
        return loginInfo;
    }

    public LoginInfo getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LOGININFO, null, ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        LoginInfo loginInfo = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE));

            loginInfo = new LoginInfo(id, username, email, password, image);
            cursor.close();
        }
        db.close();
        return loginInfo;
    }

    public int getUserId(String username, String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                LOGININFO,
                new String[]{ID},
                USERNAME + "=? AND " + EMAIL + "=? AND " + PASSWORD + "=?",
                new String[]{username, email, password},
                null, null, null
        );

        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            cursor.close();
        }
        db.close();
        return userId;
    }


}
