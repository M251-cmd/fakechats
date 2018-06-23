package de.twometer.fakechats.list;

import android.widget.TextView;

public class ViewHolderSystem {

    private TextView textView;

    public ViewHolderSystem(TextView textView) {
        this.textView = textView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
