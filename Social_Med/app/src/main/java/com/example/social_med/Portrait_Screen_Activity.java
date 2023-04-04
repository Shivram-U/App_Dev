package com.example.social_med;

import java.net.*;
import java.io.*;
import java.util.*;
import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.app.ActivityManager;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import org.json.JSONObject;

import android.view.MenuItem;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.util.concurrent.ExecutionException;


/*

Important Points:
            1. getResources().getConfiguration().orientation
            2. public void onConfigurationChanged(Configuration newConfig)
            3. R.layout.landscape_activity
 */

class inpt   implements Runnable
{
    public Portrait_Screen_Activity ps;
    public Socket soc;
    public inpt(Socket soc,Portrait_Screen_Activity ps)
    {
        this.soc = soc;
        this.ps = ps;
    }
    @Override
    public void run()
    {
        try {
            DataInputStream read = new DataInputStream(soc.getInputStream());
            String in="",out="";
            while(!in.equals("Disconnect"))
            {
                Log.i("message",in);
                in = read.readUTF();
                ps.str = in;
                ps.update();
            }
            read.close();
        }
        catch(Exception e)
        {
            Log.i("Message",e.getMessage());
        }
    }
}
class Msg_Thread extends Thread
{
    public Portrait_Screen_Activity ps;
    public inpt inp;
    public JSONObject jsn;
    public Msg_Thread(Portrait_Screen_Activity ps)
    {
        this.ps = ps;
    }
    @Override
    public void run() {
        try {
            Log.i("message", "begin: ");
            jsn = ps.jn.Read_Json("Connection");
            if (jsn != null) {
                try {
                    Log.i("Values", String.valueOf(jsn.get("Server_IP")));
                    Log.i("Values", jsn.get("Server_Port").toString());
                    Socket soc = new Socket(String.valueOf(jsn.get("Server_IP")), Integer.parseInt(String.valueOf(jsn.get("Server_Port"))));
                    inp = new inpt(soc, this.ps);
                    Log.i("message", "Connected: ");
                    Log.i("message", "Input thread Start");
                    // Input thread
                    inp.run();
                }
               catch(Exception e)
                {
                    Log.i("Message", e.getMessage());
                }
            }
            else
            {
                Log.i("message","Fault in Connection File parsing, jsn returned null");
            }
            // DataOutputStream wrt = new DataOutputStream(soc.getOutputStream());
            Log.i("message","Server Disconnected");
        }
        catch (Exception e)
        {
            // ps.Con_But.setText("error");
            Log.i("Message", e.getMessage());
        }
        //ps.Con_But.setText("done");
    }
}
class Thread_mgmt
{
    public Portrait_Screen_Activity ps;
    Thread[] thrs;
    Thread_mgmt(Portrait_Screen_Activity ps)
    {
        this.ps = ps;
        thrs = new Thread[10];
    }
}
public class Portrait_Screen_Activity extends AppCompatActivity implements View.OnClickListener{

