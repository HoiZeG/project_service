package faang.school.projectservice.service;

import faang.school.projectservice.dto.TaskCompletedEvent;
import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.publisher.TaskCompletedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskCompletedEventPublisher taskCompletedEventPublisher;
    private final TaskCompletedEvent taskCompletedEvent;

    public void createTask(TaskDto taskDto) {
        taskRepository.save(taskMapper.toEntity(taskDto));
    }

    public void updateTask(TaskDto taskDto) {
        Task task = taskRepository.findById(taskDto.getId()).orElseThrow(() -> new DataValidationException("Такой задачи не существует"));
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateTaskFromDto(taskDto, task);
        taskRepository.save(task);
        if (task.getStatus() == TaskStatus.DONE) {
            taskCompletedEvent.setUserId(taskDto.getPerformerUserId());
            taskCompletedEvent.setTaskId(taskDto.getId());
            taskCompletedEvent.setProjectId(taskDto.getProjectId());
            taskCompletedEventPublisher.publish(taskCompletedEvent);
        }
    }
}
