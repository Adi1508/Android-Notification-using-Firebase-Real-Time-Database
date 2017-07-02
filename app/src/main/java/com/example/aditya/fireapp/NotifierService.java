package com.example.aditya.fireapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 30/6/17.
 */

public class NotifierService extends Service {

    SharedPreferences sharedPrefrences;
    public FirebaseDatabase mDatabase;
    Context context;
    static String TAG ="FirebaseService";
    public String publickey;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yeet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        sharedPrefrences= PreferenceManager.getDefaultSharedPreferences(this);
        mDatabase=FirebaseDatabase.getInstance();

        System.out.println("this method is called first");
        setupNotificationListener();
    }

    private boolean alreadyNotified(String key){
        Log.d("Method called","method called");
        if(sharedPrefrences.getBoolean(key,false)){
            return true;
        }
        else{
            return false;
        }
    }

    private void saveNotificationKey(String key){
        Log.d("Method called","method called");
        SharedPreferences.Editor editor=sharedPrefrences.edit();
        editor.putBoolean(key,true);
        editor.commit();
    }

    private void setupNotificationListener(){

        //the query is used to fetch the last updated value from the database
        
        //Query lastQuery = mDatabase.getReference().child("posts").orderByKey().limitToLast(1);
        Query query = mDatabase.getReference().child("posts").orderByChild("date").limitToLast(1);
        //Query lquery = mDatabase.getReference().child("posts").orderByKey();

        query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot!=null){
                            String notific = String.valueOf(dataSnapshot.getValue());
                            Log.d("key",dataSnapshot.getKey());
                            Log.d("title",String.valueOf(dataSnapshot.child("title").getValue()));
                            Log.d("content",String.valueOf(dataSnapshot.child("content").getValue()));

                            if(alreadyNotified(dataSnapshot.getKey())){
                                System.out.print("already notified");
                            }else{
                                Log.d("not notified","");
                                createNotif(notific);
                                saveNotificationKey(dataSnapshot.getKey());
                            }

                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent service = new Intent(this, NotifierService.class);
        startService(service);
    }

    private void createNotif(String value) {

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle("Notification Example")
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(value);

        Intent notificationintent=new Intent(this,MainActivity.class);

        PendingIntent contentIntent=PendingIntent.getActivity(this,0,notificationintent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setAutoCancel(true);
        manager.notify(0,builder.build());
    }
}
