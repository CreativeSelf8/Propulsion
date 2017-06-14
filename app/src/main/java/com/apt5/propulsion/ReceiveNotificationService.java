package com.apt5.propulsion;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.apt5.propulsion.activity.DetailIdeaActivity;
import com.apt5.propulsion.object.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.apt5.propulsion.ConstantVar.CHILD_NOTIFICATION;

/**
 * Created by Van Quyen on 6/14/2017.
 */

public class ReceiveNotificationService extends Service {
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentFirebaseUser;

    public ReceiveNotificationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = firebaseAuth.getCurrentUser();

        if (currentFirebaseUser == null) {
            return;
        }

        databaseReference.child(CHILD_NOTIFICATION).child(currentFirebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("DATA===", dataSnapshot.toString());
                Message message = dataSnapshot.getValue(Message.class);
                String content = message.getContent();
                int hashCode = message.getSenderId().hashCode();
                String ideaId = message.getIdeaId();
                Intent intent = new Intent(ReceiveNotificationService.this, DetailIdeaActivity.class);
                intent.putExtra("IDEA_ID", ideaId);
                pushNoti(content, hashCode, intent);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void pushNoti(String content, int id, Intent resultIntent) {
        NotificationCompat.Builder mBuilder;
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(ReceiveNotificationService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLights(Color.BLUE, 500, 500)
                .setContentTitle("Propulsion")
                .setContentText(content)
                .setVibrate(new long[]{100, 250, 100, 250, 100, 250})
                .setAutoCancel(true)
                .setColor(R.color.colorPrimaryDark)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification noti = mBuilder.build();

        mNotificationManager.notify(id, noti);
    }
}
