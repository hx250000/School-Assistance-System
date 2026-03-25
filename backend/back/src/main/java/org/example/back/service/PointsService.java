package org.example.back.service;

public interface PointsService {

    Integer getUserPoints();

    void addPoints(Long userId, Integer points);
}