package com.amjoey.controltimernodemcu;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.widget.ListView;
import android.database.Cursor;
import android.graphics.Color;
import android.view.ViewGroup;
import android.support.v4.widget.SimpleCursorAdapter;

import simpletcp.*;
public class MainActivity extends ListActivity  {

    public static final int TCP_PORT = 21111;
    private SimpleTcpServer simpleTcpServer;

    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    private String nodemcuip ="192.168.1.44";

    DatabaseHandler mydb ;


    private void updateData()
    {
        mydb = new DatabaseHandler(this);

        cursor = mydb.getAllRecord();
        adapter = new MyCursorAdapter(
                this,
                R.layout.row_layout,
                cursor,
                new String[] {"_id", "timeON", "timeOFF"},
                new int[] {R.id.id, R.id.timeON, R.id.timeOFF},
                0);
        setListAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getListView() != null)
        {
            updateData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DatabaseHandler(this);
        cursor = mydb.getAllRecord();
        adapter = new MyCursorAdapter(
                this,
                R.layout.row_layout,
                cursor,
                new String[] {"_id", "timeON", "timeOFF"},
                new int[] {R.id.id, R.id.timeON, R.id.timeOFF},
                0);
       setListAdapter(adapter);
    }
private class MyCursorAdapter extends SimpleCursorAdapter{

        private Cursor c;
        private Context context;
        private Bundle bundle;

        public MyCursorAdapter(Context context, int layout, Cursor c,
                               String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.c = c;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //get reference to the row
            View view = super.getView(position, convertView, parent);

            this.c.moveToPosition(position);
            String timeON = Integer.toString(this.c.getInt(this.c.getColumnIndex("timeON")));
            String timeOFF = Integer.toString(this.c.getInt(this.c.getColumnIndex("timeOFF")));

            TextView timeOnTextView = (TextView)view.findViewById(R.id.timeON);
            timeOnTextView.setText(timeformat(Integer.parseInt(timeON)));

            TextView timeOffTextView = (TextView)view.findViewById(R.id.timeOFF);
            timeOffTextView.setText(timeformat(Integer.parseInt(timeOFF)));

            //check for odd or even to set alternate colors to the row background
            if(position % 2 == 0){
                view.setBackgroundColor(Color.rgb(238, 233, 233));
            }
            else {
                view.setBackgroundColor(Color.rgb(255, 255, 204));
            }
            return view;
        }

    }
   public void onListItemClick(ListView parent, View view, int position, long id) {

        Intent intent = new Intent(this, EditSetTime.class);
        Cursor cursor = (Cursor) adapter.getItem(position);
        intent.putExtra("recID", cursor.getInt(cursor.getColumnIndex("_id")));
        startActivity(intent);

    }
    public static String timeformat(int t){
        String intTime,first,second;
        if(t>0) {
            intTime = String.valueOf(Integer.toHexString(t));
            first = padding(Integer.parseInt(intTime.substring(0, intTime.length() / 2)));
            second = padding(Integer.parseInt(intTime.substring(intTime.length() / 2)));
        }else{
            first = "00";
            second = "00";
        }
        return first+ ":"  +second;
    }

    public static int timetoint(String s){
        int setTime;
        String[] separated = s.split(":");

        String first = separated[0];
        String second =separated[1];
        setTime =Integer.parseInt(first+second,16);
        return setTime;
    }

    public static String padding(int c){
        if(c>=10)
            return String.valueOf(c);
        else
            return "0"+ String.valueOf(c);
    }


}
