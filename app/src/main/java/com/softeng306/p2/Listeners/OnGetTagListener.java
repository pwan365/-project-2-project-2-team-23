package com.softeng306.p2.Listeners;

import com.softeng306.p2.DataModel.Tag;

import java.util.List;

/**
 * Interface used to get a list of Tags
 */
public interface OnGetTagListener {
    void onCallBack(List<Tag> tagList);
}
