package com.nolte.beerolympics2024;

import java.util.ArrayList;
import java.util.Random;

public class Contestant {

    private String name;
    private int rank;
    private String choice;
    private String imagePath;
    private int row;
    private int column;
    private double yValue;

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

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int setRandomRow(int contestants, ArrayList<Integer> takenSpots){
        int column;
        if(rank <= contestants/3)
            column = 0;
        else if(rank <= (contestants/3)*2)
            column = 1;
        else
            column = 2;

        this.column = column;
        int location;
        int numbers = contestants / 3;
        Random r = new Random();
        int low = numbers * column;
        int high = ((column+1)*numbers);
        location = r.nextInt(high - low) + low;
        while(takenSpots.contains(location))
            location = r.nextInt(high - low) + low;

        this.row = location - (contestants/3)*column;

        return location;
    }

    public double getyValue() {
        return yValue;
    }

    public void setyValue(double yValue) {
        this.yValue = yValue;
    }
}
