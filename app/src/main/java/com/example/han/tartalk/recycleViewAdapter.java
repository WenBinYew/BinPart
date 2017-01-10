package com.example.han.tartalk;

import java.util.Map;


public class recycleViewAdapter {

    public String Name,Image;
    public Map<String, Object> postID;

    public recycleViewAdapter(){}

    public recycleViewAdapter(String name, String image) {
        Name = name;
        Image = image;
    }


    public String getName() {
        return Name;
    }

    public String getImage() {
        return Image;
    }

}
