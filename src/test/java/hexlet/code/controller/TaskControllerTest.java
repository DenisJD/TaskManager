package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;

import static hexlet.code.config.SpringConfig.TEST_PROFILE;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfig.class)
public class TaskControllerTest {

    private static final String TASK_NAME = "task";

    private static final String TASK_NAME_2 = "newTask";

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils utils;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void before() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    private ResultActions getDefaultTaskRequest() throws Exception {
        final User user = userRepository.findByEmail(TEST_USERNAME).get();

        final TaskStatus taskStatus = getTaskStatus();

        final TaskDto taskDto = new TaskDto(
            TASK_NAME,
            "description",
            user.getId(),
            taskStatus.getId(),
            Set.of()
        );

        final var requestTask = post(TASK_CONTROLLER_PATH)
            .content(asJson(taskDto))
            .contentType(APPLICATION_JSON);

        return utils.perform(requestTask, TEST_USERNAME);
    }

    private TaskStatus getTaskStatus() throws Exception {
        final TaskStatusDto taskStatusDto = new TaskStatusDto("status");

        final var requestTaskStatus = post(TASK_STATUS_CONTROLLER_PATH)
            .content(asJson(taskStatusDto))
            .contentType(APPLICATION_JSON);

        final var responseTaskStatus = utils.perform(requestTaskStatus, TEST_USERNAME)
            .andReturn().getResponse().getContentAsString();

        return fromJson(responseTaskStatus, new TypeReference<>() {
        });
    }


    @Test
    public void createTask() throws Exception {
        assertEquals(0, taskRepository.count());
        getDefaultTaskRequest().andExpect(status().isCreated());
        assertEquals(1, taskRepository.count());
    }

    @Test
    public void getTask() throws Exception {
        Task expectedTask = fromJson(getDefaultTaskRequest()
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse().getContentAsString(), new TypeReference<>() {
            });

        final var response = utils.perform(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId()), TEST_USERNAME)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTask.getName(), task.getName());
    }

    @Test
    public void getAllTasks() throws Exception {
        getDefaultTaskRequest().andExpect(status().isCreated());

        final var response = utils.perform(
                get(TASK_CONTROLLER_PATH), TEST_USERNAME)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks).hasSize(1);
    }

    @Test
    public void updateTask() throws Exception {
        Task expectedTask = fromJson(getDefaultTaskRequest()
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse().getContentAsString(), new TypeReference<>() {
            });

        final TaskDto updatedTask = new TaskDto(
            TASK_NAME_2,
            expectedTask.getDescription(),
            expectedTask.getExecutor().getId(),
            expectedTask.getTaskStatus().getId(),
            Set.of()
        );

        final var updatedRequest = put(TASK_CONTROLLER_PATH + ID, expectedTask.getId())
            .content(asJson(updatedTask))
            .contentType(APPLICATION_JSON);

        utils.perform(updatedRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(taskRepository.existsById(expectedTask.getId()));
        assertNull(taskRepository.findByName(TASK_NAME).orElse(null));
        assertNotNull(taskRepository.findByName(TASK_NAME_2).orElse(null));
    }

    @Test
    public void deleteTask() throws Exception {
        getDefaultTaskRequest().andExpect(status().isCreated());

        final long taskId = taskRepository.findByName(TASK_NAME).get().getId();

        utils.perform(
                delete(TASK_CONTROLLER_PATH + ID, taskId), TEST_USERNAME)
            .andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }
}
