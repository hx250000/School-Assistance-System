package org.example.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.back.entity.TaskParticipant;

@Mapper
public interface TaskParticipantMapper {

    void insert(TaskParticipant tp);
}