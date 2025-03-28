package com.fitness.tracker.data.models;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_exercises")
public class UserExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private float goal;

    @Column(nullable = true)
    private LocalDate goalDeadline;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    
    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }
    
    public float getGoal() { return goal; }
    public void setGoal(float goal) { this.goal = goal; }
    
    public LocalDate getGoalDeadline() { return goalDeadline; }
    public void setGoalDeadline(LocalDate goalDeadline) { this.goalDeadline = goalDeadline; }
}