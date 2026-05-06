package org.example.back.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.HomeStatResp;
import org.example.back.dto.response.TaskVO;
import org.example.back.entity.Task;
import org.example.back.entity.TaskParticipant;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceConflictException;
import org.example.back.repository.TaskParticipantRepository;
import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.TaskRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.TaskService;
import org.example.back.service.PointsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskParticipantRepository taskParticipantRepository;

    @Autowired
    private PointsService pointsService;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Long createTask(TaskCreateRequest request) {

        Task task = new Task();
        BeanUtils.copyProperties(request, task);

//        // BeanUtils 不会自动把 String -> LocalDateTime 做可靠转换
//        // 同时 TaskCreateRequest 目前没有 getter，因此用反射读取 deadline 字段再解析。
//        String deadlineStr = extractDeadlineStr(request);
//        task.setDeadline(parseDeadline(deadlineStr));
        String deadlinestr = request.getDeadline();
        task.setDeadline(parseDeadline(deadlinestr));

        task.setCurrentPeople(0);
        task.setStatus("OPEN");

        // 从JWT中获取真实发布人ID
        Long publisherId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (publisherId == null) {
            throw new AuthenticationException("用户未登录，无法发布任务");
        }
        task.setPublisherId(publisherId);

        Task saved = taskRepository.save(task);
        return saved.getId();
    }

    @Override
    public List<TaskVO> list(int page, int size) {
        log.info("list tasks, page={}, size={}",page,size);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<TaskVO> resp=taskRepository.findAllByStatus("OPEN", pageable)
                .getContent()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        log.info("list resp: "+ resp);
        // 传入状态过滤条件
        return resp;
    }

    @Override
    @Transactional
    public TaskVO grabTask(Long taskId) {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        log.info("grabtask, userid={}, taskid={}",userId,taskId);

        // 先查任务（校验状态）
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        if (!"OPEN".equals(task.getStatus())) {
            throw new ResourceConflictException("任务不可抢");
        }

        if (taskParticipantRepository.existsByTaskIdAndUserId(taskId, userId)){
            throw new ResourceConflictException("你已经抢过该任务！");
        }

        // 再抢名额（原子操作）
        int updated = taskRepository.incrementIfNotFull(taskId);
        if (updated == 0) {
            throw new ResourceConflictException("任务已满");
        }

        // 插入参与记录（用唯一索引兜底！）
        TaskParticipant participant=null;
        try {
            participant = TaskParticipant.builder()
                    .taskId(taskId)
                    .userId(userId)
                    .status("JOINED")
                    .build();

            taskParticipantRepository.save(participant);

        } catch (DataIntegrityViolationException e){
            taskRepository.decrementIfNotEmpty(taskId);
            throw new ResourceConflictException("你已经抢过该任务！");
        } catch (Exception e) {
            taskRepository.decrementIfNotEmpty(taskId);
            throw new RuntimeException("系统异常，请稍后再试");
        }

        //该修改有竞态条件，采用原子化操作

        taskRepository.updateStatusIfFull(taskId);

        Task taskGrabbed=taskRepository.findById(taskId).get();

        log.info("grab task successfully, taskparticipant={}",participant);

        TaskVO resp=toVO(taskGrabbed);

        return resp;
    }

    @Override
    @Transactional
    public void finishTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务"+taskId+"不存在"));

        // 更新任务状态为已完成
        task.setStatus("FINISHED");
        taskRepository.save(task);

        // 如果没有奖励积分，则只更新任务状态
        Integer reward = task.getRewardPoints();
        if (reward == null || reward <= 0) {
            return;
        }

        // 查找参与者，过滤出仍为 JOINED 的参与者，更新其状态并发放积分
        List<TaskParticipant> participants = taskParticipantRepository.findByTaskId(taskId);
        for (TaskParticipant p : participants) {
            if (!"JOINED".equals(p.getStatus())) {
                continue;
            }
            p.setStatus("FINISHED");
            taskParticipantRepository.save(p);

            // 发放积分（标题和描述带上任务信息）
            String title = "完成任务";
            String desc = "完成任务 " + task.getTitle() + " 获得 "+ reward +" 积分";
            pointsService.addPoints(p.getUserId(), reward, title, desc);
        }
    }

    @Override
    @Transactional
    public void cancelTask(Long taskId) {
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        if (!userId.equals(task.getPublisherId())) {
            throw new ResourceConflictException("仅发布者可取消该任务");
        }

        String status = task.getStatus();
        if ("FINISHED".equals(status)) {
            throw new ResourceConflictException("任务已完成，无法取消");
        }
        if ("CANCELLED".equals(status)) {
            throw new ResourceConflictException("任务已取消");
        }

        taskParticipantRepository.deleteByTaskId(taskId);
        task.setCurrentPeople(0);
        task.setStatus("CANCELLED");
        taskRepository.save(task);
    }

    private TaskVO toVO(Task task) {
        TaskVO vo = new TaskVO();
        User publisher=userRepository.findById(task.getPublisherId())
                        .orElseThrow(()->new ResourceNotFoundException("用户不存在！"));

        vo.setTaskId(task.getId());
        vo.setTitle(task.getTitle());
        vo.setDescription(task.getDescription());
        vo.setNeedPeople(task.getNeedPeople());
        vo.setCurrentPeople(task.getCurrentPeople());
        vo.setStatus(task.getStatus());
        vo.setType(task.getType());
        vo.setPublisherId(task.getPublisherId());
        vo.setPublisherName(publisher.getUsername());

//        vo.setRewardMoney(task.getRewardMoney());
        vo.setRewardPoints(task.getRewardPoints());

        vo.setCreatedAt(task.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        vo.setDeadline(task.getDeadline().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return vo;
    }

    @Override
    public List<TaskVO> myTaskHistory(){
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }
        List<Task> myTasks= taskRepository.findByPublisherId(userId);
        List<TaskVO> myTasksResp=new ArrayList<>();
        for (Task task : myTasks) {
            myTasksResp.add(toVO(task));
        }
        return myTasksResp;
    }

    @Override
    public HomeStatResp stats(){
        HomeStatResp resp = new HomeStatResp();
        List<Task> tasks=taskRepository.findAll();
        int users= (int) userRepository.count();
        int inProgress=0,finished=0;
        for (Task task : tasks) {
            String status = task.getStatus();
            if ("FINISHED".equals(status)) {
                finished++;
            }
            else if ("IN_PROGRESS".equals(status)) {
                inProgress++;
            }
        }
        resp.setInProgress(inProgress);
        resp.setFinished(finished);
        resp.setUsers(users);
        System.out.println("[statresp] "+resp);
        return resp;
    }

    private LocalDateTime parseDeadline(String deadlineStr) {
        if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
            return null;
        }

        String s = deadlineStr.trim();
        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(s, formatter);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        // 兜底：只传日期时，默认时间为 00:00:00
        try {
            LocalDate date = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            throw new IllegalArgumentException("deadline 格式错误: " + deadlineStr);
        }
    }

    @Override
    public List<TaskVO> findByTitle(String keywords){
        List<Task> tasks=taskRepository.findByTitleLike("%"+keywords+"%");
        List<TaskVO> vos=new ArrayList<TaskVO>();
        for(Task task:tasks){
            vos.add(toVO(task));
        }
        return vos;
    }

    @Override
    public TaskVO findById(Long taskId){
        Task t=taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在！"));
        TaskVO vo=toVO(t);
        return vo;
    }
}