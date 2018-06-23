package de.twometer.fakechats.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import de.twometer.fakechats.R;
import de.twometer.fakechats.model.ContactData;
import de.twometer.fakechats.util.DialogCallback;

public class ContactDataDialog {

    private Context context;
    private ContactData contactData;

    ContactDataDialog(Context context) {
        this.context = context;
    }

    public void show(final DialogCallback<ContactData> callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View customView = View.inflate(context, R.layout.dialog_contact_data, null);
        final EditText contactName = customView.findViewById(R.id.contact_name);
        final EditText contactState = customView.findViewById(R.id.contact_state);
        contactName.setText(contactData.getName());
        contactState.setText(contactData.getLastSeenState());
        builder.setView(customView);
        builder.setTitle(R.string.contact_data_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contactData = new ContactData(contactName.getText().toString(), contactState.getText().toString());
                callback.onResult(contactData);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    public ContactData getContactData() {
        return contactData;
    }

    public void setContactData(ContactData contactData) {
        this.contactData = contactData;
    }
}
