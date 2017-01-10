package com.example.han.tartalk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class Post implements Serializable {


    public String id = null;
    public String content;
    public String uid;
    public String title;
    public String image;
    public String date;
    public String name;
    public int likeCount = 0;
    public int dislikeCount = 0;
    public int commentCount = 0;
    public Map<String, Boolean> likes = null;
    public Map<String, Boolean> dislikes = null;
    public Map<String, Boolean> comments = null;





    public Post(String content, String date, String image, String id, String name, String title, String uid) {
        this.content = content;
        this.date = date;
        this.image = image;
        this.id = id;
        this.name = name;
        this.title = title;
        this.uid = uid;

    }

    public Post() {
    }

    public String getTitle() {
        return title;
    }


}
