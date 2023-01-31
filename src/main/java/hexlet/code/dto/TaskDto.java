package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    @Size(min = 3, max = 1000)
    private String name;

    @Lob
    private String description;

    private Long executorId;

    @NotNull
    private Long taskStatusId;

}
