package com.example.social_med;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class jsn
{
    public String DIR,StDIR;
    jsn(String d,String sd)
    {
        this.DIR = d;
        this.StDIR = sd;
    }
    public void Write_Json(String d)
    {
        try {
            JSONObject fil = new JSONObject();
            if(d == "Data") {
                fil.put("Application Name", "General Chat");
                fil.put("Developer", "Shivram_U");
                fil.put("Year", "2023");
                fil.put("Status", "Under Development");
                FileWriter fw = new FileWriter(this.DIR + "/Data.json");
                fw.write(fil.toString());
                fw.flush();
                fw.close();
            }
            else if(d == "Connection")
            {
                fil.put("Server_IP", "192.168.140.144");
                fil.put("Server_Port", "3333");
                FileWriter fw = new FileWriter(this.DIR + "/Connection.json");
                fw.write(fil.toString());
                fw.flush();
                fw.close();
            }
        }
        catch(Exception e){
            Log.i("Exception",e.getMessage());
        }
        Log.i("Message","Write done");
    }
    public JSONObject Read_Json(String filename)
    {
        try {
            File fil = new File(this.DIR + "/" + filename + ".json");
            FileReader fr = new FileReader(fil);
            BufferedReader fbr = new BufferedReader(fr);
            String line = fbr.readLine();
            Log.i("Message", line);
            JSONObject rd = new JSONObject(line);
            //Log.i("Message", rd.get("Developer").toString());
            return rd;
        }
        catch(Exception e)
        {
            Log.i("Message",e.getMessage());
        }
        return null;
    }
}