package de.twometer.fakechats.list;

import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolderSelf {

    private ImageView statusView;
    private TextView textView;
    private TextView timeView;

    public ViewHolderSelf(ImageView statusView, TextView textView, TextView timeView) {
        this.statusView = statusView;
        this.textView = textView;
        this.timeView = timeView;
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
}
