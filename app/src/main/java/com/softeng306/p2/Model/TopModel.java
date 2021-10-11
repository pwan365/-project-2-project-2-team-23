package com.softeng306.p2.Model;

public class TopModel {
    Integer topImg;
    String topName;


    public TopModel(Integer tpImg, String tpName){
        this.topImg = tpImg;
        this.topName = tpName;
    };

    public Integer getTpImg(){
        return topImg;
    }

    public String getTpName(){
        return topName;
    }
}
