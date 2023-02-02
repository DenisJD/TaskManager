package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.LabelController.LABELS_CONTROLLER_PATH;
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
public class LabelControllerTest {
    private static final String LABEL_NAME = "label";

    private static final String LABEL_NAME_2 = "newLabel";

    @Autowired
    private LabelRepository labelRepository;

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

    private ResultActions regDefaultLabel() throws Exception {
        final LabelDto labelDto = new LabelDto(LABEL_NAME);
        final var request = post(LABELS_CONTROLLER_PATH)
            .content(asJson(labelDto))
            .contentType(APPLICATION_JSON);
        return utils.perform(request, TEST_USERNAME);
    }

    @Test
    public void createLabel() throws Exception {
        assertEquals(0, labelRepository.count());
        regDefaultLabel().andExpect(status().isCreated());
        assertEquals(1, labelRepository.count());

    }

    @Test
    public void getLabel() throws Exception {
        regDefaultLabel();

        final Label expectedLabel = labelRepository.findAll().get(0);

        final var response = utils.perform(
                get(LABELS_CONTROLLER_PATH + ID, expectedLabel.getId()), TEST_USERNAME)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedLabel.getName(), label.getName());
    }

    @Test
    public void getAllLabels() throws Exception {
        regDefaultLabel();

        final var response = utils.perform(
                get(LABELS_CONTROLLER_PATH), TEST_USERNAME)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(labels).hasSize(1);
    }

    @Test
    public void updateLabel() throws Exception {
        regDefaultLabel();

        final Long labelId = labelRepository.findByName(LABEL_NAME).get().getId();

        final var labelDto = new LabelDto(LABEL_NAME_2);

        final var updateRequest = put(LABELS_CONTROLLER_PATH + ID, labelId)
            .content(asJson(labelDto))
            .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(labelRepository.existsById(labelId));
        assertNull(labelRepository.findByName(LABEL_NAME).orElse(null));
        assertNotNull(labelRepository.findByName(LABEL_NAME_2).orElse(null));
    }

    @Test
    public void deleteLabel() throws Exception {
        regDefaultLabel();

        final Long labelId = labelRepository.findByName(LABEL_NAME).get().getId();

        utils.perform(
                delete(LABELS_CONTROLLER_PATH + ID, labelId), TEST_USERNAME)
            .andExpect(status().isOk());

        assertEquals(0, labelRepository.count());
    }
}
