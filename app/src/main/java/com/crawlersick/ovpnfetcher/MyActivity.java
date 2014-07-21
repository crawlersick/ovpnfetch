package com.crawlersick.ovpnfetcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyActivity extends FragmentActivity implements OnBackStackChangedListener{

    private Intent mServiceIntent;
    private msgReceiver mFragmentDisplayer = new msgReceiver();
    IntentFilter statusIntentFilter = new IntentFilter(
            "John");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        TextView tvId = (TextView) findViewById(R.id.textView);
        tvId.setMovementMethod(new ScrollingMovementMethod());

        // Sets the filter's category to DEFAULT
        //statusIntentFilter.addCategory("String");

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mFragmentDisplayer,statusIntentFilter
        );

        // Inflate the menu; this adds items to the action bar if it is present.

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startFetch(View view)
    {


      //  Toast.makeText(getApplicationContext(), "Button is clicked", Toast.LENGTH_LONG).show();
        mServiceIntent = new Intent(this, MyIntentService.class);
        mServiceIntent.setAction("abc");
        mServiceIntent.putExtra("p1","data1");
        this.startService(mServiceIntent);
        Button bt=(Button)findViewById(R.id.Start_button);
        bt.setEnabled(false);


    }

    @Override
    public void onBackStackChanged() {

    }


    /*
 * This callback is invoked when the system is about to destroy the Activity.
 */
    @Override
    public void onDestroy() {

        // Unregisters the FragmentDisplayer instance
        if(mFragmentDisplayer!=null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mFragmentDisplayer);
            mFragmentDisplayer=null;
        }
        super.onDestroy();
    }
    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }


    private class msgReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String msg=intent.getStringExtra("213123");

            Log.i("Receive",msg);
            final TextView tvId = (TextView) findViewById(R.id.textView);
            tvId.append("\n");
            tvId.append(msg);

            if(msg.indexOf("Done")==1||msg.indexOf("Failed")==1)
            {
                Button bt=(Button)findViewById(R.id.Start_button);
                bt.setEnabled(true);
            }

            tvId.post(new Runnable() {
                @Override
                public void run() {
                    int scrollAmount = tvId.getLayout().getLineTop(tvId.getLineCount()) - tvId.getHeight();
                    // if there is no need to scroll, scrollAmount will be <=0
                    if (scrollAmount > 0)
                        tvId.scrollTo(0, scrollAmount);
                    else
                        tvId.scrollTo(0, 0);
                }
            });


           // Toast.makeText(getApplicationContext(), "Button is clicked", Toast.LENGTH_LONG).show();
        }
    }



}


