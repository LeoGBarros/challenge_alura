package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.*;

public class NewOpenTextTaskDTO {

    @NotNull
    private Long courseId;

    @NotBlank
    @Size(min = 4, max = 255)
    private String statement;

    @NotNull
    @Min(1)
    private Integer order;

    @Deprecated
    public NewOpenTextTaskDTO() {
    }

    public NewOpenTextTaskDTO(Long courseId, String statement, Integer order) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }
}
