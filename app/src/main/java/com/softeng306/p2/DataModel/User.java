package com.softeng306.p2.DataModel;

import java.util.ArrayList;
import java.util.List;

public class User {
    private List<Integer> favourites = new ArrayList<>();;

    private User(){}

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
