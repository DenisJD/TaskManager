package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create new Task Status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task Status created",
            content = @Content(schema = @Schema(implementation = TaskStatus.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "422", description = "The data for creating a Task Status is invalid")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.createTaskStatus(taskStatusDto);
    }

    @Operation(summary = "Get all Task Statuses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task Statuses are received",
            content = @Content(schema = @Schema(implementation = TaskStatus.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
    })
    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream().toList();
    }

    @Operation(summary = "Get Task Status by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task Status received",
            content = @Content(schema = @Schema(implementation = TaskStatus.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "404", description = "Task Status not found")
    })
    @GetMapping(ID)
    public TaskStatus getTaskStatus(@PathVariable final long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Operation(summary = "Update Task Status by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task Status updated",
            content = @Content(schema = @Schema(implementation = TaskStatus.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "404", description = "Task Status not found"),
        @ApiResponse(responseCode = "422", description = "The data for updating a Task Status is invalid")
    })
    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@PathVariable final long id,
                                       @RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @Operation(summary = "Delete Task Status by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task Status deleted"),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "422", description = "Task Status is used")
    })
    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable final long id) {
        taskStatusRepository.deleteById(id);
    }
}
