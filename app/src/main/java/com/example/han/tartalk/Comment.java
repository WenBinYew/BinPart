package com.example.han.tartalk;

import java.util.Map;

public class Comment {

    public String comment;
    public String uid;
    public String date;
    public String name;
    public String id;
    public String postid;
    public int likeCount = 0;
    public int dislikeCount = 0;
    public Map<String, Boolean> likes = null;
    public Map<String, Boolean> dislikes = null;

}
