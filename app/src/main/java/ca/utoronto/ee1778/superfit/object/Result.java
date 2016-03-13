package ca.utoronto.ee1778.superfit.object;

import java.io.Serializable;

/**
 * Created by liuwyang on 2016-03-07.
 */
public class Result implements Serializable {

    private boolean result;
    /**
     * 0: invalid data \n
     * 1: valid data
     */
    private boolean valid;
    private double recommendWeight;
    private String errorMessage;
    private int age;
    private int weightOfUser;
    private boolean finished;
    private Double degree;
    /**
     * for the recording of how much times the user can perform the exercise correctly
     */
    private int successTimes;


    private double hr;
    private double x;
    private double y;
    private double z;
    /**
     * for record purpose (every pulse of data)to display the stack bar chart.
     */
    private int fail_cnt;
    private int suc_cnt;


    public Result(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.degree = calDegree(x, y, z);
    }

    public Double getDegree() {
        return degree;
    }

    public void setDegree(Double degree) {
        this.degree = degree;
    }

    private Double calDegree(double x, double y, double z){
        double epi = 0.00001;
        if (x*x + y*y + z*z < epi) return null;
        if (y*y + z*z < epi) return 90.0;

        double reusltInRadian = Math.atan(x/(y*y + z*z));

        return Math.toDegrees(reusltInRadian);
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

    public double getRecommendWeight() {
        return recommendWeight;
    }

    public void setRecommendWeight(double recommendWeight) {
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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
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

    public int getFail_cnt() {
        return fail_cnt;
    }

    public void setFail_cnt(int fail_cnt) {
        this.fail_cnt = fail_cnt;
    }

    public int getSuc_cnt() {
        return suc_cnt;
    }

    public void setSuc_cnt(int suc_cnt) {
        this.suc_cnt = suc_cnt;
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
