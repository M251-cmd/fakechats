package de.twometer.fakechats.list;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolderSelf {

    private LinearLayout marginHolder;
    private FrameLayout backgroundHolder;
    private ImageView statusView;
    private TextView textView;
    private TextView timeView;

    public ViewHolderSelf(LinearLayout marginHolder, FrameLayout backgroundHolder, ImageView statusView, TextView textView, TextView timeView) {
        this.marginHolder = marginHolder;
        this.backgroundHolder = backgroundHolder;
        this.statusView = statusView;
        this.textView = textView;
        this.timeView = timeView;
    }

    public FrameLayout getBackgroundHolder() {
        return backgroundHolder;
    }

    public ImageView getStatusView() {
        return statusView;
    }

    public TextView getTextView() {
        return textView;
    }

    public TextView getTimeView() {
        return timeView;
    }

    public LinearLayout getMarginHolder() {
        return marginHolder;
    }
}
