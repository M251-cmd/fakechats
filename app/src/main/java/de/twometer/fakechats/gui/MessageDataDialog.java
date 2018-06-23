package de.twometer.fakechats.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.RadioButton;

import de.twometer.fakechats.R;
import de.twometer.fakechats.model.ContactData;
import de.twometer.fakechats.model.MessageSender;
import de.twometer.fakechats.model.MessageState;
import de.twometer.fakechats.util.DialogCallback;

public class MessageDataDialog {

    private Context context;
    private MessageState messageState;

    MessageDataDialog(Context context) {
        this.context = context;
    }

    public void show(final DialogCallback<MessageState> callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View customView = View.inflate(context, R.layout.dialog_message_data, null);
        final RadioButton stateSeen = customView.findViewById(R.id.state_seen);
        final RadioButton stateSent = customView.findViewById(R.id.state_sent);
        final RadioButton stateDelivered = customView.findViewById(R.id.state_delivered);

        stateSeen.setChecked(messageState == MessageState.SEEN);
        stateSent.setChecked(messageState == MessageState.SENT);
        stateDelivered.setChecked(messageState == MessageState.DELIVERED);

        builder.setView(customView);
        builder.setTitle(R.string.message_data_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (stateSeen.isChecked()) callback.onResult(MessageState.SEEN);
                else if (stateSent.isChecked()) callback.onResult(MessageState.SENT);
                else if (stateDelivered.isChecked()) callback.onResult(MessageState.DELIVERED);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }
}
