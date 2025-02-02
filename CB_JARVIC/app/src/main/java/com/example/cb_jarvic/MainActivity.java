package com.example.cb_jarvic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.*;
import java.io.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;


public class MainActivity extends AppCompatActivity implements Serializable{
    private transient Button b1,b2;
    private transient EditText ed1,ed2;
    private transient TextView tv1, tv2;
    private InetAddress ip;
    private int port;
    private transient DataInputStream dis;
    private transient DataOutputStream dos;
    private socket_pass socket;
    private transient Socket s;


    public static class socket_pass implements Serializable{
        public transient Socket s;
        public InetAddress hostname;
        public int port;

        public void get_socket(Socket s, InetAddress host, int port)
        {
            this.s = s;
            this.hostname = hostname;
            this.port = port;
        }
        public Socket return_socket()
        {
            return this.s;
        }
    }

    public static class ObjectWrapperForBinder extends Binder {

        private final socket_pass mData;

        public ObjectWrapperForBinder(socket_pass data) {
            mData = data;
        }

        public socket_pass getData() {
            return mData;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.showOverflowMenu();
        getSupportActionBar().setTitle("");

        Intent i = getIntent();
        if(i.getExtras() != null) {
            try {
                socket = ((MainActivity.ObjectWrapperForBinder) i.getExtras().getBinder("socket_value")).getData();
                ip = socket.hostname;
                port = socket.port;
                s = socket.return_socket();
                if(s != null) {
                    dos = new DataOutputStream(socket.s.getOutputStream());
                    dis = new DataInputStream(socket.s.getInputStream());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText5);
        ed2 = (EditText)findViewById(R.id.editText6);
        tv1 = (TextView) findViewById(R.id.textView3);
        tv2 = (TextView) findViewById(R.id.textView4);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try {
                    if (!ed1.getText().toString().equals("") &&
                            !ed2.getText().toString().equals("")) {
                        connectToServer(ed1.getText().toString(), ed2.getText().toString());
                    } else {
                        if(ed1.getText().toString().equals(""))
                            ed1.setError("Incorrect Email address");
                        if(ed2.getText().toString().equals(""))
                            ed2.setError("Incorrect Password");
                        Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                socket.get_socket(s, ip, port);
                final Bundle bundle = new Bundle();
                bundle.putBinder("socket_value", new ObjectWrapperForBinder(socket));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                socket.get_socket(s, ip, port);
                final Bundle bundle = new Bundle();
                bundle.putBinder("socket_value", new ObjectWrapperForBinder(socket));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    public void connectToServer(final String email, final String password) throws Exception
    {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        if(s == null) {
                            s = new Socket(ip, port);
                            dos = new DataOutputStream(s.getOutputStream());
                            dis = new DataInputStream(s.getInputStream());
                        }
                        socket = new socket_pass();
                        socket.get_socket(s, ip, port);
                        String send = email + "$" + password;
                        dos.writeUTF(send);
                        System.out.println(send);
//                        String success="y";
                        String success = dis.readUTF();
                        System.out.println("\nJ.A.R.V.I.C. - " + success);
                        if(success.equals("y")) {
                            final Bundle bundle = new Bundle();
                            bundle.putBinder("socket_value", new ObjectWrapperForBinder(socket));
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                }
                            });
                            final Bundle bundle = new Bundle();
                            bundle.putBinder("socket_value", new MainActivity.ObjectWrapperForBinder(socket));
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                try {
                    if(s != null)
                        s.close();
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                })
                .show();
    }

}
