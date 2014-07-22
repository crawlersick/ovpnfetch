package com.crawlersick.ovpnfetcher;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.crawlersick.nettool.AppspotSocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.sick.myapplication22.action.FOO";
    private static final String ACTION_BAZ = "com.example.sick.myapplication22.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.sick.myapplication22.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.sick.myapplication22.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Intent localIntent =
                    new Intent("John").putExtra("213123", "1928381821");

            File f_exts=null;
            try {
            String extStore = System.getenv("EXTERNAL_STORAGE");
            f_exts = new File(extStore);
            Log.i("extstore", f_exts.getAbsolutePath());
            }catch(Exception e)
            {
                Log.i("sndstore", "no 1nd storage!");
            }


            File f_secs=null;
            try {
                String secStore = System.getenv("SECONDARY_STORAGE");
                f_secs = new File(secStore);
                Log.i("sndstore", f_secs.getAbsolutePath());
            }catch(Exception e)
            {
                Log.i("sndstore", "no 2nd storage!");
            }

            String targetFolder=null;
            String targetBKFolder=null;
            if(f_secs!=null)
            {

                targetFolder=f_secs.getAbsoluteFile()+"/";
            }
            else
            {
                if(f_exts!=null)
                targetFolder=f_exts.getAbsoluteFile()+"/";
            }

            if(targetFolder==null)
            {
                localIntent.putExtra("213123", "No Storage found!");
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                return;
            }

            targetFolder=targetFolder+"ovpnconf/";

            File tempFl=new File(targetFolder);
            if(tempFl.exists())
            {
                if(!tempFl.isDirectory())
                {
                    tempFl.delete();
                    tempFl.mkdir();
                }
            }
            else
            {
                tempFl.mkdir();
            }

            targetBKFolder=targetFolder+"bk/";
            File tempBKFl=new File(targetBKFolder);
            if(tempBKFl.exists())
            {
               // deleteFolder(tempBKFl);
               // tempBKFl.mkdir();
                File[] files = new File(targetFolder).listFiles();
                Log.i("before loop", "1111");
                for(File f:files)
                {
                    Log.i(" loop",f.getName());
                    if(!f.isDirectory()) {
                        f.getName();
                        Log.i("get filename to move ", f.getName());
                        try {
                            copyFile(f, new File(targetBKFolder + f.getName()));
                            f.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i("get filename to move exception", e.toString());
                        }
                    }
                }

            }else{

                tempBKFl.mkdir();
            }




            final String action = intent.getAction();
            //Toast.makeText(getApplicationContext(), action, Toast.LENGTH_LONG).show();

            Log.i("KKKKKKKKKKKKKKKKKKKK", intent.getStringExtra("p1"));



                            // Puts the status into the Intent


            localIntent.putExtra("213123", "Start processing, may takes 5-10 minutes, please wait... ");
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            // Broadcasts the Intent to receivers in this app.

            try {
                AppspotSocket appsock= new AppspotSocket("vpngatefetch",targetFolder,localIntent,this);
                Log.i("Device:", android.os.Build.MODEL);

                String deviceinfo= URLEncoder.encode(android.os.Build.MODEL,"UTF-8");
                String restr=appsock.URLConmunicate("urlfopenvpn?qtype=http://www.vpngate.net/api/iphone/&device=mobile"+deviceinfo,localIntent,this);
                int delaynum=120;
                int speednum=2500000;
                appsock.resultAnalyst(restr,delaynum,speednum,targetFolder);
                appsock.closeappsocket();

                localIntent.putExtra("213123", "Done, Please find ovpn files in "+targetFolder);
            } catch (Exception ex) {
                ex.printStackTrace();

                localIntent.putExtra("213123", "Failed,Please re-try: "+ex.toString());
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

            /*
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
            */
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
}
