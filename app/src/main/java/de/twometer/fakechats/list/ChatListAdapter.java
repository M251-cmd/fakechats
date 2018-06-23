package de.twometer.fakechats.list;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.twometer.fakechats.R;
import de.twometer.fakechats.model.ChatMessage;
import de.twometer.fakechats.model.MessageSender;
import de.twometer.fakechats.model.MessageState;

public class ChatListAdapter extends BaseAdapter {

    private static final String OTHER_NBSP = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
    private static final String SELF_NBSP = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";

    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    public ChatListAdapter(Context context, ArrayList<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View messageView = null;
        ChatMessage message = chatMessages.get(position);

        if (message.getSender() == MessageSender.OTHER) {
            ViewHolderOther viewHolder;
            if (convertView == null) {
                messageView = LayoutInflater.from(context).inflate(R.layout.chat_item_other, null, false);
                TextView messageTextView = messageView.findViewById(R.id.textview_message);
                TextView timeTextView = messageView.findViewById(R.id.textview_time);
                viewHolder = new ViewHolderOther(messageTextView, timeTextView);
                messageView.setTag(viewHolder);
            } else {
                messageView = convertView;
                viewHolder = (ViewHolderOther) messageView.getTag();
            }

            viewHolder.getTextView().setText(Html.fromHtml(Html.escapeHtml(message.getContent()) + OTHER_NBSP));
            viewHolder.getTimeView().setText(SIMPLE_DATE_FORMAT.format(message.getSentTime()));
        } else if (message.getSender() == MessageSender.SELF) {
            ViewHolderSelf viewHolder;
            if (convertView == null) {
                messageView = LayoutInflater.from(context).inflate(R.layout.chat_item_self, null, false);
                ImageView statusView = messageView.findViewById(R.id.user_reply_status);
                TextView textView = messageView.findViewById(R.id.textview_message);
                TextView timeView = messageView.findViewById(R.id.textview_time);
                viewHolder = new ViewHolderSelf(statusView, textView, timeView);
                messageView.setTag(viewHolder);
            } else {
                messageView = convertView;
                viewHolder = (ViewHolderSelf) messageView.getTag();
            }

            viewHolder.getTextView().setText(Html.fromHtml(message.getContent() + SELF_NBSP));
            viewHolder.getTimeView().setText(SIMPLE_DATE_FORMAT.format(message.getSentTime()));

            if (message.getMessageState() == MessageState.SENT) {
                viewHolder.getStatusView().setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_receipt_from_server));
            } else if (message.getMessageState() == MessageState.DELIVERED) {
                viewHolder.getStatusView().setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_receipt_from_target));
            } else if (message.getMessageState() == MessageState.SEEN) {
                viewHolder.getStatusView().setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_read_receipt_from_target));
            }
        }

        return messageView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).getSender().ordinal();
    }
}
