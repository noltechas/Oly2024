package com.nolte.beerolympics2024;

import java.util.List;

public class Team {
    private List<Contestant> contestants;
    private String teamName;
    private String teamAnthem;
    private int score = 0;
    private int points = 0;
    private int drinks = 0;
    private int pukes = 0;

    public Team(List<Contestant> contestants, String teamName, String teamAnthem) {
        this.contestants = contestants;
        this.teamName = teamName;
        this.teamAnthem = teamAnthem;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getDrinks() {
        return drinks;
    }

    public void setDrinks(int drinks) {
        this.drinks = drinks;
    }

    public int getPukes() {
        return pukes;
    }

    public void setPukes(int pukes) {
        this.pukes = pukes;
    }

    // Getters and setters for each field
    public List<Contestant> getContestants() { return contestants; }
    public void setContestants(List<Contestant> contestants) { this.contestants = contestants; }
    public void addContestant(Contestant contestant) { this.contestants.add(contestant); }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTeamAnthem() { return teamAnthem; }
    public void setTeamAnthem(String teamAnthem) { this.teamAnthem = teamAnthem; }

    public void printInfo() {
        System.out.print("\nName: " + teamName + ", Anthem: " + teamAnthem + ", Players: ");
        for(int i = 0; i < contestants.size(); i++)
            System.out.print(contestants.get(i).getRank() + ", ");
        System.out.print("\n");
    }
}
