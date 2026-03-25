package org.example.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.back.dto.response.TaskVO;
import org.example.back.entity.Task;

import java.util.List;

@Mapper
public interface TaskMapper {

    void insert(Task task);

    Task selectById(Long id);

    List<TaskVO> selectList();

    void update(Task task);

    void finishTask(Long taskId);
}