package org.example.back.service;

import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.HomeStatResp;
import org.example.back.dto.response.TaskVO;
import org.example.back.dto.response.UserInfoVO;

import java.util.List;

public interface TaskService {

    Long createTask(TaskCreateRequest request);

    List<TaskVO> list(int page, int size, String status);

    TaskVO grabTask(Long taskId);

    void finishTask(Long taskId);

    void cancelTask(Long taskId);

    List<TaskVO> myTaskHistory();

    List<TaskVO> myParticipatedTasks();

    List<TaskVO> findByTitle(String keywords);

    TaskVO findById(Long id);

    HomeStatResp stats();

    List<UserInfoVO> participants(Long taskId);
}