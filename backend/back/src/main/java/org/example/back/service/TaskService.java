package org.example.back.service;

import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.HomeStatResp;
import org.example.back.dto.response.TaskVO;
import org.example.back.entity.Task;

import java.util.List;

public interface TaskService {

    Long createTask(TaskCreateRequest request);

    List<TaskVO> list(int page, int size);

    Task grabTask(Long taskId);

    void finishTask(Long taskId);

    void cancelTask(Long taskId);

    List<Task> myTaskHistory();

    List<TaskVO> findByTitle(String keywords);

    TaskVO findById(Long id);

    HomeStatResp stats();
}