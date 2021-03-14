package com.alexz.messenger.app.ui.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexz.messenger.app.data.model.Result;
import com.alexz.messenger.app.data.model.imp.Chat;
import com.alexz.messenger.app.data.model.imp.Message;
import com.alexz.messenger.app.ui.common.AvatarImageView;
import com.alexz.messenger.app.ui.common.ItemClickListener;
import com.alexz.messenger.app.ui.messages.MessageFirebaseRecyclerAdapter;
import com.alexz.messenger.app.ui.messages.MessageInput;
import com.alexz.messenger.app.ui.viewmodels.ChatActivityViewModel;
import com.alexz.messenger.app.util.FirebaseUtil;
import com.google.firebase.storage.StorageReference;
import com.messenger.app.R;

public class ChatActivity extends BaseActivity
    implements MessageInput.OnSendListener,
        MessageInput.OnAttachListener,
        PopupMenu.OnMenuItemClickListener{

    private static final String EXTRA_CHAT = "EXTRA_CHAT";
    private static final int PERM_STORAGE = 2001;
    private static final int SELECT_PHOTO = 1001;
    private static final String STR_RECYCLER_DATA = "rec_data";
    private static final String STR_UPLOADED_IMAGE = "msg_image";

    private Chat chat;
    private String uploadedImage;

    private RecyclerView mRecyclerView;
    private MessageFirebaseRecyclerAdapter adapter;;
    private ChatActivityViewModel viewModel;
    private MessageInput messageInput;

    public static void startActivity(Context context, Chat chat) {
        context.startActivity(getIntent(context,chat));
    }

    public static Intent getIntent(Context context, Chat chat){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CHAT,chat);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat = getIntent().getParcelableExtra(EXTRA_CHAT);
        viewModel = new ViewModelProvider(this).get(ChatActivityViewModel.class);
        viewModel.setChatId(chat.getId());
        viewModel.getChatInfoChangedLiveData().observe(this,
                chat -> setupChatInfo(chat.getId(),chat.getName(),chat.getImageUri()));
        setupChatInfo(chat.getId(),chat.getName(),chat.getImageUri());

        setupRecyclerView();
        setupInput();
        setupToolbar();
        adapter.startListening();
        viewModel.startListening();
        mRecyclerView.postDelayed(
                () -> mRecyclerView.smoothScrollToPosition(mRecyclerView.getBottom()),200);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
        viewModel.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            if (item.getItemId() == R.id.menu_chat_invite){
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (chat.getId() != null) {
                    ClipData cd = ClipData.newPlainText("Chat id", chat.getId());
                    cm.setPrimaryClip(cd);
                    Toast.makeText(ChatActivity.this, getString(R.string.action_chat_id_copied), Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else if (item.getItemId() == R.id.menu_chat_users){
                UserListActivity.startActivity(this,chat.getId());
            }
        }
        return true;
    }

    @Override
    public void onSendClicked(ImageButton btn, TextView input) {
        if (input.getText().toString().trim().length() == 0 && uploadedImage == null) {
            return;
        }
        Message m = new Message(chat.getId());
        m.setText(input.getText().toString().trim());
        m.setPrivate(!chat.isGroup());
        m.setImageUrl(uploadedImage);
        viewModel.sendMessage(m,getString(R.string.title_file));
        input.setText("");
        mRecyclerView.postDelayed(() -> mRecyclerView.smoothScrollToPosition(mRecyclerView.getBottom()), 100);
        if (uploadedImage != null){
            uploadedImage = null;
            restoreAttachButton();
        }
    }

    @Override
    public void onAttachClicked(ImageButton btn, TextView input) {

//        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERM_STORAGE);
//        } else {
            if (uploadedImage == null) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            } else {
                uploadedImage = null;
                restoreAttachButton();
            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    onStartImageLoad();
                    FirebaseUtil.uploadPhoto(imageReturnedIntent.getData())
                            .addResultListener(new Result.ResultListener<Pair<Uri, StorageReference>>() {
                                @Override
                                public void onSuccess(@NonNull Result.ISuccess<Pair<Uri, StorageReference>> result) {
                                    uploadedImage = result.get().first.toString();
                                    onImageAttached(imageReturnedIntent.getData(),true);
                                }

                                @Override
                                public void onError(@Nullable Result.IError error) {
                                    Toast.makeText(ChatActivity.this,error.getError(),Toast.LENGTH_SHORT).show();
                                    onImageAttached(null,false);
                                }

                                @Override
                                public void onProgress(@Nullable Double percent) {

                                }
                            });
                }
                break;
            case PERM_STORAGE:
                if (resultCode == RESULT_OK){
                    onAttachClicked(messageInput.getAttachButton(),messageInput.getInputTextView());
                }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STR_UPLOADED_IMAGE,uploadedImage);
        if (mRecyclerView != null){
            RecyclerView.LayoutManager recManager = mRecyclerView.getLayoutManager();
            if (recManager !=null){
                outState.putParcelable(STR_RECYCLER_DATA,recManager.onSaveInstanceState());
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupRecyclerView();
        uploadedImage = savedInstanceState.getString(STR_UPLOADED_IMAGE);
        if (mRecyclerView!=null){
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if  (layoutManager!=null){
                layoutManager.onRestoreInstanceState(savedInstanceState);
            }
        }
    }

    private ItemClickListener<Message> newItemClickListener() {
        return new ItemClickListener<Message>() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onLongItemClick(View view, Message message) {
                PopupMenu pm = new PopupMenu(ChatActivity.this, view);
                pm.setGravity(Gravity.RIGHT);
                if (message.getSenderId().equals(FirebaseUtil.getCurrentUser().getId())) {
                    pm.inflate(R.menu.menu_message_out);
                } else {
                    pm.inflate(R.menu.menu_message_in);
                }
                pm.setOnMenuItemClickListener(e -> {
                    switch (e.getItemId()) {

                        case R.id.message_delete:
                            viewModel.deleteMessage(message);
                            return true;
                        case R.id.message_copy:
                            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            String text = message.getText();
                            ClipData cd = ClipData.newPlainText("Msg from " + message.getSenderId(), text);
                            cm.setPrimaryClip(cd);
                            Toast.makeText(ChatActivity.this, getString(R.string.action_message_copied), Toast.LENGTH_SHORT).show();
                            return true;
                    }
                    return false;
                });
                pm.show();
                return true;
            }
        };
    }

    private void restoreAttachButton(){
        ImageButton attach = messageInput.getAttachButton();
        attach.setColorFilter(ContextCompat.getColor(this, R.color.color_primary));
        attach.setImageResource(R.drawable.ic_attach);
        attach.setEnabled(true);
    }

    private void onStartImageLoad(){
        messageInput.getProgressBar().setVisibility(View.VISIBLE);
        messageInput.getAttachButton().setEnabled(false);
    }

    private void onImageAttached(@Nullable Uri image, boolean success){
        messageInput.getProgressBar().setVisibility(View.GONE);
        final ImageButton attach = messageInput.getAttachButton();
        if (success) {
            attach.setImageURI(image);
            attach.setColorFilter(null);
            attach.setEnabled(true);
        } else{
            restoreAttachButton();
            uploadedImage = null;
        }
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.message_recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayout);
        adapter = viewModel.getAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(newItemClickListener());
        adapter.setImageClickListener(new ItemClickListener<Message>() {
            @Override
            public void onItemClick(View view, Message data) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        ChatActivity.this,view,getString(R.string.util_transition_image_fullscreen));
                FullscreenImageActivity.startActivity(ChatActivity.this, data.getImageUrl(),options.toBundle());
            }

            @Override
            public boolean onLongItemClick(View view, Message data) {
                return adapter.getOnItemClickListener().onLongItemClick(view,data);
            }
        });
    }

    private void setupInput() {
        messageInput = findViewById(R.id.message_input);
        messageInput.setOnSendListener(this);
        messageInput.setOnAttachListener(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupChatInfo(String chatId,String chatName, String chatPhoto) {
        final AvatarImageView avatarImageView = findViewById(R.id.chat_avatar);
        final TextView tvChatName = findViewById(R.id.chat_name);

        boolean loadInfo = false;
        if (tvChatName != null && chatName != null && !chatName.isEmpty()){
            tvChatName.setText(chatName);
        } else {
            loadInfo = true;
        }
        if (avatarImageView != null && chatPhoto != null && !chatPhoto.isEmpty()){
            avatarImageView.setImageURI(Uri.parse(chatPhoto));
        } else {
            loadInfo =true;
        }
        if (loadInfo) {
            FirebaseUtil.getChatInfo(chatId).addResultListener(new Result.ResultListener<Chat>() {
                @Override
                public void onSuccess(@NonNull Result.ISuccess<Chat> result) {
                    Chat chat = result.get();
                    if (chat != null) {
                        final String avatarUrl = chat.getImageUri();
                        final String chatName = chat.getName();


                        if (tvChatName != null && chatName != null) {
                            tvChatName.setText(chatName);
                        }
                        if (avatarImageView != null && avatarUrl != null) {
                            avatarImageView.setImageURI(Uri.parse(avatarUrl));
                        }
                    }
                }

                @Override
                public void onError(@Nullable Result.IError error) {
                    if (error != null) {
                        Toast.makeText(ChatActivity.this, error.getError(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, R.string.error_chat_load, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            });
        }
    }
}