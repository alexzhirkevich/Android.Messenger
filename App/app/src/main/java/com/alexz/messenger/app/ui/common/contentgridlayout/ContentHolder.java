package com.alexz.messenger.app.ui.common.contentgridlayout;

public interface ContentHolder {

    void setContentClickListener(ContentClickListener listener);

    ContentClickListener getContentClickListener();
}
