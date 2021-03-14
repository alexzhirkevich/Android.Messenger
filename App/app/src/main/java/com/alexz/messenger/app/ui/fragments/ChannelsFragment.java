package com.alexz.messenger.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alexz.messenger.app.data.model.imp.MediaContent;
import com.alexz.messenger.app.data.model.interfaces.IMediaContent;
import com.alexz.messenger.app.ui.common.contentgridlayout.ContentClickListener;
import com.alexz.messenger.app.ui.common.contentgridlayout.ContentGridLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.messenger.app.R;

public class ChannelsFragment extends Fragment {

    private FloatingActionButton floatingActionButton;
    private ContentGridLayout contentGridLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channels,container,false);
        contentGridLayout = view.findViewById(R.id.post_grid_content);
        contentGridLayout.addContent(new MediaContent(IMediaContent.VIDEO,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"));
        contentGridLayout.addContent(new MediaContent(IMediaContent.IMAGE,
                "https://firebasestorage.googleapis.com/v0/b/messenger-302121.appspot.com/o/FetFyNi4VXPurgFEkN24risaFQv1%2F1614521613228.jpg?alt=media&token=ae192601-de34-466b-b374-bc2ef3521289"
        ));
        contentGridLayout.reGroup();

        Activity activity = getActivity();
        if (activity!=null){
            contentGridLayout.setFullscreenTransition(getActivity());
            floatingActionButton = activity.findViewById(R.id.fab_dialogs);
        }

        return view;
    }

    @Override
    public void onResume() {
        floatingActionButton.setOnClickListener(e -> {
            contentGridLayout.addContent(new MediaContent(IMediaContent.IMAGE,
                    "https://firebasestorage.googleapis.com/v0/b/messenger-302121.appspot.com/o/FetFyNi4VXPurgFEkN24risaFQv1%2F1614521613228.jpg?alt=media&token=ae192601-de34-466b-b374-bc2ef3521289"
            ));
            contentGridLayout.reGroup();
        });
        super.onResume();
    }
}
