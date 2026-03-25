package org.example.back.controller;

import org.example.back.common.Result;
import org.example.back.dto.request.GrabTaskRequest;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
@Data
@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public Result create(@RequestBody TaskCreateRequest request) {
        return Result.success(taskService.createTask(request));
    }

    @GetMapping("/list")
    public Result list(@RequestParam int page,
                       @RequestParam int size) {
        return Result.success(taskService.list(page, size));
    }

    @PostMapping("/grab")
    public Result grab(@RequestBody GrabTaskRequest request) {
        taskService.grabTask(request.getTaskId());
        return Result.success("抢单成功");
    }

    @PostMapping("/finish")
    public Result finish(@RequestParam Long taskId) {
        taskService.finishTask(taskId);
        return Result.success("任务完成");
    }
}