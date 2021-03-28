//package com.alexz.messenger.app.data.services;
//
//import android.annotation.SuppressLint;
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.IBinder;
//import android.os.SystemClock;
//import android.util.Log;
//import android.util.Pair;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.alexz.messenger.app.data.model.imp.Chat;
//import com.alexz.messenger.app.data.model.interfaces.IMessage;
//import com.alexz.messenger.app.data.repo.DialogsRepository;
//import com.alexz.messenger.app.ui.activities.ChatActivity;
//import com.alexz.messenger.app.util.FirebaseUtil;
//import com.alexz.messenger.app.util.NotificationUtil;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.DataSource;
//import com.bumptech.glide.load.engine.GlideException;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.ValueEventListener;
//import com.messenger.app.BuildConfig;
//import com.messenger.app.R;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class NewMessageService extends Service {
//
//    private static final String TAG = NewMessageService.class.getSimpleName();
//
//    private final Map<String, DatabaseReference> chatRefs = new HashMap<>();
//    private final Map<String, ValueEventListener> chatListeners = new HashMap<>();
//    private Pair<DatabaseReference, ChildEventListener> userChatsRefAndListener;
//
//    private int counter = 0;
//
//    @Override
//    public void onDestroy() {
//        stopListening();
//        super.onDestroy();
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        startListening();
//        return Service.START_STICKY;
//    }
//
//    private void startListening(){
//        if (userChatsRefAndListener == null) {
//
//            DatabaseReference ref = DialogsRepository.getChatIds();
//
//            ChildEventListener listener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    String chatId = snapshot.getKey();
//                    if (chatId != null) {
//                        observeChat(chatId);
//                    }
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    if (BuildConfig.DEBUG) {
//                        Log.d(TAG, "ChatId changed");
//                    }
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                    String chatId = snapshot.getValue(String.class);
//                    if (chatId != null) {
//                        stopObserve(chatId);
//                    }
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    if (BuildConfig.DEBUG) {
//                        Log.w(TAG, "[!!!] ChatId moved from user profile ?!?");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    if (BuildConfig.DEBUG) {
//                        Log.e(TAG, error.toString());
//                    }
//                }
//            };
//
//            ref.addChildEventListener(listener);
//            userChatsRefAndListener = new Pair<>(ref, listener);
//        } else {
//            if (BuildConfig.DEBUG) {
//                Log.w(TAG, "[!] Tried to set up second chats listener. Blocked");
//            }
//        }
//
//    }
//
//    private void observeChat(String chatId){
//        DatabaseReference chatRef = DialogsRepository.getChat(chatId);
//        chatRefs.put(chatId,chatRef);
//        ValueEventListener listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Chat chat = snapshot.getValue(Chat.class);
//                if (chat !=null){
//                    IMessage lastMessage = chat.lastMessage;
//                    if (lastMessage != null && !lastMessage.senderId.equals(FirebaseUtil.getCurrentUser().getId())){
//                        notifyNewMessage(chat);
//                    }
//                } else {
//                    DialogsRepository.removeEmptyChatId(chatId);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                if (BuildConfig.DEBUG){
//                    Log.e(TAG,"Failed to load chat " + chatId);
//                }
//            }
//        };
//        chatRef.addValueEventListener(listener);
//        chatListeners.put(chatId,listener);
//    }
//
//    private void stopObserve(String chatId) {
//        if (chatId != null) {
//            if (chatListeners.containsKey(chatId)) {
//                ValueEventListener listener = chatListeners.remove(chatId);
//                if (chatRefs.containsKey(chatId)) {
//                    DatabaseReference chatRef = chatRefs.get(chatId);
//                    if (chatRef != null && listener != null) {
//                        chatRef.removeEventListener(listener);
//                    }
//                }
//            }
//        }
//    }
//
//    private void stopListening() {
//
//        if (userChatsRefAndListener != null) {
//
//            userChatsRefAndListener.first.removeEventListener(userChatsRefAndListener.second);
//            userChatsRefAndListener = null;
//
//            for (String chatId : chatRefs.keySet()) {
//                stopObserve(chatId);
//            }
//        } else {
//            if (BuildConfig.DEBUG) {
//                Log.w(TAG, "[!] Tried to remove non-exist listener");
//            }
//        }
//    }
//
//    @SuppressLint("CheckResult")
//    private void notifyNewMessage( Chat chat){
//
//        NotificationUtil notificationUtil = NotificationUtil.with(NewMessageService.this)
//                .setTitle(chat.lastMessage.getSenderName())
//                .setText(chat.lastMessage.getText())
//                .setAutoCancel(true)
//                .setIntent(ChatActivity.getIntent(NewMessageService.this, chat));
//
//        if (chat.imageUri != null && !chat.imageUri.isEmpty()) {
//            Glide.with(NewMessageService.this).asBitmap().load(chat.imageUri).circleCrop().addListener(new RequestListener<Bitmap>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                    if (BuildConfig.DEBUG) {
//                        Log.w(TAG, "[!] Failed to load chat bitmap for notification. Notified without image");
//                        Log.e(TAG, e.getMessage());
//                    }
//                    notificationUtil.execute(0);
//                    return true;
//                }
//
//                @Override
//                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                    notificationUtil.setIcon(resource).execute(0);
//                    return true;
//                }
//            }).submit();
//        } else {
//            Glide.with(NewMessageService.this).asBitmap().load(R.drawable.logo256).circleCrop().addListener(new RequestListener<Bitmap>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                    notificationUtil.execute(0);
//                    return true;
//                }
//
//                @Override
//                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                    notificationUtil.setIcon(resource).execute(0);
//                    return true;
//                }
//            });
//        }
//        notificationUtil.execute(counter++);
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//
//        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 1000,
//                restartServicePendingIntent);
//
//        super.onTaskRemoved(rootIntent);
//    }
//}
