package com.example.han.tartalk;

import java.util.Map;

/**
 * Created by Bin on 11/12/2016.
 */

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
