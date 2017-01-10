package com.example.han.tartalk;

import java.util.Map;

/**
 * Created by han on 26/12/2016.
 */

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
