package br.com.alura.AluraFake.task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SingleChoiceOptionDTO {

    @NotBlank(message = "O texto da alternativa não pode estar em branco.")
    @Size(min = 4, max = 80, message = "Cada alternativa deve ter entre 4 e 80 caracteres.")
    private String option;

    @JsonProperty("isCorrect")
    private boolean isCorrect;

    public String getOption() {
        return option;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }
}
