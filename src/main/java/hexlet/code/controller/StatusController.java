package hexlet.code.controller;

import hexlet.code.dto.StatusDto;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import hexlet.code.service.StatusService;
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

import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class StatusController {

    public static final String STATUS_CONTROLLER_PATH = "/statuses";

    public static final String ID = "/{id}";

    private final StatusRepository statusRepository;

    private final StatusService statusService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Status createStatus(@RequestBody @Valid final StatusDto statusDto) {
        return statusService.createStatus(statusDto);
    }

    @GetMapping
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    @GetMapping(ID)
    public Status getStatus(@PathVariable final long id) {
        return statusRepository.findById(id).get();
    }

    @PutMapping(ID)
    public Status updateStatus(@PathVariable final long id, @RequestBody final StatusDto statusDto) {
        return statusService.updateStatus(id, statusDto);
    }

    @DeleteMapping(ID)
    public void deleteStatus(@PathVariable final long id) {
        statusRepository.deleteById(id);
    }

}
