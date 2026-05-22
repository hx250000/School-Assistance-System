package org.example.back.dto.response;

import lombok.Data;

@Data
public class FileUploadResponse {
    private String type;
    private String fileUrl;
}
