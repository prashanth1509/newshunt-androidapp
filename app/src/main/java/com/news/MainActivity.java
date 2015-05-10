package com.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {
    EditText editText;
    EditText editText1;
    public String regId="";
    public static final String user="name";
    public static final String devid="div";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the text fields
        editText=(EditText) findViewById(R.id.user);
        editText1=(EditText) findViewById(R.id.pass);
        SharedPreferences sharedPreferences = getSharedPreferences(HomeActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String sess = "" + sharedPreferences.getString(HomeActivity.userstr, null);
        if (!sess.equals("null")) {
            finish();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                System.out.println("Error Occured "+e.getMessage());
            }
        });
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest permissions was properly set
        GCMRegistrar.checkManifest(this);

        // Get GCM registration id
        regId = GCMRegistrar.getRegistrationId(this);

        // Check if regid already presents
        if (regId.equals("")) {
            GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
            regId = GCMRegistrar.getRegistrationId(this);
            GCMIntentService gcmIntentService=new GCMIntentService();
            regId=gcmIntentService.REGGID;
        }
        System.out.println("My registed ID "+regId);
    }
    public void signup(View view){
        String user=editText.getText().toString();
        String pass=editText1.getText().toString();
        if(user.length()==0)
            editText.setError("Enter the Username");
        else if(pass.length()==0)
            editText1.setError("Enter the Password");
        else
            send(user);
    }

    public void send(String s){
        String usr=editText.getText().toString();
        //Toast.makeText(getBaseContext(), "SignUp Successfully "+regId, Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this,HomeActivity.class);
        intent.putExtra(user,usr);
        intent.putExtra(devid,regId);
        startActivity(intent);
        finish();
    }


}
