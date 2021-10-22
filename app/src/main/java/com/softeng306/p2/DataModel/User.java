package com.softeng306.p2.DataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent the user
 */
public class User {
    private List<Integer> favourites = new ArrayList<>();;

    /**
     * empty constructor for firebase
     */
    private User(){}

    /**
     * Get the list of favourite vehicle's id
     * @return list of ids
     */
    public List<Integer> getFavourites(){
        return favourites;
    }

    /**
     * Method for adding a vehicle to the favourites
     * @param id vehicle id
     */
    public void addFavourite(int id){
        favourites.add(id);
    }

    /**
     * Method for removeing a vehicle from the favourites
     * @param id vehicle id
     */
    public void removeFavourite(int id){
        favourites.remove(id);
    }
}
