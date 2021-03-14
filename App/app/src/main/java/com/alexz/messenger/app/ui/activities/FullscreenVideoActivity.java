package com.alexz.messenger.app.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.messenger.app.R;

public class FullscreenVideoActivity extends AppCompatActivity {

    private static final String EXTRA_URI = "EXTRA_URI";
    private static final String EXTRA_POS = "EXTRA_POS";
    private static final String EXTRA_WINDOW = "EXTRA_WINDOW";
    private static final String EXTRA_AUTOPLAY = "EXTRA_AUTOPLAY";
    private SimpleExoPlayer player;
    private boolean autoplay = true;

    public static void startActivity(Context context, String videoUri,Bundle bundle){
        Intent intent = new Intent(context,FullscreenVideoActivity.class);
        intent.putExtra(EXTRA_URI,videoUri);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video);

        final String uri = getIntent().getStringExtra(EXTRA_URI);
        final PlayerView playerView= findViewById(R.id.player_fullscreen);

        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(uri));
        player.setMediaItem(mediaItem);
        player.prepare();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_POS,player.getContentPosition());
        outState.putInt(EXTRA_WINDOW,player.getCurrentWindowIndex());
        outState.putBoolean(EXTRA_AUTOPLAY,player.getPlayWhenReady());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        player.seekTo(savedInstanceState.getInt(EXTRA_WINDOW),
                savedInstanceState.getLong(EXTRA_POS,0));
        autoplay = savedInstanceState.getBoolean(EXTRA_AUTOPLAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(autoplay);
    }
}