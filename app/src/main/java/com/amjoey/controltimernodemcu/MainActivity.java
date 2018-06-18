package com.amjoey.controltimernodemcu;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import simpletcp.*;
public class MainActivity extends AppCompatActivity {

    public static final int TCP_PORT = 21111;
    private SimpleTcpServer simpleTcpServer;

    private TextView textViewTime;
    private Button buttonSend;

    private String nodemcuip ="192.168.1.44";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTime  = findViewById(R.id.timeText);
        buttonSend = findViewById(R.id.buttonSend);


        textViewTime.setText(TcpUtils.getIpAddress(this) + ":" + TCP_PORT);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Send message and waiting for callback
                SimpleTcpClient.send("Return\r\n", nodemcuip, TCP_PORT, new SimpleTcpClient.SendCallback() {
                    public void onReturn(String tag) {
                        String[] arr_state = tag.split(",");
                        textViewTime.setText(arr_state[1]+"--"+arr_state[2]);
                    }

                    public void onFailed(String tag) {
                        Toast.makeText(getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
                    }
                }, "TAG");

            }
        });

        simpleTcpServer = new SimpleTcpServer(TCP_PORT);
        simpleTcpServer.setOnDataReceivedListener(new SimpleTcpServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {

                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();

            }
        });



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


    public void onResume() {
        super.onResume();
        simpleTcpServer.start();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 500; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // Send message and waiting for callback
                            SimpleTcpClient.send("Return\r\n", nodemcuip, TCP_PORT, new SimpleTcpClient.SendCallback() {
                                public void onReturn(String tag) {
                                    String[] arr_state = tag.split(",");
                                    textViewTime.setText(timeformat(Integer.parseInt(arr_state[1]))+"--"+arr_state[2]);
                                }

                                public void onFailed(String tag) {
                                  //  Toast.makeText(getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
                                }
                            }, "TAG");
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();

    }

    public void onStop() {
        super.onStop();
        simpleTcpServer.stop();
    }


}
