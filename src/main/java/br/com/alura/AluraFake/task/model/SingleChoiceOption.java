package br.com.alura.AluraFake.task.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class SingleChoiceOption {

    private String option;
    private boolean isCorrect;

    public SingleChoiceOption() {}

    public SingleChoiceOption(String option, boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public String getOption() {
        return option;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
