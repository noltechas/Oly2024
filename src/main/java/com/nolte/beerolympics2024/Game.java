package com.nolte.beerolympics2024;

class Game {
    private String name;
    private int numOfPlayers;

    public Game(String name, int numOfPlayers) {
        this.name = name;
        this.numOfPlayers = numOfPlayers;
    }

    public String getName() {
        return name;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}