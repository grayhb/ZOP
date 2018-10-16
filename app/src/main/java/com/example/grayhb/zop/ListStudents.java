package com.example.grayhb.zop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.database.Cursor;
import android.widget.ListView;

import java.util.ArrayList;

public class ListStudents extends AppCompatActivity {

    DBHelper db;
    Cursor cursor;
    ListView lv;

    private static final int ADD_STUDENT = 1001;
    private static final int EDIT_STUDENT = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        db = new DBHelper(this);

        Button btn_AddStudent = (Button)findViewById(R.id.btnAddStudent);
        btn_AddStudent.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                //открыть активити для добавления нового ученика
                Intent intent = new Intent(ListStudents.this, AddStudent.class);
                intent.putExtra("StudentId", "");
                startActivityForResult(intent, ADD_STUDENT);
            }
        });

        lv = (ListView) findViewById(R.id.ListViewStudents);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListStudents.this, AddStudent.class);
                cursor.moveToPosition(i);
                intent.putExtra("StudentId", cursor.getString(0));
                startActivityForResult(intent, EDIT_STUDENT);
            }
        });

        Button btnListStudentBack = (Button)findViewById(R.id.btnListStudentBack);
        btnListStudentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GetAllStudents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            GetAllStudents();
        }
    }

    void GetAllStudents()
    {
        lv.setAdapter(null);
        cursor = db.GetAllStudents(false);

        if (cursor.getCount() > 0) {
            ArrayList<String> arrayList = new ArrayList<String>();
            while (cursor.moveToNext())
            {
                arrayList.add(cursor.getString(1));
            }

            // создаем адаптер
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, arrayList);

            // присваиваем адаптер списку
            lv.setAdapter(adapter);
        }
    }

}
