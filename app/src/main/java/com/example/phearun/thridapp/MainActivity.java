package com.example.phearun.thridapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


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
            socket = IO.socket("http://192.168.178.143:3000", opts);

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

                Toast.makeText(MainActivity.this, "Sending...", Toast.LENGTH_SHORT).show();

                socket.emit("chat", txtMessage.getText().toString());
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

            final TextView tvDisplay = (TextView) findViewById(R.id.textView2);
            final String message = args[0].toString();
            Log.e("ooooo", "onMessage");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    tvDisplay.append("Anonymous : "  + message + "\r\n");
                }
            });
        }
    };



}
