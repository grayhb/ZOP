package com.example.grayhb.zop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Students (StudentId INTEGER PRIMARY KEY AUTOINCREMENT, StudentName TEXT)");
        db.execSQL("create table ZOP (Date TEXT, StudentId INTEGER, Z INTEGER, O INTEGER, P INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Students");
        db.execSQL("DROP TABLE IF EXISTS ZOP");
        onCreate(db);
    }

    /** Сохранить данные ученика
    * @param StudentName ФИО ученика
    * @param StudentId Id ученика
    */
    public boolean SaveStudent(String StudentName, String StudentId)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("StudentName", StudentName);

        long result = -1;

        //больше двух символов
        if (StudentName.length() > 2) {
            if (StudentId.length() == 0) //новый ученик
                result = db.insert("Students", null, contentValues);
            else // редактируем существующего
                result = db.update("Students", contentValues, "StudentId = " + StudentId, null);
        }

        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean SaveZOP(String StudentId, String date, String TypeData, Boolean flChecked) {

        ConvertDate();

        date = GetDateInSQLiteFormat(date);

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        long result = -1;

        if (this.ExistStudentZOP(StudentId, date)) {
            Integer v = 0;
            if (flChecked) v = 1;
            contentValues.put(TypeData, v);
            result = db.update("ZOP", contentValues, "Date = '" + date + "' AND StudentId = " + StudentId, null);
        } else {    // новая запись
            contentValues.put("Date", date);
            contentValues.put("StudentId", StudentId);

            Integer Z = 0, O = 0, P = 0;
            if (TypeData == "Z" && flChecked) Z = 1;
            if (TypeData == "O" && flChecked) O = 1;
            if (TypeData == "P" && flChecked) P = 1;

            contentValues.put("Z", Z);
            contentValues.put("O", O);
            contentValues.put("P", P);

            result = db.insert("ZOP", null, contentValues);
        }

        if (result == -1)
            return false;
        else
            return true;
    }

    private void ConvertDate() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select Date, StudentId from ZOP where Date like '%.%'", null);

        Log.d("DBHelper", String.format("{0}", c.getCount()));

        if (c.getCount() == 0) return;

        while (c.moveToNext()) {

            String date = c.getString(0);
            String studentId = c.getString(1);

            Log.d("DBHelper", date);
            Log.d("DBHelper", studentId);

            String dateNew = GetDateInSQLiteFormat(date);

            ContentValues contentValues = new ContentValues();
            contentValues.put("Date", dateNew);

            db.update("ZOP", contentValues,
            "date = '" + date + "' and StudentId = '" + studentId + "'", null);
        }
    }

    /** Получить всех учеников */
    public Cursor GetAllStudents(boolean flSort)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from Students";

        if (flSort)
            sql += " ORDER BY StudentName";

        return db.rawQuery(sql, null);
    }

    /**  Получить запись ученика по id
     * @param StudentId Id ученика
     */
    public Cursor GetStudent(String StudentId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from Students where StudentId = ?", new String[] {StudentId});
    }

    public Cursor GetZOPData(String date, String StudentId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from ZOP where Date = ? And StudentId = ?", new String[] {GetDateInSQLiteFormat(date), StudentId});
    }

    public Cursor GetZOPDataStatic(String StudentId, String d1, String d2)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("select StudentId, SUM(Z) as cntZ, SUM(O) as cntO, SUM(P) as cntP from ZOP where StudentId = ? AND Date BETWEEN ? AND ? ", new String[] {StudentId, GetDateInSQLiteFormat(d1), GetDateInSQLiteFormat(d2)});
    }

    boolean ExistStudentZOP(String StudentId, String date)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.rawQuery("select * from ZOP where Date = ? AND StudentId = ?", new String[] {GetDateInSQLiteFormat(date), StudentId});

        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public void DeleteStudent(String StudentId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        long flDelete = db.delete("Students", "StudentId = ?", new String[]{StudentId});
        flDelete = db.delete("ZOP", "StudentId = ?", new String[]{StudentId});
    }

    private String GetDateInSQLiteFormat(String D) {

        SimpleDateFormat dateFormatOld = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat dateFormatNew = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        //Log.d("DBHelper", D);
        //Log.d("DBHelper", dateFormatOld.format(date));
        if (D.contains("-")) return D;

        try {
            //Log.d("DBHelper", dateFormatNew.format(dateFormatOld.parse(D)));
            return dateFormatNew.format(dateFormatOld.parse(D));
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.d("DBHelper-Error", D);
            return "";
        }
    }

}
