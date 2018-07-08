package de.twometer.fakechats.gui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
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
import de.twometer.fakechats.save.SaveState;
import de.twometer.fakechats.util.DialogCallback;
import de.twometer.fakechats.util.TextWatcherAdapter;
import de.twometer.fakechats.util.Utils;


public class ChatActivity extends AppCompatActivity {

    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    private ChatListAdapter chatListAdapter;

    private ContactData currentContactData;
    private MessageSender currentSender = MessageSender.SELF;

    private TextView contactName;
    private TextView contactState;

    private SaveState.Chat chat;

    public static final String EXTRA_IDENTIFIER = "fakechats.extra.id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        int id = getIntent().getIntExtra(EXTRA_IDENTIFIER, -1);
        if (id == -1) {
            finish();
            return;
        }

        chatMessages.add(new ChatMessage(getString(R.string.today), MessageSender.SYSTEM_DATE));

        ListView chatListView = findViewById(R.id.chat_list_view);
        chatListAdapter = new ChatListAdapter(this, chatMessages);
        chatListView.setAdapter(chatListAdapter);

        final EditText chatInputView = findViewById(R.id.chat_input_view);
        final ImageView chatButtonSend = findViewById(R.id.chat_button_send);
        chatButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatInputView.getText().toString(), currentSender);
                chatInputView.setText("");
            }
        });

        final ImageView sideSwitchButton = findViewById(R.id.sideSwitchButton);
        sideSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSender = currentSender.invert();
                if (currentSender == MessageSender.SELF)
                    sideSwitchButton.setImageResource(R.drawable.switcher_self);
                else if (currentSender == MessageSender.OTHER)
                    sideSwitchButton.setImageResource(R.drawable.switcher_other);
            }
        });

        final ImageView attachButton = findViewById(R.id.attachButton);
        final ImageView photoButton = findViewById(R.id.camButton);

        chatInputView.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chatInputView.getText().toString().trim().length() > 0) {
                    chatButtonSend.setImageResource(R.drawable.input_send);
                    attachButton.setVisibility(View.GONE);
                    photoButton.setVisibility(View.GONE);
                    sideSwitchButton.setVisibility(View.VISIBLE);
                } else {
                    chatButtonSend.setImageResource(R.drawable.input_mic_white);
                    attachButton.setVisibility(View.VISIBLE);
                    photoButton.setVisibility(View.VISIBLE);
                    sideSwitchButton.setVisibility(View.GONE);
                }
            }
        });

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ChatMessage message = chatMessages.get(position);
                MessageDataDialog dialog = new MessageDataDialog(ChatActivity.this);
                dialog.setMessageState(message.getMessageState());
                dialog.show(new DialogCallback<MessageState>() {
                    @Override
                    public void onResult(MessageState result) {
                        message.setMessageState(result);
                        chatListAdapter.notifyDataSetChanged();
                        SaveState.save();
                    }
                });
            }
        });

        initActionBar();

        //// Loading data ////
        System.out.println("Loading chat " + id);
        chat = SaveState.getChat(id);
        setCurrentContactData(chat.data);

        for (SaveState.Message msg : chat.messages) chatMessages.add(msg.chatMessage);

        if (chatListAdapter != null)
            chatListAdapter.notifyDataSetChanged();

    }

    private void sendMessage(final String content, final MessageSender sender) {
        if (content.trim().length() == 0)
            return;

        final ChatMessage message = new ChatMessage(content, sender, MessageState.SEEN, System.currentTimeMillis());
        chatMessages.add(message);
        SaveState.createMessage(chat.id, message);

        if (chatListAdapter != null)
            chatListAdapter.notifyDataSetChanged();
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

            contactName = customView.findViewById(R.id.actionbarTitle);
            contactState = customView.findViewById(R.id.actionbarSubtitle);

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
                            setCurrentContactData(result);
                            chat.data = result;
                            SaveState.save();
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

    public void setCurrentContactData(ContactData currentContactData) {
        this.currentContactData = currentContactData;
        contactName.setText(currentContactData.getName());
        contactState.setText(currentContactData.getLastSeenState());
        if (chatMessages.get(0).getSender() == MessageSender.SYSTEM_UNKNOWN_NUMBER) {
            chatMessages.remove(0);
            chatListAdapter.notifyDataSetChanged();
        }
        if (Utils.isPhoneNumber(currentContactData.getName())) {
            chatMessages.add(0, new ChatMessage("", MessageSender.SYSTEM_UNKNOWN_NUMBER));
            chatListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

}
