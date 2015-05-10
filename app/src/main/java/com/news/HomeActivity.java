package com.news;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class HomeActivity extends ActionBarActivity {
    // Initialize all the variables that used in the activity
    final Context context = this;
    // Create a broadcast receiver to get message and show on screen
    String user="";
    String regId="";
    String data="";
    public static final String userstr = "usrstr";
    public static final String MyPREFERENCES = "MyPrefs";
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Controller aController = (Controller) getApplicationContext();
            String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            aController.acquireWakeLock(getApplicationContext());
            // Display message on the screen
            //lblMessage.append(newMessage + "\n");4
            System.out.println("meeeese" + newMessage);
            Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_LONG).show();
            // Releasing wake lock
            aController.releaseWakeLock();
            //Toast.makeText(getBaseContext(), "msg reciverd"+newMessage, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                System.out.println("Error Occured "+e.getMessage());
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(HomeActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String sess = "" + sharedPreferences.getString(HomeActivity.userstr, null);
        if (!sess.equals("null")) {
            finish();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
        Intent intent=getIntent();
        user=""+intent.getStringExtra(MainActivity.user);
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
    }

    public void tohome(View view){
        CheckBox checkBox1= (CheckBox) findViewById(R.id.checkBox1);
        CheckBox checkBox2= (CheckBox) findViewById(R.id.checkBox2);
        CheckBox checkBox3= (CheckBox) findViewById(R.id.checkBox3);
        CheckBox checkBox4= (CheckBox) findViewById(R.id.checkBox4);
        if(checkBox1.isChecked())
            data+=checkBox1.getText().toString()+",";
        if(checkBox2.isChecked())
            data+=checkBox2.getText().toString()+",";
        if(checkBox3.isChecked())
            data+=checkBox3.getText().toString()+",";
        if(checkBox4.isChecked())
            data+=checkBox4.getText().toString()+",";
        new Sendlogin().execute();

    }
    private class Sendlogin extends AsyncTask<String,Void,String> {
        final ProgressDialog progDailog = ProgressDialog.show(HomeActivity.this, "Loading", "Please Wait....", true);
        @Override
        protected String doInBackground(String... params) {
            String str="";String line="";
            try{
                HttpClient client=new DefaultHttpClient();
                HttpPost post=new HttpPost("https://sleepy-cliffs-3321.herokuapp.com/app/signup?name="+user+"&deviceId="+regId+"&pass=password&topics="+data);
                HttpResponse response=client.execute(post);
                BufferedReader bf=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line=bf.readLine()) != null){
                    str+=line;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            progDailog.dismiss();
            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            send(s);
        }
    }
    public void send(String s){
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userstr, data);
        editor.commit();
        Toast.makeText(getBaseContext(), "SignUp Successfully ", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this,Home.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
