package com.softeng306.p2.Helpers;

import com.softeng306.p2.DataModel.Tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SortTagsByType {

    /**
     * Method sorts tags, from a list of tags passed in, into separate lists of tags organised by
     * tag type and all stored in an array
     *
     * @param tagsList List of Tag objects
     * @return Tags sorted into a list by tag type, with each list stored in a array list
     */
    public static ArrayList<List<String>> listTagTypes(List<Tag> tagsList) {

        // Create string hash set of types to ensure no duplicates
        LinkedHashSet<String> tagTypes = new LinkedHashSet<>();
        // Loop through all tags to find and store their type
        for (Tag tag : tagsList) {
            tagTypes.add(tag.getTagType());
        }

        // Sort all tags by type by looping through each tag type
        ArrayList<List<String>> sortedTags = new ArrayList<>();
        for (String type : tagTypes) {
            // New list for the specific type
            List<String> tagNames = new ArrayList<>();
            tagNames.add(type); // Adds the type name first

            // Loops through all tags and adds tags that match the type to the list
            for (Tag tag : tagsList) {
                if (tag.getTagType().equals(type)) {
                    tagNames.add(tag.getTagName());
                }
            }
            // Once found all matching tags to the specific type, adds the list to the array
            sortedTags.add(tagNames);
        }

        return sortedTags;
    }
}
