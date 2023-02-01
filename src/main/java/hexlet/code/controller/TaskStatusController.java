package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";

    public static final String ID = "/{id}";

    private final TaskStatusRepository taskStatusRepository;

    private final TaskStatusService taskStatusService;

    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.createTaskStatus(taskStatusDto);
    }

    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream().toList();
    }

    @GetMapping(ID)
    public TaskStatus getTaskStatus(@PathVariable final long id) {
        return taskStatusRepository.findById(id).get();
    }

    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@PathVariable final long id,
                                       @RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable final long id) {
        taskStatusRepository.deleteById(id);
    }

}
