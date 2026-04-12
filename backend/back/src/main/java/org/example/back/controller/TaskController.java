package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.request.GrabTaskRequest;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody TaskCreateRequest request) {
        return ApiResponse.success(taskService.createTask(request));
    }

    @GetMapping("/list")
    public ApiResponse list(@RequestParam int page,
                       @RequestParam int size) {
        return ApiResponse.success(taskService.list(page, size));
    }

    @PostMapping("/grab")
    public ApiResponse grab(@RequestBody GrabTaskRequest request) {
        taskService.grabTask(request.getTaskId());
        return ApiResponse.success("抢单成功");
    }

    @PostMapping("/finish")
    public ApiResponse finish(@RequestParam Long taskId) {
        taskService.finishTask(taskId);
        return ApiResponse.success("任务完成");
    }

    @PostMapping("/cancel")
    public ApiResponse cancel(@RequestParam Long taskId) {
        taskService.cancelTask(taskId);
        return ApiResponse.success("任务已取消");
    }

    @GetMapping("/history")
    public ApiResponse myTaskHistory() {
        return ApiResponse.success(taskService.myTaskHistory());
    }
}