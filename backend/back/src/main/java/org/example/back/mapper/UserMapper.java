package org.example.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.back.entity.User;

@Mapper
public interface UserMapper {

    void insert(User user);

    User findByUsername(String username);

    User selectById(Long id);

    void update(User user);
}