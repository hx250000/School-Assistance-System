package org.example.back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "task_participant")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "任务参与者实体")
public class TaskParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "参与记录ID")
    private Long id;

    @Column(name = "task_id", nullable = false)
    @Schema(description = "任务ID")
    private Long taskId;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "用户ID")
    private Long userId;

    @Column(nullable = false, length = 20)
    @Schema(description = "参与状态", example = "JOINED/FINISHED")
    private String status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "JOINED";
        }
    }
}