package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final LabelRepository labelRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final UserRepository userRepository;

    @Override
    public Task createTask(final TaskDto taskDto) {
        final Task task = fromDto(taskDto);
        final Long executorId = taskDto.getExecutorId();
        if (executorId != null) {
            task.setExecutor(userRepository.findById(executorId).get());
        }
        taskRepository.save(task);
        return task;
    }

    @Override
    public Task updateTask(long id, final TaskDto taskDto) {
        final Task taskToUpdate = taskRepository.findById(id).get();
        merge(taskToUpdate, taskDto);
        final Long executorId = taskDto.getExecutorId();
        if (executorId != null) {
            taskToUpdate.setExecutor(userRepository.findById(executorId).get());
        }
        taskRepository.save(taskToUpdate);
        return taskToUpdate;
    }

    private void merge(final Task taskToUpdate, final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        taskToUpdate.setName(newTask.getName());
        taskToUpdate.setDescription(newTask.getDescription());
        taskToUpdate.setTaskStatus(newTask.getTaskStatus());
        taskToUpdate.setLabels(newTask.getLabels());
    }

    private Task fromDto(final TaskDto taskDto) {

        final User author = userService.getCurrentUser();

        final TaskStatus taskStatus = taskStatusRepository.findById(taskDto.getTaskStatusId()).get();

        final Set<Label> labels = taskDto.getLabelIds().stream()
            .map(x -> labelRepository.findById(x).get())
            .collect(Collectors.toSet());

        return Task.builder()
            .name(taskDto.getName())
            .description(taskDto.getDescription())
            .taskStatus(taskStatus)
            .author(author)
            .labels(labels)
            .build();
    }
}
