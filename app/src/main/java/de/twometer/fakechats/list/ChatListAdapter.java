package de.twometer.fakechats.list;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

import de.twometer.fakechats.R;
import de.twometer.fakechats.model.ChatMessage;
import de.twometer.fakechats.model.MessageSender;
import de.twometer.fakechats.model.MessageState;

public class ChatListAdapter extends BaseAdapter {

    private static final String OTHER_NBSP = " &#160;&#160;&#160;&#160;&#160;&#160;";
    private static final String SELF_NBSP = " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";

    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    private DateFormat dateFormat;

    public ChatListAdapter(Context context, ArrayList<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.dateFormat = android.text.format.DateFormat.getTimeFormat(context);
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

    private float dp2px(int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View messageView = null;
        ChatMessage message = chatMessages.get(position);

        if (message.getSender() == MessageSender.OTHER) {
            ViewHolderOther viewHolder;
            if (convertView == null) {
                messageView = LayoutInflater.from(context).inflate(R.layout.chat_item_other, null, false);
                FrameLayout backgroundHolder = messageView.findViewById(R.id.background_holder);
                TextView messageTextView = messageView.findViewById(R.id.textview_message);
                TextView timeTextView = messageView.findViewById(R.id.textview_time);
                viewHolder = new ViewHolderOther(backgroundHolder, messageTextView, timeTextView);
                messageView.setTag(viewHolder);
            } else {
                messageView = convertView;
                viewHolder = (ViewHolderOther) messageView.getTag();
            }

            viewHolder.getTextView().setText(Html.fromHtml(Html.escapeHtml(message.getContent()) + buildNbspString(OTHER_NBSP, message.getSentTime())));
            viewHolder.getTimeView().setText(dateFormat.format(message.getSentTime()));

            MessageSender previous = getMessageSender(position - 1);
            viewHolder.getBackgroundHolder().setBackgroundResource(getResource(message.getSender(), previous));
            if (previous != message.getSender())
                setMarginLeft(viewHolder.getTextView(), (int) dp2px(8));
            else
                setMarginLeft(viewHolder.getTextView(), (int) dp2px(4));

        } else if (message.getSender() == MessageSender.SELF) {
            ViewHolderSelf viewHolder;
            if (convertView == null) {
                messageView = LayoutInflater.from(context).inflate(R.layout.chat_item_self, null, false);
                FrameLayout backgroundHolder = messageView.findViewById(R.id.background_holder);
                ImageView statusView = messageView.findViewById(R.id.user_reply_status);
                TextView textView = messageView.findViewById(R.id.textview_message);
                TextView timeView = messageView.findViewById(R.id.textview_time);
                LinearLayout marginHolder = messageView.findViewById(R.id.marginHolder);
                viewHolder = new ViewHolderSelf(marginHolder, backgroundHolder, statusView, textView, timeView);
                messageView.setTag(viewHolder);
            } else {
                messageView = convertView;
                viewHolder = (ViewHolderSelf) messageView.getTag();
            }

            viewHolder.getTextView().setText(Html.fromHtml(message.getContent() + buildNbspString(SELF_NBSP, message.getSentTime())));
            viewHolder.getTimeView().setText(dateFormat.format(message.getSentTime()));
            MessageSender previous = getMessageSender(position - 1);
            viewHolder.getBackgroundHolder().setBackgroundResource(getResource(message.getSender(), previous));

            if (previous != message.getSender())
                setMarginRight(viewHolder.getMarginHolder(), (int) dp2px(4));
            else
                setMarginRight(viewHolder.getMarginHolder(), (int) dp2px(0));

            if (message.getMessageState() == MessageState.SENT) {
                viewHolder.getStatusView().setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_receipt_from_server));
            } else if (message.getMessageState() == MessageState.DELIVERED) {
                viewHolder.getStatusView().setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_receipt_from_target));
            } else if (message.getMessageState() == MessageState.SEEN) {
                viewHolder.getStatusView().setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_read_receipt_from_target));
            }
        } else if (message.getSender() == MessageSender.SYSTEM_DATE) {
            ViewHolderSystem viewHolder;
            if (convertView == null) {
                messageView = LayoutInflater.from(context).inflate(R.layout.chat_item_date, null, false);
                TextView textView = messageView.findViewById(R.id.system_message);
                viewHolder = new ViewHolderSystem(textView);
                messageView.setTag(viewHolder);
            } else {
                messageView = convertView;
                viewHolder = (ViewHolderSystem) messageView.getTag();
            }
            viewHolder.getTextView().setText(message.getContent());
        } else if (message.getSender() == MessageSender.SYSTEM_UNKNOWN_NUMBER) {
            if (convertView == null) {
                messageView = LayoutInflater.from(context).inflate(R.layout.chat_item_unknownchat, null, false);
            } else {
                messageView = convertView;
            }
        }

        return messageView;
    }

    private void setMarginRight(View view, int px) {
        ViewGroup.MarginLayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, px, params.bottomMargin);
        view.setLayoutParams(params);
    }

    private void setMarginLeft(View view, int px) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(px, params.topMargin, params.rightMargin, params.bottomMargin);
        view.setLayoutParams(params);
    }

    private MessageSender getMessageSender(int idx) {
        if (idx < 0)
            return MessageSender.SYSTEM_UNKNOWN_NUMBER; // Could be anything != OTHER && != SELF
        return chatMessages.get(idx).getSender();
    }

    private int getResource(MessageSender current, MessageSender last) {
        if (current == MessageSender.OTHER) {
            if (current == last) return R.drawable.balloon_incoming_normal_ext;
            else return R.drawable.balloon_incoming_normal;
        } else if (current == MessageSender.SELF) {
            if (current == last) return R.drawable.balloon_outgoing_normal_ext;
            else return R.drawable.balloon_outgoing_normal;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).getSender().ordinal();
    }

    private String buildNbspString(String base, long time) {
        int append = dateFormat.format(time).length();
        StringBuilder baseBuilder = new StringBuilder(base);
        for (int i = 0; i < append; i++)
            baseBuilder.append("&#160;");
        base = baseBuilder.toString();
        return base;
    }
}
