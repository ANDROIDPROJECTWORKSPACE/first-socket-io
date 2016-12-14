package com.example.phearun.thridapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.R.id.message;


public class MainActivity extends AppCompatActivity {

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            socket = IO.socket("http://192.168.178.127:3000/chat", opts);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, this.onConnectHandler);
        socket.on(Socket.EVENT_DISCONNECT, this.onDisConnectHandler);
        socket.on("message", this.onMessageHandler);

        socket.connect();

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

                Toast.makeText(MainActivity.this, "Sending...", Toast.LENGTH_SHORT).show();

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

            JSONObject obj = new JSONObject();
            obj = (JSONObject) args[0];

            Log.e("ooooo", "onMessage" + obj);

            try {
                show(obj.get("username") +"", obj.get("message") + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    public void show(final String username, final String message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TextView tvDisplay = (TextView) findViewById(R.id.textView2);

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                tvDisplay.append(username + ": " + message + "\r\n");
            }
        });

    }


}
