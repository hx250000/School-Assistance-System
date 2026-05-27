package org.example.back.service.impl;

import lombok.extern.java.Log;
import org.example.back.exception.ResourceConflictException;
import org.example.back.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    // 允许的图片格式
    private static final List<String> ALLOWED_EXTENSIONS = List.of("image/jpeg", "image/png", "image/gif");
    // 最大 8MB
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024;
    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Override
    public String storeFile(MultipartFile file, String subDir) {
        // 1. 基础校验
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传图片不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过 4MB");
        }

//        // 2. 校验文件后缀（先不加，等后续需要再补充）
//        String contentType = file.getContentType();
//        if (contentType==null || !ALLOWED_EXTENSIONS.contains(contentType)) {
//            throw new IllegalArgumentException("只支持 JPG, PNG, GIF 格式的图片!");
//        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() : "";

        try {
            // 3. 确定最终存放的绝对路径
            String baseUploadPath = System.getProperty("user.dir") + File.separator + "uploads";
            String targetDirPath = baseUploadPath + File.separator + subDir;

            File targetDir = new File(targetDirPath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            // 4. 使用 UUID 生成新文件名
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            Path targetLocation = Paths.get(targetDirPath).resolve(newFilename);

            // 5. 写入本地文件系统
            Files.copy(file.getInputStream(), targetLocation);
            String returnPath = "uploads/" + subDir + "/" + newFilename;
            log.info("save file to {}", returnPath);

            // 6. 返回相对访问路径（供前端组合成完整 URL）
            return returnPath;

        } catch (IOException e) {
            throw new RuntimeException("图片上传失败，请重试", e);
        }
    }


}
