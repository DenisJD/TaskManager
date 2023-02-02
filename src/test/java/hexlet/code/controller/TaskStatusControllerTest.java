package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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

import static hexlet.code.config.SpringConfig.TEST_PROFILE;
import static hexlet.code.controller.LabelController.ID;
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
public class TaskStatusControllerTest {

    private static final String STATUS_NAME = "status";

    private static final String STATUS_NAME_2 = "newStatus";

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void before() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void clear() throws Exception {
        utils.tearDown();
    }

    private ResultActions regDefaultTaskStatus() throws Exception {
        final TaskStatusDto taskStatusDto = new TaskStatusDto(STATUS_NAME);
        final var request = post(TASK_STATUS_CONTROLLER_PATH)
            .content(asJson(taskStatusDto))
            .contentType(APPLICATION_JSON);
        return utils.perform(request, TEST_USERNAME);
    }

    @Test
    public void createTaskStatus() throws Exception {
        assertEquals(0, taskStatusRepository.count());
        regDefaultTaskStatus().andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());

    }

    @Test
    public void getTaskStatus() throws Exception {
        regDefaultTaskStatus();

        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.perform(
                get(TASK_STATUS_CONTROLLER_PATH + ID, expectedTaskStatus.getId()), TEST_USERNAME)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTaskStatus.getName(), taskStatus.getName());
    }

    @Test
    public void getAllTaskStatuses() throws Exception {
        regDefaultTaskStatus();

        final var response = utils.perform(
                get(TASK_STATUS_CONTROLLER_PATH), TEST_USERNAME)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatuses).hasSize(1);
    }

    @Test
    public void updateTaskStatus() throws Exception {
        regDefaultTaskStatus();

        final Long taskStatusId = taskStatusRepository.findByName(STATUS_NAME).get().getId();

        final var taskStatusDto = new TaskStatusDto(STATUS_NAME_2);

        final var updateRequest = put(TASK_STATUS_CONTROLLER_PATH + ID, taskStatusId)
            .content(asJson(taskStatusDto))
            .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(taskStatusRepository.existsById(taskStatusId));
        assertNull(taskStatusRepository.findByName(STATUS_NAME).orElse(null));
        assertNotNull(taskStatusRepository.findByName(STATUS_NAME_2).orElse(null));
    }

    @Test
    public void deleteTaskStatus() throws Exception {
        regDefaultTaskStatus();

        final Long taskStatusId = taskStatusRepository.findByName(STATUS_NAME).get().getId();

        utils.perform(
                delete(TASK_STATUS_CONTROLLER_PATH + ID, taskStatusId), TEST_USERNAME)
            .andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }
}
