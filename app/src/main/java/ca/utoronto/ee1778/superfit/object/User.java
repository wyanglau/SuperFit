package ca.utoronto.ee1778.superfit.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwyang on 2016-03-07.
 */
public class User implements Serializable {

    private Long id;
    private String name;
    private String age;
    private String weight;
    private List<Exercise> onGoingExercises;


    public User(String name, String age, String weight, Long id) {

        this.id = id;
        this.name = name;
        this.age = age;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", weight='" + weight + '\'' +
                ", onGoingExercises=" + onGoingExercises +
                '}';
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<Exercise> getOnGoingExercises() {
        return onGoingExercises;
    }

    public void setOnGoingExercises(List<Exercise> onGoingExercises) {
        this.onGoingExercises = onGoingExercises;
    }

}
