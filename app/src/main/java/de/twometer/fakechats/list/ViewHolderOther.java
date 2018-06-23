package de.twometer.fakechats.list;

import android.widget.TextView;

public class ViewHolderOther {

    private TextView textView;
    private TextView timeView;

    public ViewHolderOther(TextView textView, TextView timeView) {
        this.textView = textView;
        this.timeView = timeView;
    }

    public TextView getTextView() {
        return textView;
    }

    public TextView getTimeView() {
        return timeView;
    }
}
