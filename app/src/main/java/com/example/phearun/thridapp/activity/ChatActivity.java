package com.example.phearun.thridapp.activity;

import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.phearun.thridapp.R;
import com.example.phearun.thridapp.url.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatActivity extends AppCompatActivity {

    private Socket socket;

    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            socket = IO.socket(URL.CHAT_NAMESPACE, opts);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, this.onConnectHandler);
        socket.on(Socket.EVENT_DISCONNECT, this.onDisConnectHandler);
        socket.on("message", this.onMessageHandler);

        socket.connect();

        scrollView = (ScrollView) findViewById(R.id.myScrollView);
        final EditText txtMessage = (EditText) findViewById(R.id.editText);
        Button btnSend = (Button) findViewById(R.id.button);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("username", "Phearun");
                    obj.put("message", txtMessage.getText() + "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(ChatActivity.this, "Sending...", Toast.LENGTH_SHORT).show();

                socket.emit("message", obj);
            }
        });

    }

    private Emitter.Listener onConnectHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("ooooo", "onConnected");
        }
    };


    private Emitter.Listener onDisConnectHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("ooooo", "onDisConnected");
        }
    };


    private Emitter.Listener onMessageHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject obj = (JSONObject) args[0];
            Log.e("ooooo", "onMessage" + obj);

            try {
                renderView(obj.get("username") +"", obj.get("message") + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void renderView(final String username, final String message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView tvDisplay = (TextView) findViewById(R.id.textView2);

                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
                tvDisplay.append(username + ": " + message + "\r\n");

                scrollView.scrollTo(0, 0);

            }
        });

    }

}
