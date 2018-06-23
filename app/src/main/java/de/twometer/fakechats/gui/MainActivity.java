package de.twometer.fakechats.gui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
            ImageView v = customView.findViewById(R.id.contactImage);
            v.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_contact)));
            actionBar.setCustomView(customView);
            Toolbar parent =(Toolbar) customView.getParent();
            parent.setPadding(0,0,0,0);//for tab otherwise give space in tab
            parent.setContentInsetsAbsolute(0,0);

        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

}
