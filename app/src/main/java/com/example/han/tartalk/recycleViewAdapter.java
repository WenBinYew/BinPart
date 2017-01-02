package com.example.han.tartalk;

import java.util.Map;

/**
 * Created by Bin on 11/12/2016.
 */

public class recycleViewAdapter {

    public String Name,Title,Image,Date,Content;
    public Map<String, Object> postID;

    public recycleViewAdapter(){}

    public recycleViewAdapter(String name, String title, String image, String date, String content) {
        Name = name;
        Title = title;
        Image = image;
        Date = date;
        Content = content;

    }


    public String getName() {
        return Name;
    }

    public String getTitle() {
        return Title;
    }

    public String getImage() {
        return Image;
    }

    public String getDate() {
        return Date;
    }

    public String getContent() {
        return Content;
    }


}
