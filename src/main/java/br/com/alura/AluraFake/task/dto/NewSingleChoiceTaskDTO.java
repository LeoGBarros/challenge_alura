package br.com.alura.AluraFake.task.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class NewSingleChoiceTaskDTO {

    @NotNull
    private Long courseId;

    @NotBlank
    @Size(min = 4, max = 255)
    private String statement;

    @NotNull
    @Min(1)
    private Integer order;

    @Size(min = 2, max = 5)
    @NotNull
    private List<@Valid SingleChoiceOptionDTO> options;

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }

    public List<SingleChoiceOptionDTO> getOptions() {
        return options;
    }
}