    ImageView img;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public Button Con_But;
    public EditText cons;
    // Values:
    public String str = ">",StDIR,DIR;
    // Non-UI Threads:
    public Thread ConThr;
    // UI Threads
    public Handler h;
    // Json
    public com.example.social_med.jsn jn;
    //
    // Permissions:
    private static final int MANAGE_PERMISSION_CODE = 100;
    private static final int WRITE_PERMISSION_CODE = 101;
    private static final int READ_PERMISSION_CODE = 102;
    public Portrait_Screen_Activity()
    {
        // Variable Initialisation:
        this.ConThr = new Msg_Thread(this);
        this.StDIR = String.valueOf(Environment.getExternalStorageDirectory());
        this.DIR = this.StDIR+"/Dev_Apps_Dir/General_Chat";
        jn = new jsn(this.DIR,this.StDIR);
    }
    // Updation
    public void update()
    {
        // this.cons.setText(this.str);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // UI updation related code.
                cons.setText(cons.getText()+str+"\n");
            }
        });
    }
    // App Directory Creation
    public boolean Create_Dir_File(String dir,String fn)   throws IOException
    {
        boolean res;
        File file;
        if(fn!="")
            file = new File(this.StDIR+"/"+dir+"/"+fn);
        else
            file = new File(this.StDIR+"/"+dir);
        if (!file.exists())
        {
            if(fn=="")
                res = file.mkdir();
            else
                res = file.createNewFile();
            //Log.i("Message_dir",String.valueOf(res)+": "+fn);
            if(res)
            {
                Log.i("Message",dir+" "+fn+" created");
            }
            return false;
        }
        else
        {
            Log.i("Message",dir+" "+fn+" already exists");
            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // App Setup // Code to be executed only at the Beginning.
        //
        SharedPreferences Prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("Message","Check1: "+String.valueOf(Prefs.getBoolean("firstTime",false)));
        /*
        Error:
            Before checking the Syntax, or Data parsing Error, in Data Files Establishment, check the Permissions,
            provided to the App, in the Mobile.
        */
        try {
        this.Create_Dir_File("Dev_Apps_Dir", "");
        this.Create_Dir_File("Dev_Apps_Dir/General_Chat", "");
        }
        catch(Exception e)
        {
            Log.i("Message",e.getMessage());
        }
        if (Prefs.getBoolean("firstTime",true))
        {
            // Code to run once
            // Storage Establishment:
                // Permissions Establishment:
                startService(new Intent( this, Permissions_Service.class ) );
            //file.delete();
            try {
                // Directory Establishment
                this.Create_Dir_File("Dev_Apps_Dir", "");
                this.Create_Dir_File("Dev_Apps_Dir/General_Chat", "");
                Log.i("Message","Dir create");
            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }

            //File file = new File(Environment.getExternalStorageDirectory()+"/Dev_Apps_DIR/General_Chat");
            //File file = new File(Environment.getDataDirectory()+"/Dev_Apps_DIR/General_Chat");
            //File file = new File(Environment.getExternalStorageDirectory()+"/Dev_Apps_DIR/General_Chat");
            //File file = new File(Environment.getExternalStorageDirectory()+"/Dev_Apps_DIR/General_Chat");
            //File mydir = context.getDir("mydirectory", Context.MODE_PRIVATE)
            Log.i("Message", String.valueOf(Environment.getDataDirectory()));
            Log.i("Message", String.valueOf(Environment.getExternalStorageDirectory()));
            Log.i("Message", String.valueOf(Environment.getRootDirectory()));
            //Log.i("Message",String.valueOf(Environment.getStorageDirectory()));
            // Log.i("DIR",(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
            // checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            //

            //
            //
            SharedPreferences.Editor editor = Prefs.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
        }
        //
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            // the above if condition gets the orientation value from getResources() method, to infer the current Orientation of the Device.
        {
            setContentView(R.layout.landscape_activity);
            System.out.println("ls");
        } else if (getResources().getConfiguration().orientation  == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.portrait_activity);
            System.out.println("pr");
        }
        // UI Setup:
            drawerLayout = findViewById(R.id.portrait_drawer_layout);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            cons = findViewById(R.id.Console);
            // to make the Navigation drawer icon always appear on the action bar
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        // Event
            Con_But = findViewById(R.id.Connect_Server);
            Con_But.setOnClickListener(this);
        //
        // Services Startup
        startService(new Intent( this, DataBase_AppFiles_MGMT_Service.class ) );
        /*
        if(isMyServiceRunning(DataBase_AppFiles_MGMT_Service.class))
        {
            Log.i("Message","DataBase_AppFiles_MGMT_Service Service Started");
        }
        else
            Log.i("Message","DataBase_AppFiles_MGMT_Service Service not Started");

         */
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.landscape_activity);
            System.out.println("ls");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.portrait_activity);
            System.out.println("pr");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Event
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Connect_Server: {
                Log.i("Message", "Connect Button click");
                //Con_But.setText("meow");
                this.ConThr.start();
                //this.ConThr = new Msg_Thread(this);
            }
            // Do something
        }
    }
}