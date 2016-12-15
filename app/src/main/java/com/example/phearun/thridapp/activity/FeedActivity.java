package com.example.phearun.thridapp.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phearun.thridapp.listener.ItemClickListener;
import com.example.phearun.thridapp.adapter.MyFeedAdapter;
import com.example.phearun.thridapp.R;
import com.example.phearun.thridapp.entity.Feed;
import com.example.phearun.thridapp.url.URL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FeedActivity extends AppCompatActivity implements ItemClickListener {

    private RecyclerView mRecyclerView;
    private List<Feed> mFeeds = new ArrayList<>();
    private MyFeedAdapter myFeedAdapter;

    private Socket socket;
    private Context context;

    EditText txtPost;
    Button btnPost;
    TextView tvTyping;

    Timer timer = new Timer();
    boolean isTimerSet;

    private View.OnKeyListener onKeyUpEvent = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(event.getAction() == KeyEvent.ACTION_UP){
                socket.emit("typing", "Phearun");
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        this.context = this;

        this.bindingEventListener();

        txtPost = (EditText) findViewById(R.id.txtPost);
        btnPost = (Button) findViewById(R.id.btnPost);
        tvTyping = (TextView) findViewById(R.id.tvTyping);

        btnPost.setOnClickListener(this.onButtonPostClick);
        txtPost.setOnKeyListener(this.onKeyUpEvent);
        tvTyping.setVisibility(View.GONE);
    }
    private Emitter.Listener onConnectEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET", "connnect");
        }
    };


    private Emitter.Listener onDisconnectEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET", "disconnect");
        }
    };

    private Emitter.Listener onAllPostEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET", "all post");

            String jsonString = args[1].toString();

            List<Feed> feeds = new Gson().fromJson(jsonString, new TypeToken<List<Feed>>(){}.getType());
            mFeeds.addAll(feeds);

            /*JSONArray jsonArray = (JSONArray) args[1];

            for(int i=0; i<jsonArray.length(); i++){
                try {
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String username = jsonArray.getJSONObject(i).getString("username");
                    String text = jsonArray.getJSONObject(i).getString("text");
                    int like = jsonArray.getJSONObject(i).getInt("like");

                    mFeeds.add(new Feed(id, username, text, like));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/
            Log.d("SOCKET", mFeeds + "");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    renderView();
                }
            });
        }
    };


    private Emitter.Listener onNewPostEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String jsonString = args[0].toString();

            final Feed feed = new Gson().fromJson(jsonString, Feed.class);

            //final JSONObject obj = (JSONObject) args[0];

            Log.e("SOCKET", "onMessage" + feed);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*Feed feed = new Feed();
                    try {
                        feed.setId(obj.getString("id"));
                        feed.setUsername(obj.getString("username"));
                        feed.setText(obj.getString("text"));
                        feed.setLike(obj.getInt("like"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    //mFeeds.add(feed);

                    mFeeds.add(0, feed);

                    //myFeedAdapter.notifyItemInserted(mFeeds.size()-1);
                    myFeedAdapter.notifyItemInserted(0);
                    scrollToTop();
                }
            });
        }
    };

    private Emitter.Listener onRemovePostEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String id = (String) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final int index = Feed.findIndexById(mFeeds, id);
                    mFeeds.remove(index);
                    myFeedAdapter.notifyItemRemoved(index);
                }
            });
        }
    };

    private Emitter.Listener onLikePostEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SOCKET", args[0] + ", " + args[1]);
            int like = Integer.parseInt(args[0].toString());
            String id = args[1].toString();
            updateLike(like, id);
        }
    };

    public void updateLike(final int like, final String id){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = Feed.findIndexById(mFeeds, id);
                mFeeds.get(index).setLike(like);
                myFeedAdapter.notifyItemChanged(index);
            }
        });
    }

    private Emitter.Listener onTypingEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            renderTypingView(View.VISIBLE);
        }
    };

    private Emitter.Listener onStopTypingEvent = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            renderTypingView(View.INVISIBLE);
        }
    };

    public void renderView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        myFeedAdapter = new MyFeedAdapter(mFeeds, context);
        mRecyclerView.setAdapter(myFeedAdapter);
    }

    private void renderTypingView(final int visibility){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTyping.setVisibility(visibility);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String id = mFeeds.get(position).getId();
        socket.emit("remove post", id);
    }

    @Override
    public void onLikeClick(int position) {
        String id = mFeeds.get(position).getId();
        socket.emit("like post", id);
    }

    public void bindingEventListener(){

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            socket = IO.socket(URL.FEED_NAMESPACE, opts);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, this.onConnectEvent);
        socket.on(Socket.EVENT_DISCONNECT, this.onDisconnectEvent);
        socket.on("all posts", this.onAllPostEvent);
        socket.on("new post", this.onNewPostEvent);
        socket.on("removed post", this.onRemovePostEvent);
        socket.on("update like", this.onLikePostEvent);
        socket.on("typing", this.onTypingEvent);
        socket.on("stop typing", this.onStopTypingEvent);

        socket.connect();
    }

    public void unbindEventListener(){

        socket.disconnect();

        socket.off(Socket.EVENT_CONNECT, this.onConnectEvent);
        socket.off(Socket.EVENT_DISCONNECT, this.onDisconnectEvent);
        socket.off("all posts", this.onAllPostEvent);
        socket.off("new post", this.onNewPostEvent);
        socket.off("removed post", this.onRemovePostEvent);
        socket.off("update like", this.onLikePostEvent);
        socket.off("typing", this.onTypingEvent);
        socket.off("stop typing", this.onStopTypingEvent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindEventListener();
    }

    private View.OnClickListener onButtonPostClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String text = txtPost.getText().toString();
                String username = "Phearun";
                Feed feed = new Feed(null, text, username, 0);
                String jsonString = new Gson().toJson(feed);

                JSONObject jsonObject = new JSONObject(jsonString);
                socket.emit("new post", jsonObject, new Ack() {
                    @Override
                    public void call(Object... args) {
                        clearText();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //TODO: Clear status text
    private void clearText(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtPost.setText("");
            }
        });
    }

    //TODO: scroll recycler view to top
    private void scrollToTop(){
        mRecyclerView.scrollToPosition(0);
    }
}
