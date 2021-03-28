package com.alexz.messenger.app.util;

import android.net.Uri;
import android.util.Pair;
import android.webkit.MimeTypeMap;

import com.alexz.messenger.app.data.model.imp.Chat;
import com.alexz.messenger.app.data.model.result.Error;
import com.alexz.messenger.app.data.model.result.Future;
import com.alexz.messenger.app.data.model.result.MutableFuture;
import com.alexz.messenger.app.data.model.result.Result;
import com.alexz.messenger.app.data.model.imp.User;
import com.alexz.messenger.app.data.model.result.Success;
import com.alexz.messenger.app.data.repo.MessagesRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.messenger.app.R;

import java.util.Date;

public class FirebaseUtil {

    public static final String ID = "id";
    public static final String USERS = "users";
    public static final String CHATS = "chats";
    public static final String INFO = "info";
    public static final String MESSAGES = "messages";
    public static final String LASTMESSAGE = "lastMessage";
    public static final String ONLINE = "online";
    public static final String LASTONLINE = "lastOnline";

    public static FirebaseUser getCurrentFireUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static User getCurrentUser(){
        FirebaseUser user = getCurrentFireUser();
        User cUser;
        if (user != null) {
            cUser = new User(user.getDisplayName(), user.getPhotoUrl().toString(), System.currentTimeMillis(), true);
            cUser.setId(user.getUid());
        }
        else
            cUser = new User();
        return  cUser;
    }


    public static void setOnline(boolean online) {
        if (FirebaseAuth.getInstance().getUid() != null) {
            DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseUtil.USERS)
                    .child(FirebaseUtil.getCurrentUser().getId())
                    .child(FirebaseUtil.INFO);

            userInfoRef.child(FirebaseUtil.ONLINE).setValue(online);
            if (!online) {
                userInfoRef.child(FirebaseUtil.LASTONLINE).setValue(new Date().getTime());
            }
        }
    }

    public static Future<Chat> getChatInfo(String chatId){
        MutableFuture<Chat> future = new MutableFuture<>();
        MessagesRepository.getChatInfo(chatId)
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        future.post(new Success<>(chat));
                    } else {
                        future.post(new Error<>(R.string.error_chat_not_found));
                    }
                })
                .addOnFailureListener(e -> {
                    future.post(new Error(R.string.error_chat_load));
                });
        return future;
    }

    public static Future<Pair<Uri,StorageReference>> uploadPhoto(Uri path){

        String ext = MimeTypeMap.getFileExtensionFromUrl(path.toString());

        MutableFuture<Pair<Uri,StorageReference>> res = new MutableFuture<>();

        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child(FirebaseUtil.getCurrentUser().getId())
                .child(System.currentTimeMillis() + "." + ext);
        ref.putFile(path)
                .addOnSuccessListener(taskSnapshot ->
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(task -> res.post(new Success<>(new Pair<>(task,ref))))
                                .addOnFailureListener(e -> res.post(new Error(R.string.error_upload_file))))
                .addOnFailureListener(e -> res.post(new Error(R.string.error_upload_file)))
                .addOnProgressListener(snapshot -> res.setProgress(100.0 * ((UploadTask.TaskSnapshot)snapshot).getBytesTransferred() / ((UploadTask.TaskSnapshot)snapshot).getTotalByteCount()));

        return res;
    }
}
