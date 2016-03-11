package ca.utoronto.ee1778.superfit.object;

import java.io.Serializable;

/**
 * Created by liuwyang on 2016-03-07.
 */
public class Exercise implements Serializable {

    private Long id;
    private String name;
    private String date;
    private double weight;
    private int repetition;
    private int numOfSet;
    private double completionRate;
    private User user;
    private int logoId;
    private Long scheduleId;

    public Exercise(String name, String date, double weight, int repetition,int numOfSet) {
        this.name = name;
        this.date = date;
        this.numOfSet=numOfSet;
        this.repetition = repetition;
        this.weight = weight;

    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getLogoId() {
        return logoId;
    }

    public void setLogoId(int logoId) {
        this.logoId = logoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumOfSet() {
        return numOfSet;
    }

    public void setNumOfSet(int numOfSet) {
        this.numOfSet = numOfSet;
    }
}
