package org.example.back.dto.request;
import lombok.Data;
@Data
public class GrabTaskRequest {
    private Long taskId;
    private Long userId;
}