package com.example.han.tartalk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by han on 21/12/2016.
 */

public class Post implements Serializable {


    public String id;
    public String content;
    public String uid;
    public String title;
    public String image;
    public String date;
    public String name;
    public Map<String, Object> likes;
    public Map<String, Object> dislikes;
    public Map<String, Object> comments;





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
