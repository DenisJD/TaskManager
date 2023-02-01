package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";

    public static final String ID = "/{id}";

    private static final String ONLY_AUTHOR_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private final TaskRepository taskRepository;

    private final TaskService taskService;

    @Operation(summary = "Create new Task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "422", description = "The data for creating a Task is invalid")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Task createTask(@RequestBody @Valid final TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Get all Tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task are received",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
    })
    @GetMapping
    public Iterable<Task> getTasks(@QuerydslPredicate final Predicate predicate) {
        return predicate == null ? taskRepository.findAll()
            : taskRepository.findAll(predicate);
    }

    @Operation(summary = "Get Task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task received",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping(ID)
    public Task getTask(@PathVariable final long id) {
        return taskRepository.findById(id).get();
    }

    @Operation(summary = "Update Task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated",
            content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "422", description = "The data for updating a Task is invalid")
    })
    @PutMapping(ID)
    public Task updateTask(@PathVariable final long id,
                           @RequestBody @Valid TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @Operation(summary = "Delete Task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted"),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "403", description = "Incorrect Task author"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public void deleteTask(@PathVariable final long id) {
        taskRepository.deleteById(id);
    }
}
