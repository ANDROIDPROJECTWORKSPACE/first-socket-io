package com.example.phearun.thridapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.phearun.thridapp.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button chatRoom = (Button) findViewById(R.id.chatActivity);
        Button postStatus = (Button) findViewById(R.id.postStatus);

        chatRoom.setOnClickListener(this.onChatClickListener);
        postStatus.setOnClickListener(this.onPostStatusClickListener);
    }

    private View.OnClickListener onChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onPostStatusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HomeActivity.this, FeedActivity.class);
            startActivity(intent);
        }
    };
}
