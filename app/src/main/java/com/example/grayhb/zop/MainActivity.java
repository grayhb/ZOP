package com.example.grayhb.zop;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DBHelper db;
    Cursor students;
    Cursor zopData;

    Calendar dateAndTime = Calendar.getInstance();
    SimpleDateFormat df;

    Button btnPDay;
    Button btnNDay;
    TextView dateField;
    TableLayout mainTable;

    Boolean flLoad;

    Integer widthColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        widthColumn = 225;
        db = new DBHelper(this);

        df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        btnPDay = (Button)findViewById(R.id.btnPrevDay);
        btnNDay = (Button)findViewById(R.id.btnNextDay);
        dateField = (TextView)findViewById(R.id.DateField);

        mainTable = (TableLayout)findViewById(R.id.MainTable);

        setInitialDateTime();

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(view);
            }
        });

        btnPDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateAndTime.add(Calendar.DATE, -1);
                setInitialDateTime();
            }
        });

        btnNDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateAndTime.add(Calendar.DATE, 1);
                setInitialDateTime();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_students) {
            //открыть активити для учеников
            Intent intent = new Intent(MainActivity.this, ListStudents.class);
            intent.putExtra("ListStudents", "");
            startActivity(intent);
            setInitialDateTime();
        }
        else if (id == R.id.menu_month_statistics) {
            //открыть активити для статистика за месяц
            Intent intent = new Intent(MainActivity.this, MonthStatic.class);
            intent.putExtra("selectDate", dateField.getText().toString());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    void LoadData(){

        students = db.GetAllStudents(true);
        Integer cntZ = 0, cntO = 0, cntP = 0;

        if(students.getCount() == 0 ) return;

        flLoad = true;

        while (students.moveToNext()){

            Integer Z = 0, O = 0, P = 0;

            zopData = db.GetZOPData(dateField.getText().toString(), students.getString(0));

            if (zopData.getCount() > 0) {
                zopData.moveToNext();
                Z = Integer.parseInt(zopData.getString(2));
                O = Integer.parseInt(zopData.getString(3));
                P = Integer.parseInt(zopData.getString(4));
            }

            cntZ+=Z;cntO+=O;cntP+=P;
            AddRow(students.getString(0), students.getString(1), Z,O,P);
        }

        AddRowStat(cntZ, cntO, cntP);

        flLoad = false;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    // установка начальных даты и времени
    private void setInitialDateTime() {
        dateField.setText(df.format(dateAndTime.getTime()));
        ClearTable();
        LoadData();
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(MainActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    void AddRow(String StudentId, String StudentName, Integer Z, Integer O, Integer P)
    {
        TableRow tr = new TableRow(this);
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        tr.setLayoutParams(tableRowParams);
        tr.setPadding(0,10,0,10);

        TextView tv2 = new TextView(this);
        tv2.setText(StudentName);
        tv2.setLayoutParams(new TableRow.LayoutParams(1));
        tv2.setPadding(20,0,0,0);
        tr.addView(tv2);

        final CheckBox chZ = new CheckBox(this);

        chZ.setTag(StudentId);

        if (Z == 1) chZ.setChecked(true); else chZ.setChecked(false);

        chZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flLoad) return;
                db.SaveZOP(chZ.getTag().toString(), dateField.getText().toString(), "Z", chZ.isChecked());
                UpdateRowStat();
            }
        });

        TableRow.LayoutParams LP = new TableRow.LayoutParams(2);
        LP.gravity = Gravity.CENTER;

        chZ.setLayoutParams(LP);
        tr.addView(chZ);

        final CheckBox chO = new CheckBox(this);

        chO.setTag(StudentId);

        chO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flLoad) return;
                db.SaveZOP(chO.getTag().toString(), dateField.getText().toString(), "O", chO.isChecked());
                UpdateRowStat();
            }
        });

        if (O == 1) chO.setChecked(true); else chO.setChecked(false);

        LP = new TableRow.LayoutParams(3);
        LP.gravity = Gravity.CENTER;
        chO.setLayoutParams(LP);

        tr.addView(chO);

        final CheckBox chP = new CheckBox(this);

        chP.setTag(StudentId);

        chP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flLoad) return;
                db.SaveZOP(chP.getTag().toString(), dateField.getText().toString(), "P", chP.isChecked());
                UpdateRowStat();
            }
        });

        if (P == 1) chP.setChecked(true); else chP.setChecked(false);

        LP = new TableRow.LayoutParams(4);
        LP.gravity = Gravity.CENTER;
        chP.setLayoutParams(LP);

        tr.addView(chP);

        tr.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_shape));

        mainTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    void AddRowStat(Integer Z, Integer O, Integer P){

        TableRow tr = new TableRow(this);
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        mainTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tr.setPadding(0,1,0,1);
        Resources resource = this.getResources();
        tr.setBackgroundColor(resource.getColor(R.color.colorPrimary));
        tr = new TableRow(this);

        tr.setLayoutParams(tableRowParams);
        tr.setPadding(0,10,0,10);

        TextView tv1 = new TextView(this);
        tv1.setText("Итого");
        tv1.setLayoutParams(new TableRow.LayoutParams(1));
        tv1.setPadding(20,0,0,0);
        tr.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(Z.toString());
        tv2.setLayoutParams(new TableRow.LayoutParams(2));
        tv2.setWidth(widthColumn);
        tv2.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tr.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText(O.toString());
        tv3.setLayoutParams(new TableRow.LayoutParams(3));
        tv3.setWidth(widthColumn);
        tv3.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tr.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setText(P.toString());
        tv4.setLayoutParams(new TableRow.LayoutParams(4));
        tv4.setWidth(widthColumn);
        tv4.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tr.addView(tv4);

        mainTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

    }

    void UpdateRowStat()
    {
        if (mainTable.getChildCount() == 0) return;

        Integer lastIndex = mainTable.getChildCount() - 1;
        TableRow tr = (TableRow)mainTable.getChildAt(lastIndex);
        TextView tv2 = (TextView)tr.getChildAt(1);
        TextView tv3 = (TextView)tr.getChildAt(2);
        TextView tv4 = (TextView)tr.getChildAt(3);

        Integer cntZ = 0, cntO = 0, cntP = 0;
        for(int i = 0; i < lastIndex-1; i++)
        {
            tr = (TableRow)mainTable.getChildAt(i);
            CheckBox cbZ = (CheckBox)tr.getChildAt(1);
            CheckBox cbO = (CheckBox)tr.getChildAt(2);
            CheckBox cbP = (CheckBox)tr.getChildAt(3);

            if (cbZ.isChecked()) cntZ++;
            if (cbO.isChecked()) cntO++;
            if (cbP.isChecked()) cntP++;
        }
        tv2.setText(cntZ.toString());
        tv3.setText(cntO.toString());
        tv4.setText(cntP.toString());
    }

    void ClearTable()
    {
        mainTable.removeAllViews();
    }

}
