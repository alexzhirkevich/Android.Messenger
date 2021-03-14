package com.alexz.messenger.app.ui.common.contentgridlayout;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityOptionsCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.LifecycleObserver;

import com.alexz.messenger.app.data.model.imp.MediaContent;
import com.alexz.messenger.app.data.model.interfaces.IMediaContent;
import com.alexz.messenger.app.ui.activities.FullscreenImageActivity;
import com.alexz.messenger.app.ui.activities.FullscreenVideoActivity;
import com.alexz.messenger.app.util.MetrixUtil;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.messenger.app.BuildConfig;
import com.messenger.app.R;

import java.util.ArrayList;

public class ContentGridLayout extends GridLayout
        implements ContentHolder {

    private static final String TAG = ContentGridLayout.class.getSimpleName();

    private final ArrayList<IMediaContent> contents = new ArrayList<>();
    private ContentClickListener listener;
    private int paddings;

    public ContentGridLayout(Context context) {
        super(context);
          init(context);
    }

    public ContentGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ContentGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void addContent(IMediaContent content){
        if (contents.size()>=10) {
            contents.remove(0);
        }
        contents.add(content);
    }

    public void reGroup(){
        removeAllViews();
        switch (contents.size()) {
            case 1: {
                setRowCount(1);
                setColumnCount(1);
                View view = createViewForContent(contents.get(0));
                LayoutParams params = new LayoutParams();
                params.width = 0;
                params.height = 0;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                addView(view, params);
                requestLayout();
                break;
            }
            case 2: {
                setRowCount(1);
                setColumnCount(2);
                for (int i = 0; i < 2; i++) {
                    View view = createViewForContent(contents.get(i));
                    LayoutParams params = (LayoutParams) view.getLayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    view.requestLayout();
                    addView(view);
                }
                break;
            }
            case 3:
            case 4: {
                setColumnCount(2);
                setRowCount(contents.size() - 1);
                View view = createViewForContent(contents.get(0));
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, contents.size() - 1);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, contents.size() - 1, contents.size() - 1);
                view.requestLayout();
                addView(view);
                for (int i = 1; i < contents.size(); i++) {
                    view = createViewForContent(contents.get(i));
                    params = (LayoutParams) view.getLayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    view.requestLayout();
                    addView(view);
                }
                break;
            }
            case 5: {
                setColumnCount(3);
                setRowCount(3);
                View view = createViewForContent(contents.get(0));
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2, 2f);
                view.requestLayout();
                addView(view);
                view = createViewForContent(contents.get(1));
                params = (LayoutParams) view.getLayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2, 2f);
                view.requestLayout();
                addView(view);
                for (int i = 2; i < 5; i++) {
                    view = createViewForContent(contents.get(i));
                    params = (LayoutParams) view.getLayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    view.requestLayout();
                    addView(view);
                }
                break;
            }
            case 6: {
                setColumnCount(3);
                setRowCount(3);
                View view = createViewForContent(contents.get(0));
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2, 2f);
                view.requestLayout();
                addView(view);
                for (int i = 1; i < 6; i++) {
                    view = createViewForContent(contents.get(i));
                    params = (LayoutParams) view.getLayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    view.requestLayout();
                    addView(view);
                }
                break;
            }
            case 7: {
                setColumnCount(3);
                setRowCount(4);
                View view = createViewForContent(contents.get(0));
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 3, 1f);
                params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 3, 3f);
                view.requestLayout();
                addView(view);
                for (int i = 1; i < 7; i++) {
                    view = createViewForContent(contents.get(i));
                    params = (LayoutParams) view.getLayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    view.requestLayout();
                    addView(view);
                }
                break;
            }
            case 8: {
                setColumnCount(2);
                setRowCount(4);
                for (int i = 0; i < 8; i++) {
                    View view = createViewForContent(contents.get(i));
                    LayoutParams params = (LayoutParams) view.getLayoutParams();
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                    view.requestLayout();
                    addView(view);
                }
                break;
            }
            case 9: {
                setColumnCount(6);
                setRowCount(4);
                for (int i = 0; i < 9; i++) {
                    View view = createViewForContent(contents.get(i));
                    LayoutParams params = (LayoutParams) view.getLayoutParams();
                    if (i<6) {
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 3, 3f);
                        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 2f);
                    } else{

                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2, 2f);
                        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1,1f);
                    }
                    view.requestLayout();
                    addView(view);
                }
                break;
            }
            case 10: {
                setColumnCount(6);
                setRowCount(4);
                for (int i = 0; i < 10; i++) {
                    View view = createViewForContent(contents.get(i));
                    LayoutParams params = (LayoutParams) view.getLayoutParams();
                    if (i<4) {
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 3, 3f);
                        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 2f);
                    } else {

                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2, 2f);
                        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1,1f);
                    }
                    view.requestLayout();
                    addView(view);
                }
                break;
            }
            default:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Invalid content count: " + contents.size());
                }
        }
    }

    private void init(Context context){
        setOrientation(HORIZONTAL);
        paddings = MetrixUtil.dpToPx(context,1);
    }

    private View createViewForContent(IMediaContent content) {
        View view;
        if (content.getType() == MediaContent.IMAGE){
            view = new ImageView(getContext());
            view.setTransitionName(getResources().getString(R.string.util_transition_image_fullscreen));
            Glide.with(this).load(content.getUrl()).centerCrop().into((ImageView)view);
            view.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onImageClick((ImageView)view, content);
                }
            });
        } else {
            view = new PlayerView(getContext());
            final PlayerView playerView = (PlayerView)view;
            playerView.setUseArtwork(true);
            playerView.setDefaultArtwork(AppCompatResources.getDrawable(getContext(),R.drawable. logo512));
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            final SimpleExoPlayer player = new SimpleExoPlayer.Builder(getContext()).build();
            playerView.setPlayer(player);
            player.addListener(new Player.EventListener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_IDLE && listener!=null){
                        listener.onVideoClick(playerView,content);
                        player.stop();
                    }
                }
            });
            final MediaItem mediaItem = MediaItem.fromUri(Uri.parse(content.getUrl()));
            player.setMediaItem(mediaItem);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            view.setTransitionName(getContext().getString(R.string.util_transition_video_fullscreen));
            view.setOnClickListener(v -> {
                if (listener!=null){
                    listener.onVideoClick(playerView,content);
                }
            });
        }
        LayoutParams params = new LayoutParams();
        params.width = 0;
        params.height = 0;
        view.setPadding(paddings, paddings, paddings, paddings);
        view.setLayoutParams(params);
        view.setClickable(true);
        view.setFocusable(true);
        view.setLongClickable(true);
        return view;
    }

    public void setFullscreenTransition(Activity transitionActivity) {
        setContentClickListener(new ContentClickListener() {
            @Override
            public void onImageClick(ImageView view, IMediaContent content) {
                if (transitionActivity != null){
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            transitionActivity,view,getContext().getString(R.string.util_transition_image_fullscreen));
                    FullscreenImageActivity.startActivity(transitionActivity, content.getUrl(),options.toBundle());
                }
            }

            @Override
            public void onVideoClick(PlayerView playerView, IMediaContent content) {
                if (transitionActivity != null){
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            transitionActivity,playerView,getContext().getString(R.string.util_transition_video_fullscreen));
                    FullscreenVideoActivity.startActivity(transitionActivity, content.getUrl(),options.toBundle());
                }
            }
        });
    }

    @Override
    public void setContentClickListener(ContentClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ContentClickListener getContentClickListener() {
        return listener;
    }
}