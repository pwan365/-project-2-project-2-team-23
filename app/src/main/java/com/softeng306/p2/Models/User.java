package com.softeng306.p2.Models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private static User _user = null;
    private static List<Integer> favourites;

    private User(){
        favourites = new ArrayList<>();
    }

    public static User getInstance(){
        if (_user == null){
            _user = new User();
        }
        return _user;
    }

    public List<Integer> getFavourites(){
        return favourites;
    }

    public void addFavourite(int id){
        favourites.add(id);
    }

    public void removeFavourite(int id){
        favourites.remove(id);
    }
}
