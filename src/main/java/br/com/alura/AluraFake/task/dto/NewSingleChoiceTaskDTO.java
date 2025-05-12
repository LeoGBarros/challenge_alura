package br.com.alura.AluraFake.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class NewSingleChoiceTaskDTO {

    @NotNull(message = "O ID do curso é obrigatório.")
    private Long courseId;

    @NotBlank(message = "O enunciado não pode estar em branco.")
    @Size(min = 4, max = 255, message = "O enunciado deve ter entre 4 e 255 caracteres.")
    private String statement;

    @NotNull(message = "A ordem é obrigatória.")
    @Min(value = 1, message = "A ordem deve ser um número inteiro positivo.")
    private Integer order;

    @NotNull(message = "A lista de opções não pode ser nula.")
    @Size(min = 2, max = 5, message = "A atividade deve ter entre 2 e 5 alternativas.")
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
