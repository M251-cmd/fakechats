package de.twometer.fakechats.save;

import android.os.Environment;

import org.nustaq.serialization.FSTConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import de.twometer.fakechats.model.ChatMessage;
import de.twometer.fakechats.model.ContactData;

public class SaveState {

    private static File saveFile;

    private static FSTConfiguration conf = FSTConfiguration.createAndroidDefaultConfiguration();

    private static SaveHolder saveHolder;

    public static ArrayList<Chat> getChats() {
        init();
        return saveHolder.chats;
    }

    public static class Chat implements Serializable {
        public int id;
        public int messageIdCounter;
        public ContactData data;
        public ArrayList<Message> messages = new ArrayList<>();

        public Chat(int id, ContactData data) {
            this.id = id;
            this.data = data;
        }
    }

    public static class Message implements Serializable {
        public int id;
        public ChatMessage chatMessage;
    }

    private static class SaveHolder implements Serializable {
        public ArrayList<Chat> chats = new ArrayList<>();
        public int chatIdCounter;
    }

    public static int createChat(ContactData contactData) {
        init();
        int chatId = saveHolder.chatIdCounter;
        saveHolder.chatIdCounter++;
        saveHolder.chats.add(new Chat(chatId, contactData));
        save();
        return chatId;
    }

    public static Chat getChat(int chatId) {
        init();
        for (Chat chat : saveHolder.chats) {
            if (chat.id == chatId) {
                return chat;
            }
        }
        return null;
    }

    public static void createMessage(int chatId, ChatMessage chatMessage) {
        init();
        Chat c = getChat(chatId);
        if (c == null) return;
        Message message = new Message();
        message.id = c.messageIdCounter;
        c.messageIdCounter++;
        message.chatMessage = chatMessage;
        c.messages.add(message);
        save();
    }

    public static void save() {
        if (saveHolder != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(saveFile);
                outputStream.write(conf.asByteArray(saveHolder));
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void create(File file) {
        saveFile = new File(file, "savestate");
    }

    private static void init() {
        if (saveHolder == null) {
            if (!saveFile.exists()) {
                saveHolder = new SaveHolder();
                return;
            }
            try {
                byte[] t = new byte[(int) saveFile.length()];
                FileInputStream inputStream = new FileInputStream(saveFile);
                inputStream.read(t);
                inputStream.close();
                saveHolder = (SaveHolder) conf.asObject(t);
            } catch (IOException e) {
                e.printStackTrace();
                saveHolder = new SaveHolder();
            }
        }
    }
}
