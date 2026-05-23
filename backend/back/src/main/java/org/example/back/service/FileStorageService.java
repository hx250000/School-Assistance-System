package org.example.back.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * 保存上传的文件
     * @param file 核心文件
     * @param subDir 子目录，如 "avatars" 或 "shop"
     * @return 返回相对访问路径，例如 "uploads/avatars/abc-123.jpg"
     */
    String storeFile(MultipartFile file, String subDir);
}
