package org.example.back.service;

import org.example.back.service.impl.FileStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceImplTest {

    private FileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl();
    }

    @Test
    void storeFile_whenFileIsEmpty_shouldThrowIllegalArgumentException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThatThrownBy(() -> fileStorageService.storeFile(emptyFile, "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("上传图片不能为空");
    }

    @Test
    void storeFile_whenFileExceedsMaxSize_shouldThrowIllegalArgumentException() {
        byte[] largeContent = new byte[5 * 1024 * 1024];
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                largeContent
        );

        assertThatThrownBy(() -> fileStorageService.storeFile(largeFile, "test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("图片大小不能超过 4MB");
    }

    @Test
    void storeFile_whenFileIsValid_shouldStoreAndReturnPath() {
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String result = fileStorageService.storeFile(validFile, "testsubdir");

        assertThat(result).startsWith("uploads/testsubdir/");
        assertThat(result).endsWith(".jpg");
    }

    @Test
    void storeFile_shouldGenerateUniqueFilename() {
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "content1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "content2".getBytes()
        );

        String result1 = fileStorageService.storeFile(file1, "test");
        String result2 = fileStorageService.storeFile(file2, "test");

        assertThat(result1).isNotEqualTo(result2);
    }
}