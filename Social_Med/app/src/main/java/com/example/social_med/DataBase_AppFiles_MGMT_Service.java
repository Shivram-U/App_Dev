package com.example.social_med;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;


public class DataBase_AppFiles_MGMT_Service extends Service
{
    public String StDIR,DIR;
    public com.example.social_med.jsn jn;
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

    public DataBase_AppFiles_MGMT_Service()
    {
        this.StDIR = String.valueOf(Environment.getExternalStorageDirectory());
        this.DIR = this.StDIR+"/Dev_Apps_Dir/General_Chat";
        jn = new jsn(this.DIR,this.StDIR);
    }
    @Override

    // execution of service will start
    // on calling this method
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("Message","DataBase_AppFiles_MGMT_Service Service Started");
        try {
            if (!this.Create_Dir_File("Dev_Apps_Dir/General_Chat", "Connection.json")) {
                this.jn.Write_Json("Connection");
            }
            if (!this.Create_Dir_File("Dev_Apps_Dir/General_Chat", "Data.json")) {
                this.jn.Write_Json("Data");
            }
        }
        catch(Exception e)
        {
            Log.i("Message",e.getMessage());
        }
        return START_STICKY;
    }

    @Override
    // execution of the service will
    // stop on calling this method
    public void onDestroy() {
        Log.i("Message","DataBase_AppFiles_MGMT_Service Service Stopped");
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
