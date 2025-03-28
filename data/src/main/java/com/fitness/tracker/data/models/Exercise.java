package com.fitness.tracker.data.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    private List<ExerciseDetail> exerciseDetails = new ArrayList<>();

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    private List<UserExercise> userExercises = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<ExerciseDetail> getExerciseDetails() { return exerciseDetails; }
    public void setExerciseDetails(List<ExerciseDetail> exerciseDetails) { this.exerciseDetails = exerciseDetails; }
    
    public List<UserExercise> getUserExercises() { return userExercises; }
    public void setUserExercises(List<UserExercise> userExercises) { this.userExercises = userExercises; }
}