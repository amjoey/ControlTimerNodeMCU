package com.amjoey.controltimernodemcu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.view.View;
import android.database.Cursor;
import android.widget.Toast;

import java.util.Calendar;

import static com.amjoey.controltimernodemcu.MainActivity.padding;
import static com.amjoey.controltimernodemcu.MainActivity.timeformat;
import static com.amjoey.controltimernodemcu.MainActivity.timetoint;

public class EditSetTime extends Activity{

    private EditText etTimeOn;
    private EditText etTimeOff;
    private Button etButtonOK,etButtonCancel;

    ImageButton imgTimeON,imgTimeOFF;
    private int chour,cminute;

    static final int TIME_ON_ID=0;
    static final int TIME_OFF_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_set_time);

        final Calendar calendar=Calendar.getInstance();
        chour=calendar.get(Calendar.HOUR_OF_DAY);
        cminute=calendar.get(Calendar.MINUTE);

        final int recID = getIntent().getIntExtra("recID", 0);


        final DatabaseHandler mydb = new DatabaseHandler(this);
        Cursor cursor = mydb.getEditRecord(recID);

        etTimeOn = (EditText) findViewById(R.id.timeON);
        etTimeOff = (EditText) findViewById(R.id.timeOFF);

        etButtonOK = (Button)findViewById(R.id.button_submit);

        etButtonCancel = (Button)findViewById(R.id.button_cancel);

        imgTimeON = (ImageButton) findViewById(R.id.imgTimeON);
        imgTimeOFF = (ImageButton) findViewById(R.id.imgTimeOFF);

        imgTimeON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_ON_ID);
            }
        });

        imgTimeOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_OFF_ID);
            }
        });

        if(cursor.getCount()==1){

            cursor.moveToFirst();
            etTimeOn.setText(timeformat(Integer.parseInt(cursor.getString(cursor.getColumnIndex("timeON")))));
            etTimeOff.setText(timeformat(Integer.parseInt(cursor.getString(cursor.getColumnIndex("timeOFF")))));


        }
        etButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isUpdated = mydb.updateTime(recID,timetoint(etTimeOn.getText().toString()),timetoint(etTimeOff.getText().toString()));
                if(isUpdated == true) {
                    Toast.makeText(EditSetTime.this, "Data Updated", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                    Toast.makeText(EditSetTime.this,"Data not Updated",Toast.LENGTH_LONG).show();
            }
        });

        etButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private TimePickerDialog.OnTimeSetListener mStartTime=new TimePickerDialog.OnTimeSetListener()
    {
        public void onTimeSet(TimePicker view, int hourofday, int min)
        {
            etTimeOn.setText(new StringBuilder().append(padding(hourofday))
                    .append(":").append(padding(min)));
        }
    };

    private TimePickerDialog.OnTimeSetListener mEndTime=new TimePickerDialog.OnTimeSetListener()
    {
        public void onTimeSet(TimePicker view,int hourofday,int min)
        {
            etTimeOff.setText(new StringBuilder().append(padding(hourofday))
                    .append(":").append(padding(min)));
        }
    };

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case TIME_ON_ID:
                return new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,mStartTime,chour,cminute,false);

            case TIME_OFF_ID:
                return new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,mEndTime,chour,cminute,false);
        }
        return null;
    }
}
