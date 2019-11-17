package online.AttendanceManagementSystem.AAMS.teacher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TeacherDB extends SQLiteOpenHelper {


    public static final String DATABASE = "t_details";
    public static final String table_teacher_registration = "s_registration";

    public static final String table_batch_details = "batch_details";

    public static final String table_subject_details = "subject_details";
    public static final String table_non_blockchain_subject_details = "non_blockchain_subject_details";

    public static final String table_student_details = "student_details";
    public static final String table_all_students = "all_students";
    public static final String table_blockchain_sync = "blockchain_sync";

    public static final String table_attendance_details = "attendance_details";
    public static final String table_attendance_blockchain_sync = "attendance_blockchain_sync";

    public static final String table_subjectwise_students = "subjectwise_students";


    public TeacherDB(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + table_teacher_registration + "("
                + "s_name" + " TEXT,"
                + "s_email" + " TEXT,"
                + "s_mob" + " TEXT,"
                + "s_imei" + " TEXT"
                + ")");

        db.execSQL("Create table " + table_batch_details + "("
                + "id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name" + " TEXT"
                + ")");

        db.execSQL("Create table " + table_subject_details + "("
                + "id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "batch_id" + " INTEGER,"
                + "name" + " TEXT,"
                + "code" + " TEXT"
                + ")");

        db.execSQL("Create table " + table_non_blockchain_subject_details + "("
                + "id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "code" + " TEXT"
                + ")");

        db.execSQL("Create table " + table_attendance_details + "("
                + "batch_id" + " INTEGER,"
                + "subject_id" + " INTEGER,"
                + "subject_code" + " TEXT,"
                + "student_roll" + " TEXT,"
                + "student_pk" + " TEXT,"
                + "time_stamp" + " TEXT,"
                + "AorP" + " INTEGER"
                + ")");

        db.execSQL("Create table " + table_attendance_blockchain_sync + "("
                + "batch_id" + " INTEGER,"
                + "subject_id" + " INTEGER,"
                + "subject_code" + " TEXT,"
                + "student_roll" + " TEXT,"
                + "student_pk" + " TEXT,"
                + "time_stamp" + " TEXT,"
                + "AorP" + " INTEGER"
                + ")");

        db.execSQL("Create table " + table_student_details + "("
                + "s_name" + " TEXT,"
                + "student_roll" + " TEXT,"
                + "s_sap" + " TEXT,"
                + "s_email" + " TEXT,"
                + "s_course" + " TEXT,"
                + "s_batch_sec" + " TEXT,"
                + "s_mob" + " TEXT,"
                + "s_sem" + " TEXT,"
                + "s_imei" + " TEXT,"
                + "batch_id" + " INTEGER,"
                + "subject_id" + " INTEGER,"
                + "subject" + " TEXT"
                + ")");

        db.execSQL("Create table " + table_all_students + "("
                + "s_pk" + " TEXT,"
                + "s_name" + " TEXT,"
                + "s_roll" + " TEXT,"
                + "s_email" + " TEXT,"
                + "s_imei" + " TEXT"
                + ")");

        db.execSQL("Create table " + table_blockchain_sync + "("
                + "s_pk" + " TEXT,"
                + "s_name" + " TEXT,"
                + "s_roll" + " TEXT,"
                + "s_email" + " TEXT,"
                + "s_imei" + " TEXT"
                + ")");

        db.execSQL("Create table " + table_subjectwise_students + "("
                + "s_pk" + " TEXT,"
                + "s_name" + " TEXT,"
                + "s_roll" + " TEXT,"
                + "s_email" + " TEXT,"
                + "s_imei" + " TEXT,"
                + "s_subject" + " TEXT,"
                + "s_subjectid" + " INTEGER"
                + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_teacher_registration);
        db.execSQL("DROP TABLE IF EXISTS " + table_batch_details);
        db.execSQL("DROP TABLE IF EXISTS " + table_student_details);
        db.execSQL("DROP TABLE IF EXISTS " + table_subject_details);
        db.execSQL("DROP TABLE IF EXISTS " + table_attendance_details);
        db.execSQL("DROP TABLE IF EXISTS " + table_blockchain_sync);
        db.execSQL("DROP TABLE IF EXISTS " + table_all_students);

        onCreate(db);
    }

    public Cursor getAttendance(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_student_details,null);
        return res;
    }

    public Cursor getSubjectWiseAttendance(int batch_id, int subject_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_attendance_details +
                        " where batch_id = " + batch_id + " and subject_id = " + subject_id,
                null);
        return res;
    }

    public Cursor getSubjectWiseAttendanceForBlockchain(int batch_id, int subject_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_attendance_blockchain_sync +
                        " where batch_id = " + batch_id + " and subject_id = " + subject_id,
                null);
        return res;
    }

    public long insertStudent(String s_pk, String s_name, String s_roll, String s_email, String s_imei){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("s_pk", s_pk);
        contentValues.put("s_name", s_name);
        contentValues.put("s_roll", s_roll);
        contentValues.put("s_email", s_email);
        contentValues.put("s_imei", s_imei);
        long id = sqLiteDatabase.insert(table_all_students,null,contentValues);
        Log.e("Inserted into data base","   "+id+"  ");
        return id;
    }


    public Cursor getAllUniqueStudents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_all_students, null);
        return res;
    }

    public long insertSubjectStudentForBlockchain(String s_pk, String s_name, String s_roll, String s_email, String s_imei, String s_subject, int s_subjectid){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("s_pk", s_pk);
        contentValues.put("s_name", s_name);
        contentValues.put("s_roll", s_roll);
        contentValues.put("s_email", s_email);
        contentValues.put("s_imei", s_imei);
        contentValues.put("s_subject", s_subject);
        contentValues.put("s_subjectid", s_subjectid);
        long id = sqLiteDatabase.insert(table_subjectwise_students,null,contentValues);
        Log.e("Inserted into data base","   "+id+"  ");
        return id;
    }

    public Cursor getSubjectwiseUniqueStudentsForBlockchain(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_subjectwise_students,null);
        return res;
    }

    public void deleteStudentEntrySubjectwiseForBlockchain(String s_imei, String s_roll, int s_subjectid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table_subjectwise_students + " WHERE s_imei = '" + s_imei + "' AND s_roll ='" + s_roll + "' AND s_subjectid = " + s_subjectid);
        Log.e("b", "student entry deleted");
    }

    public long insertForBlockchain(String s_pk, String s_name, String s_roll, String s_email, String s_imei){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("s_pk", s_pk);
        contentValues.put("s_name", s_name);
        contentValues.put("s_roll", s_roll);
        contentValues.put("s_email", s_email);
        contentValues.put("s_imei", s_imei);
        long id = sqLiteDatabase.insert(table_blockchain_sync,null,contentValues);
        Log.e("Inserted into data base","   "+id+"  ");
        return id;
    }

    public Cursor getForBlockchain(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_blockchain_sync, null);
        return res;
    }
    public void deleteStudentEntryForBlockchain(String s_imei, String s_roll){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " +table_blockchain_sync + " WHERE s_imei = '" + s_imei +"' AND s_roll ='" + s_roll+"'");
        Log.e("b", "student entry deleted");
    }

    public long insertAttendance(int batch_id, int subject_id, String subject_code,
                                 String roll, String student_pk, String timeStamp, int AorP){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("batch_id", batch_id);
        contentValues.put("subject_id", subject_id);
        contentValues.put("subject_code", subject_code);
        contentValues.put("student_roll", roll);
        contentValues.put("student_pk", student_pk);
        contentValues.put("time_stamp", timeStamp);
        contentValues.put("AorP", AorP);

        long id = sqLiteDatabase.insert(table_attendance_details,null,contentValues);
        Log.e("Inserted into data base","   "+id+"  ");
        return id;
    }

    public long insertAttendanceForBlockchain(int batch_id, int subject_id, String subject_code,
                                 String roll, String student_pk, String timeStamp, int AorP){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("batch_id", batch_id);
        contentValues.put("subject_id", subject_id);
        contentValues.put("subject_code", subject_code);
        contentValues.put("student_roll", roll);
        contentValues.put("student_pk", student_pk);
        contentValues.put("time_stamp", timeStamp);
        contentValues.put("AorP", AorP);

        long id = sqLiteDatabase.insert(table_attendance_blockchain_sync,null,contentValues);
        Log.e("Inserted into data base","   "+id+"  ");
        return id;
    }

    public void deleteAttendanceSubjectwiseForBlockchain(int batch_id, int subject_id, String student_roll, String student_pk) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table_attendance_blockchain_sync +
                " WHERE batch_id = " + batch_id + " AND subject_id = " + subject_id +
                " AND student_roll = '" + student_roll + "' AND student_pk = '" + student_pk +"'");
        Log.e("b", "student entry deleted");
    }


    public Cursor getStudentByBatch(int batch_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_student_details + " where batch_id = "+ batch_id,null);
        return res;
    }

    public Cursor getStudentByBatchAndSubject(int batch_id, int subject_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_student_details + " where batch_id = "+
                batch_id + " and subject_id = " + subject_id,null);
        return res;
    }

    public Cursor getAllSubjects(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_subject_details,null);
        return res;
    }

    public Cursor getSubjectByBatch(int batch_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ table_subject_details + " where batch_id = "+ batch_id,null);
        return res;
    }

    public long insertStudentDetails(String name,String roll, String sap, String email,
                                     String course, String batchSec, String mob, String sem,
                                     String imei, int batch_id,int subject_id, String subject){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("s_name", name);
        contentValues.put("student_roll", roll);
        contentValues.put("s_sap", sap);
        contentValues.put("s_email", email);
        contentValues.put("s_course", course);
        contentValues.put("s_batch_sec", batchSec);
        contentValues.put("s_mob", mob);
        contentValues.put("s_sem", sem);
        contentValues.put("s_imei", imei);
        contentValues.put("batch_id", batch_id);
        contentValues.put("subject_id", subject_id);
        contentValues.put("subject", subject);

        long id = sqLiteDatabase.insert(table_student_details,null,contentValues);
        Log.e("Inserted into data base","   "+id+"  ");
        return id;
    }


    public long insertSubject(int batch_id,String name, String code){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("code", code);
        contentValues.put("batch_id", batch_id);

        long id = sqLiteDatabase.insert(table_subject_details,null,contentValues);
        return id;
    }

    public long insertSubjectForBlockchain(String code){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("code", code);

        long id = sqLiteDatabase.insert(table_non_blockchain_subject_details,null,contentValues);
        return id;
    }

    public Cursor getSubjectsForBlockchain() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table_non_blockchain_subject_details, null);
        return res;
    }
    public void deleteSubjectsPushedToBlockchain(String code){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table_non_blockchain_subject_details + " WHERE code = '" + code +"'");
        Log.e("b", " subject entry deleted");
    }

    public long insertTRegistrationDetails(String name, String email, String mob, String imei) {


        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("s_name", name);
        contentValues.put("s_email", email);
        contentValues.put("s_mob", mob);
        contentValues.put("s_imei", imei);

        Cursor res = sqLiteDatabase.rawQuery("select * from " + table_teacher_registration, null);

        if (res.getCount() > 0) {
            sqLiteDatabase.delete(table_teacher_registration, null, null);
        }

        long id = sqLiteDatabase.insert(table_teacher_registration, null, contentValues);
        sqLiteDatabase.close();
        return id;
    }

    public Cursor getTRegDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table_teacher_registration, null);
        return res;
    }


    public void clearTable() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(table_teacher_registration, null, null);
        sqLiteDatabase.delete(table_batch_details, null, null);
        sqLiteDatabase.close();
    }

    public long insertBatch(String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        long id = db.insert(table_batch_details, null, contentValues);
        db.close();
        return id;
    }



    public Cursor getAllBatch() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor ss = db.rawQuery("select * from " + table_batch_details, null);
        return ss;
    }

    public Cursor getBatchByID(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor ss = db.rawQuery("select * from " + table_batch_details + " where id=" + id, null);
        return ss;
    }

}
