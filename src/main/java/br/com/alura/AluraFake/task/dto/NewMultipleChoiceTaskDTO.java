package br.com.alura.AluraFake.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class NewMultipleChoiceTaskDTO {

    @NotNull(message = "Course ID is required.")
    private Long courseId;

    @NotBlank(message = "Statement must not be blank.")
    @Size(min = 4, max = 255, message = "Statement must be between 4 and 255 characters.")
    private String statement;

    @NotNull(message = "Order is required.")
    @Min(value = 1, message = "Order must be a positive integer.")
    private Integer order;

    @NotNull(message = "Options list must not be null.")
    @Size(min = 3, max = 5, message = "You must provide between 3 and 5 options.")
    private List<@Valid SingleChoiceOptionDTO> options;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<SingleChoiceOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<SingleChoiceOptionDTO> options) {
        this.options = options;
    }
}
