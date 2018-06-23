package de.twometer.fakechats.list;

import android.widget.FrameLayout;
import android.widget.TextView;

public class ViewHolderOther {

    private FrameLayout backgroundHolder;
    private TextView textView;
    private TextView timeView;

    public ViewHolderOther(FrameLayout backgroundHolder, TextView textView, TextView timeView) {
        this.backgroundHolder = backgroundHolder;
        this.textView = textView;
        this.timeView = timeView;
    }

    public FrameLayout getBackgroundHolder() {
        return backgroundHolder;
    }

    public TextView getTextView() {
        return textView;
    }

    public TextView getTimeView() {
        return timeView;
    }
}
