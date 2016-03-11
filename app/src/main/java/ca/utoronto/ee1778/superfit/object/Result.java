package ca.utoronto.ee1778.superfit.object;

import java.io.Serializable;

/**
 * Created by liuwyang on 2016-03-07.
 */
public class Result implements Serializable {

    private boolean result;
    private int recommendWeight;
    private String errorMessage;

    private double x;
    private double y;
    private double z;

    public Result(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    @Override
    public String toString() {
        return "Result{" +
                "result=" + result +
                ", recommendWeight=" + recommendWeight +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
