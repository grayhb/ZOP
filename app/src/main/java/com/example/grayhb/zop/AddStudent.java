package com.example.grayhb.zop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.database.Cursor;

public class AddStudent extends AppCompatActivity {

    DBHelper db;
    Cursor cursor;
    String student_id;

    EditText StudentField;
    Button btnCancel;
    Button btnSave;
    CheckBox cbDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        student_id = getIntent().getStringExtra("StudentId");

        db = new DBHelper(this);

        StudentField = (EditText)findViewById(R.id.AddStudentFIO);
        btnCancel = (Button)findViewById(R.id.btnCancelAddStudent);
        btnSave = (Button)findViewById(R.id.btnSaveAddStudent);
        cbDelete = (CheckBox)findViewById(R.id.checkBoxStudentDelete);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cbDelete.isChecked())
                {
                    db.DeleteStudent(student_id);
                    setResult(Activity.RESULT_OK);
                }
                else {
                    if (db.SaveStudent(StudentField.getText().toString().trim(), student_id))
                        setResult(Activity.RESULT_OK);
                    else
                        setResult(Activity.RESULT_CANCELED);
                }

                finish();
            }
        });

        LoadData();
    }

    void LoadData(){

        if (student_id.length() == 0) return;

        cursor = db.GetStudent(student_id);
        if (cursor.getCount() > 0 )
        {
            cursor.moveToFirst();
            StudentField.setText(cursor.getString(1));
        }
    }
}
