package com.example.han.tartalk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by han on 21/12/2016.
 */

public class User implements Serializable {


    public String Image;
    public String Name;
    public Map<String, Object> postID;


    public User(){}


    public User(String Image, String Name) {
        this.Image = Image;
        this.Name = Name;

    }



}
