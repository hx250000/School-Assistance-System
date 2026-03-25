package org.example.back.entity;
import lombok.Data;
@Data
public class TaskParticipant {
    private Long id;
    private Long taskId;
    private Long userId;

    private String status; // JOINED / FINISHED
}