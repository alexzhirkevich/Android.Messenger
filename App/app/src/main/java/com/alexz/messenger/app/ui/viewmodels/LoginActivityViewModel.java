package com.alexz.messenger.app.ui.viewmodels;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alexz.messenger.app.data.model.Result;
import com.alexz.messenger.app.data.repo.AuthRepository;
import com.alexz.messenger.app.util.FirebaseUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.messenger.app.R;

import java.util.Observable;


public class LoginActivityViewModel extends ViewModel {

    Observable o = new Observable();
    private final MutableLiveData<Result<FirebaseUser>> loginResult = new MutableLiveData<>();

    public LiveData<Result<FirebaseUser>> getLoginResult() {
        return loginResult;
    }

    public void login(GoogleSignInAccount account){

        AuthRepository.googleLogin(account).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loginResult.postValue(new Result.Success<>(FirebaseUtil.getCurrentFireUser()));
            }
            else {
                loginResult.postValue(new Result.Error(R.string.error_google_login));
            }
        });
    }


    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseUtil.getCurrentFireUser();
    }
}
