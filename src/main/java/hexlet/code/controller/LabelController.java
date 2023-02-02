package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
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

import static hexlet.code.controller.LabelController.LABELS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABELS_CONTROLLER_PATH)
public class LabelController {

    public static final String LABELS_CONTROLLER_PATH = "/labels";

    public static final String ID = "/{id}";

    private final LabelRepository labelRepository;

    private final LabelService labelService;

    @Operation(summary = "Create new Label")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Label created",
            content = @Content(schema = @Schema(implementation = Label.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "422", description = "The data for creating a Label is invalid")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createLabel(@RequestBody @Valid final LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Get all Labels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Labels are received",
            content = @Content(schema = @Schema(implementation = Label.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
    })
    @GetMapping
    public List<Label> getLabels() {
        return labelRepository.findAll().stream().toList();
    }

    @Operation(summary = "Get Label by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label received",
            content = @Content(schema = @Schema(implementation = Label.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "404", description = "Label not found")
    })
    @GetMapping(ID)
    public Label getLabel(@PathVariable final long id) {
        return labelRepository.findById(id).get();
    }

    @Operation(summary = "Update Label by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label updated",
            content = @Content(schema = @Schema(implementation = Label.class))),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "404", description = "Label not found"),
        @ApiResponse(responseCode = "422", description = "The data for updating a Label is invalid")
    })
    @PutMapping(ID)
    public Label updateLabel(@PathVariable final long id,
                             @RequestBody @Valid final LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @Operation(summary = "Delete Label by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label deleted"),
        @ApiResponse(responseCode = "401", description = "User is unauthorized"),
        @ApiResponse(responseCode = "404", description = "Label not found"),
        @ApiResponse(responseCode = "422", description = "Label is used")
    })
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable final long id) {
        labelRepository.deleteById(id);
    }
}
