package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    @Override
    public Task createTask(final TaskDto taskDto) {
        final Task task = fromDto(taskDto);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(long id, TaskDto taskDto) {
        final Task taskToUpdate = taskRepository.findById(id).get();
        merge(taskToUpdate, taskDto);
        return taskRepository.save(taskToUpdate);
    }

    private void merge(final Task taskToUpdate, final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        taskToUpdate.setName(newTask.getName());
        taskToUpdate.setDescription(newTask.getDescription());
        taskToUpdate.setTaskStatus(newTask.getTaskStatus());
        taskToUpdate.setExecutor(newTask.getExecutor());
    }

    private Task fromDto(final TaskDto taskDto) {

        final TaskStatus taskStatus = Optional.ofNullable(taskDto.getTaskStatusId())
            .map(TaskStatus::new)
            .orElse(null);

        final User author = userService.getCurrentUser();

        final User executor = Optional.ofNullable(taskDto.getExecutorId())
            .map(User::new)
            .orElse(null);

        return Task.builder()
            .name(taskDto.getName())
            .description(taskDto.getDescription())
            .taskStatus(taskStatus)
            .author(author)
            .executor(executor)
            .build();
    }
}
