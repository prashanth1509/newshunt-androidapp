package com.news;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Home extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    String newid="";
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Controller aController = (Controller) getApplicationContext();
            String newMessage = intent.getExtras().getString("NS_MSG");
            // Waking up mobile if it is sleeping
            aController.acquireWakeLock(getApplicationContext());
            // Display message on the screen
            //lblMessage.append(newMessage + "\n");4
            System.out.println("meeeese" + newMessage);
            Toast.makeText(getApplicationContext(), "Got Message: " + newMessage, Toast.LENGTH_LONG).show();
            // Releasing wake lock
            aController.releaseWakeLock();
            String[] ex=newMessage.split(",");
            newid=ex[2];
            //Toast.makeText(getBaseContext(), "msg reciverd"+newMessage, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(GCMIntentService.MyPREFERENCES, Context.MODE_PRIVATE);
        String sess = "" + sharedPreferences.getString(GCMIntentService.userstr, null);
        if(!sess.equals("null")) {
            String[] ex = sess.split(",");
            newid = ex[2];
            new Sendlogin().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(GCMIntentService.MyPREFERENCES, Context.MODE_PRIVATE);
        String sess = "" + sharedPreferences.getString(GCMIntentService.userstr, null);
        if(!sess.equals("null")) {
            String[] ex = sess.split(",");
            newid = ex[2];
            new Sendlogin().execute();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                System.out.println("Error Occured "+e.getMessage());
            }
        });
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }

    }
    private class Sendlogin extends AsyncTask<String,Void,String> {
        final ProgressDialog progDailog = ProgressDialog.show(Home.this, "Loading", "Please Wait....", true);
        @Override
        protected String doInBackground(String... params) {
            String str="";String line="";
            try{
                HttpClient client=new DefaultHttpClient();
                System.out.println("https://sleepy-cliffs-3321.herokuapp.com/app/news?newsId="+newid);
                HttpPost post=new HttpPost("https://sleepy-cliffs-3321.herokuapp.com/app/news?newsId="+newid);
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
        System.out.println("message "+s);
        try {
            JSONObject object=new JSONObject(s);
            TextView textView=(TextView) findViewById(R.id.newss);
            textView.setText(""+object.getString("content"));
            TextView textView1=(TextView) findViewById(R.id.heading);
            textView1.setText(""+object.getString("heading"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Home) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
