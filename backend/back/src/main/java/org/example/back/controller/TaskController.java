package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.request.GrabTaskRequest;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.HomeStatResp;
import org.example.back.dto.response.TaskVO;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public ApiResponse<Long> create(@RequestBody TaskCreateRequest request) {
        return ApiResponse.success(taskService.createTask(request));
    }

    /**
     * 分页查询任务记录
     * @param page 分页
     * @param size 页面大小
     * @return OPEN状态TaskVO
     */
    @GetMapping("/list")
    public ApiResponse<List<TaskVO>> list(@RequestParam int page,
                                          @RequestParam int size) {
//        AllTaskListResponse taskListResponse=new AllTaskListResponse();
        return ApiResponse.success(taskService.list(page, size,"OPEN"));
    }

    /**
     * 管理员查询任务记录
     * @param page 分页
     * @param size 页面大小
     * @param status Task状态
     * @return status状态TaskVO
     */
    @GetMapping("/admin/list")
    public ApiResponse<List<TaskVO>> adminList(@RequestParam int page, @RequestParam int size, @RequestParam String status){
        return ApiResponse.success(taskService.list(page, size, status));
    }

    @GetMapping("/{taskId}/parcitipants")
    public ApiResponse<List<UserInfoVO>> parcitipants(@PathVariable long taskId) {
        return ApiResponse.success(taskService.participants(taskId));
    }

    @PostMapping("/grab")
    public ApiResponse<TaskVO> grab(@RequestBody GrabTaskRequest request) {
        TaskVO taskGrabbed=taskService.grabTask(request.getTaskId());
        return ApiResponse.success(taskGrabbed);
    }

    @PostMapping("/{taskId}/finish")
    public ApiResponse<String> finish(@PathVariable Long taskId) {
        taskService.finishTask(taskId);
        return ApiResponse.success("任务完成");
    }

    @PostMapping("/{taskId}/cancel")
    public ApiResponse<String> cancel(@PathVariable Long taskId) {
        taskService.cancelTask(taskId);
        return ApiResponse.success("任务已取消");
    }

    @GetMapping("/history")
    public ApiResponse<List<TaskVO>> myTaskHistory() {
        return ApiResponse.success(taskService.myTaskHistory());
    }

    @GetMapping("/joined")
    public ApiResponse<List<TaskVO>> myParticipatedTasks() {
        return ApiResponse.success(taskService.myParticipatedTasks());
    }

    @GetMapping("/search")
    public ApiResponse<List<TaskVO>> search(@RequestParam String keyword) {
        return ApiResponse.success(taskService.findByTitle(keyword));
    }

    @GetMapping("/task")
    public ApiResponse<TaskVO> getTaskById(@RequestParam Long taskId) {
        return ApiResponse.success(taskService.findById(taskId));
    }

    @GetMapping("/stats")
    public ApiResponse<HomeStatResp> homeStats(){
        return ApiResponse.success(taskService.stats());
    }
}