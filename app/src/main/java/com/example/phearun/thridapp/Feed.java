package com.example.phearun.thridapp;

import java.util.List;

/**
 * Created by Phearun on 12/14/2016.
 */

public class Feed {
    private String id;
    private String text;
    private String username;
    private int like;

    public Feed() {
    }

    public Feed(String id, String text, String username, int like) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.like = like;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", username='" + username + '\'' +
                ", like=" + like +
                '}';
    }

    public static int findIndexById(List<Feed> feeds, String id){
        for(int i=0; i<feeds.size(); i++){
            if(feeds.get(i).getId().equals(id))
                return i;
        }
        return 0;
    }

}
