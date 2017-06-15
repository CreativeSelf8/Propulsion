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
                String notiId = dataSnapshot.getKey();
                String content = message.getContent();
                int hashCode = message.getSenderId().hashCode();
                String ideaId = message.getIdeaId();
                String status = message.getStatus();
                Intent intent = new Intent(ReceiveNotificationService.this, DetailIdeaActivity.class);
                intent.putExtra("IDEA_ID", ideaId);
                if (status.equals("0")) {
                    pushNoti(content, hashCode, intent);
                    setCheckedNotification(notiId);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                String notiId = dataSnapshot.getKey();
                String content = message.getContent();
                int hashCode = message.getSenderId().hashCode();
                String ideaId = message.getIdeaId();
                String status = message.getStatus();
                Intent intent = new Intent(ReceiveNotificationService.this, DetailIdeaActivity.class);
                intent.putExtra("IDEA_ID", ideaId);
                if (status.equals("0")) {
                    pushNoti(content, hashCode, intent);
                    setCheckedNotification(notiId);
                }
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

    private void setCheckedNotification(String notiId) {
        databaseReference.child(CHILD_NOTIFICATION).child(currentFirebaseUser.getUid()).child(notiId).child("status").setValue("1");
    }
}
