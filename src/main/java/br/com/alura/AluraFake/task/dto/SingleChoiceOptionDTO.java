package br.com.alura.AluraFake.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SingleChoiceOptionDTO {

    @NotBlank(message = "Option text must not be blank.")
    @Size(min = 4, max = 80, message = "Each option must be between 4 and 80 characters.")
    private String option;

    private boolean isCorrect;

    // Default constructor required for deserialization
    public SingleChoiceOptionDTO() {}

    // Constructor with arguments to support tests
    public SingleChoiceOptionDTO(String option, boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public String getOption() {
        return option;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
