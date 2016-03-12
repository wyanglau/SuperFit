package ca.utoronto.ee1778.superfit.object;

import java.io.Serializable;

/**
 * Created by liuwyang on 2016-03-07.
 */
public class Result implements Serializable {

    private boolean result;
    /**
     *  0: invalid data \n
     *  1: valid data
     */
    private boolean active;
    private int recommendWeight;
    private String errorMessage;
    private int age;
    private int weightOfUser;
    private boolean finished;
    private int successTimes;

    private double hr;
    private double x;
    private double y;
    private double z;

    public Result(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getSuccessTimes() {
        return successTimes;
    }

    public void setSuccessTimes(int successTimes) {
        this.successTimes = successTimes;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getRecommendWeight() {
        return recommendWeight;
    }

    public void setRecommendWeight(int recommendWeight) {
        this.recommendWeight = recommendWeight;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeightOfUser() {
        return weightOfUser;
    }

    public void setWeightOfUser(int weightOfUser) {
        this.weightOfUser = weightOfUser;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getHr() {
        return hr;
    }

    public void setHr(double hr) {
        this.hr = hr;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "Result{" +
                "age=" + age +
                ", result=" + result +
                ", recommendWeight=" + recommendWeight +
                ", errorMessage='" + errorMessage + '\'' +
                ", weightOfUser=" + weightOfUser +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
