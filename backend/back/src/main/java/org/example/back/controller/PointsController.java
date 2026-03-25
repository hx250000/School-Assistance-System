package org.example.back.controller;

import org.example.back.common.Result;
import org.example.back.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping("/info")
    public Result getPoints() {
        return Result.success(pointsService.getUserPoints());
    }
}