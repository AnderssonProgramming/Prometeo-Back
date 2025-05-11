package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
public class Goal extends BaseEntity {
    // @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    // @Column(name = "goal_id", nullable = false)

    // private UUID goalId;
    @Column(name = "user_id", nullable = false)
    private UUID userId;


    @Column(name = "goal", nullable = false)
    private String goal;

    @Column(name = "active", nullable = false)
    private boolean active;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    // public UUID getGoalId() {
    //     return goalId;
    // }

    // public void setGoalId(UUID goalId) {
    //     this.goalId = goalId;
    // }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
