package br.com.alura.AluraFake.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SingleChoiceOptionDTO {

    @NotBlank
    @Size(min = 4, max = 80)
    private String option;

    private boolean isCorrect;

    public String getOption() {
        return option;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
