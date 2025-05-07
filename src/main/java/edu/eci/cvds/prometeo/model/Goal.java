package edu.eci.cvds.prometeo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Goal {
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "goal_id", nullable = false)
    private UUID goalId;

    @Column(name = "goal", nullable = false)
    private String goal;

    @Column(name = "active", nullable = false)
    private boolean active;

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setGoalId(UUID goalId) {
        this.goalId = goalId;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getGoalId() {
        return goalId;
    }

    public String getGoal() {
        return goal;
    }

    public boolean getActive() {
        return active;
    }
}
