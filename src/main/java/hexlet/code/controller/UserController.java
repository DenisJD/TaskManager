package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
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
import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";

    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final UserService userService;

    private final UserRepository userRepository;

    @Operation(summary = "Create new User")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "422", description = "The data for creating a User is invalid")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public User createUser(@RequestBody @Valid final UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Operation(summary = "Get all Users")
    @ApiResponse(responseCode = "200", description = "Users are received",
        content = @Content(schema = @Schema(implementation = User.class)))
    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll().stream().toList();
    }

    @Operation(summary = "Get User by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User received",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(ID)
    public User getUser(@PathVariable final long id) {
        return userRepository.findById(id).get();
    }

    @Operation(summary = "Update User by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "403", description = "Incorrect User owner"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "422", description = "The data for updating a User is invalid")
    })
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@PathVariable final long id,
                           @RequestBody @Valid final UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @Operation(summary = "Delete User by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted"),
        @ApiResponse(responseCode = "403", description = "Incorrect User owner"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable final long id) {
        userRepository.deleteById(id);
    }

}
