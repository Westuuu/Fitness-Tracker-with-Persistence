package com.fitness.tracker.data.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workouts")
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL)
    private List<ExerciseDetail> exerciseDetails = new ArrayList<>();

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL)
    private List<TrainingSession> trainingSessions = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    
    public List<ExerciseDetail> getExerciseDetails() { return exerciseDetails; }
    public void setExerciseDetails(List<ExerciseDetail> exerciseDetails) { this.exerciseDetails = exerciseDetails; }
    
    public List<TrainingSession> getTrainingSessions() { return trainingSessions; }
    public void setTrainingSessions(List<TrainingSession> trainingSessions) { this.trainingSessions = trainingSessions; }
}