package de.twometer.fakechats.gui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import de.twometer.fakechats.R;
import de.twometer.fakechats.list.ChatListAdapter;
import de.twometer.fakechats.model.ChatMessage;
import de.twometer.fakechats.model.MessageSender;
import de.twometer.fakechats.model.MessageState;


public class MainActivity extends AppCompatActivity {

    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    private Handler handler;
    private ChatListAdapter chatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));

        handler = new Handler(getMainLooper());

        ListView chatListView = findViewById(R.id.chat_list_view);
        chatListAdapter = new ChatListAdapter(this, chatMessages);
        chatListView.setAdapter(chatListAdapter);

        final EditText chatInputView = findViewById(R.id.chat_input_view);
        ImageView chatButtonSend = findViewById(R.id.chat_button_send);
        chatButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatInputView.getText().toString(), MessageSender.SELF);
                chatInputView.setText("");
            }
        });
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


}
