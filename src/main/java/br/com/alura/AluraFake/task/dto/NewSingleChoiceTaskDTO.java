package br.com.alura.AluraFake.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class NewSingleChoiceTaskDTO {

    @NotNull(message = "Course ID is required.")
    private Long courseId;

    @NotBlank(message = "Statement cannot be blank.")
    @Size(min = 4, max = 255, message = "Statement must be between 4 and 255 characters.")
    private String statement;

    @NotNull(message = "Order is required.")
    @Min(value = 1, message = "Order must be a positive integer.")
    private Integer order;

    @NotNull(message = "Option list cannot be null.")
    @Size(min = 2, max = 5, message = "You must provide between 2 and 5 options.")
    private List<@Valid SingleChoiceOptionDTO> options;

    // Getters
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

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setOptions(List<SingleChoiceOptionDTO> options) {
        this.options = options;
    }
}
