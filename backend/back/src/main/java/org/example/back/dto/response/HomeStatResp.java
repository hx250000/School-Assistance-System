package org.example.back.dto.response;

import lombok.Data;

@Data
public class HomeStatResp {
    private int inProgress;
    private int finished;
    private int users;
}
