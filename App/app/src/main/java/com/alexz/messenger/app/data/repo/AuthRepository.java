package com.alexz.messenger.app.data.repo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alexz.messenger.app.data.model.imp.User;
import com.alexz.messenger.app.util.FirebaseUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.messenger.app.BuildConfig;

public abstract class AuthRepository {

    private static final String TAG = AuthRepository.class.getCanonicalName();
    private static volatile AuthRepository instance;

    public static Task<AuthResult> googleLogin(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        return FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                addUserToDatabase(task.getResult().getUser());
            }
        });
    }
    private static void addUserToDatabase(FirebaseUser user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtil.USERS)
                .child(user.getUid())
                .child(FirebaseUtil.INFO)
                .child(FirebaseUtil.ID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    User u = new User(user.getUid(), user.getPhotoUrl().toString(), user.getDisplayName());
                    u.setOnline(true);
                    ref.getParent().setValue(u);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "Failed to load user");
                }
            }
        });
    }
}
