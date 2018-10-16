package com.example.grayhb.zop;

import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MonthStatic extends AppCompatActivity {

    DBHelper db;
    Cursor students;
    Cursor zopData;

    TableLayout mainTable;
    String D;
    Date d1, d2;

    Integer widthColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_static);
        widthColumn = 175;
        mainTable = (TableLayout)findViewById(R.id.MonthStaticTable);

        D = getIntent().getStringExtra("selectDate");

        db = new DBHelper(this);

        TextView monthLabel = (TextView)findViewById(R.id.MonthLabel);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM.yyyy", Locale.getDefault());
        SimpleDateFormat month_date = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());

        Calendar c = Calendar.getInstance();

        try {
            monthLabel.setText("Статистика " + month_date.format(dateFormat.parse(D)));

            d1 = dateFormat.parse("01." + dateFormat2.format(dateFormat.parse(D)));

            c.setTime(d1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);

            d2 = dateFormat.parse(dateFormat.format(c.getTime()));

            //Cursor cursor = db.GetZOPDataStatic(d1, d2);
            //тут дописать
            LoadData();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button btnMonthStaticBack = (Button)findViewById(R.id.btnMonthStaticBack);
        btnMonthStaticBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    void LoadData(){

        students = db.GetAllStudents(true);
        Integer cntZ = 0, cntO = 0, cntP = 0;

        if(students.getCount() == 0 ) return;

        while (students.moveToNext()){

            Integer Z = 0, O = 0, P = 0;

            zopData = db.GetZOPDataStatic(students.getString(0), d1, d2 );

            if (zopData.getCount() > 0) {
                zopData.moveToNext();

                if (zopData.getString(1) != null)
                {Z = Integer.parseInt(zopData.getString(1));}

                if (zopData.getString(2) != null){
                    O = Integer.parseInt(zopData.getString(2));}

                if (zopData.getString(3) != null){
                    P = Integer.parseInt(zopData.getString(3));}
            }

            cntZ+=Z;cntO+=O;cntP+=P;
            AddRow(students.getString(0), students.getString(1), Z,O,P);
        }


        AddRowStat(cntZ, cntO, cntP);
    }

    void AddRow(String StudentId, String StudentName, Integer Z, Integer O, Integer P)
    {
        TableRow tr = new TableRow(this);
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        tr.setLayoutParams(tableRowParams);
        tr.setPadding(0,20,0,20);

        TextView tv1 = new TextView(this);
        tv1.setPadding(20,0,0,0);
        tv1.setText(StudentName);
        tv1.setLayoutParams(new TableRow.LayoutParams(1));

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

        tr.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_shape));

        mainTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    void AddRowStat(Integer Z, Integer O, Integer P){

        TableRow tr = new TableRow(this);
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        tr.setPadding(0,1,0,1);
        Resources resource = this.getResources();
        tr.setBackgroundColor(resource.getColor(R.color.colorPrimary));

        mainTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        tr = new TableRow(this);

        tr.setLayoutParams(tableRowParams);
        tr.setPadding(0,10,0,10);

        TextView tv1 = new TextView(this);
        tv1.setText("Итого");
        tv1.setPadding(20,0,0,0);
        tv1.setLayoutParams(new TableRow.LayoutParams(1));
        tr.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(Z.toString());
        tv2.setLayoutParams(new TableRow.LayoutParams(2));
        tv2.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tv2.setWidth(widthColumn);
        tr.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText(O.toString());
        tv3.setLayoutParams(new TableRow.LayoutParams(3));
        tv3.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tv3.setWidth(widthColumn);
        tr.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setText(P.toString());
        tv4.setLayoutParams(new TableRow.LayoutParams(4));
        tv4.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tv4.setWidth(widthColumn);
        tr.addView(tv4);

        mainTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

    }


}
