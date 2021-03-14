package com.alexz.messenger.app.data.repo;

import androidx.annotation.Nullable;

import com.alexz.messenger.app.data.model.imp.Message;
import com.alexz.messenger.app.util.FirebaseUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class MessagesRepository {

    public static DatabaseReference getMessagesReference(String chatId) {
        return  FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtil.CHATS)
                .child(chatId)
                .child(FirebaseUtil.MESSAGES);
    }

    public static Task<DataSnapshot> getChatInfo(String chatId){
        return FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtil.CHATS)
                .child(chatId)
                .child(FirebaseUtil.INFO)
                .get();
    }

    public static void sendMessage(Message m, @Nullable String replaceText){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtil.CHATS)
                .child(m.getChatId())
                .child(FirebaseUtil.MESSAGES).push();
        m.setId(ref.getKey());
        m.setSenderId(FirebaseUtil.getCurrentUser().getId());
        ref.setValue(m);
        if (m.getText() == null || m.getText().isEmpty()){
            m.setText(replaceText);
        }
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtil.CHATS)
                .child(m.getChatId())
                .child(FirebaseUtil.INFO)
                .child(FirebaseUtil.LASTMESSAGE)
                .setValue(m);
    }

    public static void deleteMessage(Message item) {

        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtil.CHATS)
                .child(item.getChatId())
                .child(FirebaseUtil.MESSAGES)
                .child(item.getId())
                .removeValue();
    }
}
