package de.twometer.fakechats.gui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.twometer.fakechats.R;
import de.twometer.fakechats.list.ChatListAdapter;
import de.twometer.fakechats.model.ChatMessage;
import de.twometer.fakechats.model.ContactData;
import de.twometer.fakechats.model.MessageSender;
import de.twometer.fakechats.model.MessageState;
import de.twometer.fakechats.util.DialogCallback;
import de.twometer.fakechats.util.TextWatcherAdapter;
import de.twometer.fakechats.util.Utils;


public class ChatActivity extends AppCompatActivity {

    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    private Handler handler;
    private ChatListAdapter chatListAdapter;

    private ContactData currentContactData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        handler = new Handler(getMainLooper());

        chatMessages.add(new ChatMessage(getString(R.string.today), MessageSender.SYSTEM_DATE));

        ListView chatListView = findViewById(R.id.chat_list_view);
        chatListAdapter = new ChatListAdapter(this, chatMessages);
        chatListView.setAdapter(chatListAdapter);

        final EditText chatInputView = findViewById(R.id.chat_input_view);
        final ImageView chatButtonSend = findViewById(R.id.chat_button_send);
        chatButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatInputView.getText().toString(), MessageSender.SELF);
                chatInputView.setText("");
            }
        });

        chatInputView.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chatInputView.getText().toString().trim().length() > 0) {
                    chatButtonSend.setImageResource(R.drawable.input_send);
                } else {
                    chatButtonSend.setImageResource(R.drawable.input_mic_white);
                }
            }
        });

        initActionBar();
    }

    private void sendMessage(final String content, final MessageSender sender) {
        if (content.trim().length() == 0)
            return;

        final ChatMessage message = new ChatMessage(content, sender, MessageState.SENT, System.currentTimeMillis());
        chatMessages.add(message);

        if (chatListAdapter != null)
            chatListAdapter.notifyDataSetChanged();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                message.setMessageState(MessageState.DELIVERED);
                chatMessages.add(new ChatMessage(content, MessageSender.OTHER, MessageState.SENT, System.currentTimeMillis()));
                chatListAdapter.notifyDataSetChanged();
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                message.setMessageState(MessageState.SEEN);
                chatListAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View customView = View.inflate(actionBar.getThemedContext(), R.layout.chat_action_bar, null);

            ImageView contactImage = customView.findViewById(R.id.contactImage);
            contactImage.setImageBitmap(Utils.cropToCircle(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_contact)));

            LinearLayout backButton = customView.findViewById(R.id.backButtonClickable);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            final TextView contactName = customView.findViewById(R.id.actionbarTitle);
            final TextView contactState = customView.findViewById(R.id.actionbarSubtitle);

            currentContactData = new ContactData(contactName.getText().toString(), contactState.getText().toString());

            LinearLayout contactDataButton = customView.findViewById(R.id.contactInfoClickable);
            contactDataButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactDataDialog dialog = new ContactDataDialog(ChatActivity.this);
                    dialog.setContactData(currentContactData);
                    dialog.show(new DialogCallback<ContactData>() {
                        @Override
                        public void onResult(ContactData result) {
                            currentContactData = result;
                            contactName.setText(currentContactData.getName());
                            contactState.setText(currentContactData.getLastSeenState());
                            if(chatMessages.get(0).getSender() == MessageSender.SYSTEM_UNKNOWN_NUMBER){
                                chatMessages.remove(0);
                                chatListAdapter.notifyDataSetChanged();
                            }
                            if(Utils.isPhoneNumber(currentContactData.getName())){

                                chatMessages.add(0, new ChatMessage("", MessageSender.SYSTEM_UNKNOWN_NUMBER));
                                chatListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            actionBar.setCustomView(customView);

            Toolbar parent = (Toolbar) customView.getParent();
            parent.setPadding(0, 0, 0, 0);
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

}
