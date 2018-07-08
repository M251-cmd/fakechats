package de.twometer.fakechats.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.twometer.fakechats.R;
import de.twometer.fakechats.model.ContactData;
import de.twometer.fakechats.save.SaveState;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<ChatItem> arrayAdapter;

    private class ChatItem {
        private int id;
        private String title;

        public ChatItem(int id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayAdapter.clear();
        for (SaveState.Chat chat : SaveState.getChats()) {
            arrayAdapter.add(new ChatItem(chat.id, chat.data.getName()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SaveState.create(getApplicationContext().getFilesDir());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = findViewById(R.id.all_chats_list_view);
        listView.setAdapter(arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatItem item = (ChatItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_IDENTIFIER, item.id);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveState.createChat(new ContactData(getString(R.string.contact_default), getString(R.string.last_seen_default)));
                arrayAdapter.add(new ChatItem(1, getString(R.string.contact_default)));
            }
        });
    }

}
