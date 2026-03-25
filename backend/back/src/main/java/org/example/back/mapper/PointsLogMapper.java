package org.example.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.back.entity.PointsLog;

@Mapper
public interface PointsLogMapper {

    void insert(PointsLog log);
}