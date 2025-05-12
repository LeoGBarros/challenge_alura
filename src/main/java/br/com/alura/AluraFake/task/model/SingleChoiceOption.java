package br.com.alura.AluraFake.task.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SingleChoiceOption {

    @Column(name = "option_text") // evita conflito com palavra reservada 'option'
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
