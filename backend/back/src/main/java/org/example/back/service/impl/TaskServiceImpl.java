package org.example.back.service.impl;

import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.TaskVO;
import org.example.back.entity.Task;
import org.example.back.entity.TaskParticipant;
import org.example.back.mapper.TaskMapper;
import org.example.back.mapper.TaskParticipantMapper;
import org.example.back.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskParticipantMapper participantMapper;

    @Override
    public Long createTask(TaskCreateRequest request) {

        Task task = new Task();
        BeanUtils.copyProperties(request, task);

        task.setStatus("OPEN");
        task.setCurrentPeople(0);

        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    public List<TaskVO> list(int page, int size) {
        return taskMapper.selectList();
    }

    @Override
    public void grabTask(Long taskId) {

        Task task = taskMapper.selectById(taskId);

        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        if (!"OPEN".equals(task.getStatus())) {
            throw new RuntimeException("任务不可抢");
        }

        if (task.getCurrentPeople() >= task.getNeedPeople()) {
            throw new RuntimeException("人数已满");
        }

        // 更新人数
        task.setCurrentPeople(task.getCurrentPeople() + 1);

        if (task.getCurrentPeople().equals(task.getNeedPeople())) {
            task.setStatus("IN_PROGRESS");
        }

        taskMapper.update(task);

        // 插入参与记录
        TaskParticipant tp = new TaskParticipant();
        tp.setTaskId(taskId);
        tp.setUserId(1L); // TODO 从JWT获取

        participantMapper.insert(tp);
    }

    @Override
    public void finishTask(Long taskId) {

        // 直接用已有方法
        taskMapper.finishTask(taskId);

        // ⚠️这里没有 participant 查询方法，所以先不发积分
        // 如果你要发积分，必须补一个Mapper方法（我下面会给）
    }
}