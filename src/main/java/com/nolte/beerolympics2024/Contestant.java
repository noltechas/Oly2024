package com.nolte.beerolympics2024;

public class Contestant {

    private String name;
    private int rank;
    private String choice;
    private String imagePath;

    public Contestant(String name, int rank, String choice, String imagePath){
        this.name = name;
        this.rank = rank;
        this.choice = choice;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
