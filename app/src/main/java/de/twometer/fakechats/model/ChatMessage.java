package de.twometer.fakechats.model;

public class ChatMessage {

    private String content;
    private MessageSender sender;
    private MessageState messageState;
    private long sentTime;

    public ChatMessage(String content, MessageSender sender) {
        this.content = content;
        this.sender = sender;
    }

    public ChatMessage(String content, MessageSender sender, MessageState messageState, long sentTime) {
        this.content = content;
        this.sender = sender;
        this.messageState = messageState;
        this.sentTime = sentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageSender getSender() {
        return sender;
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }
}
