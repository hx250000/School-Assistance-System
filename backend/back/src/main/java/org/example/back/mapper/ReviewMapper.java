package org.example.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.back.entity.Review;

@Mapper
public interface ReviewMapper {
    void insert(Review review);
}