package br.com.alura.AluraFake.task.model;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends Task {

    @ElementCollection
    @CollectionTable(name = "single_choice_options", joinColumns = @JoinColumn(name = "task_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "option", column = @Column(name = "option_text")),
            @AttributeOverride(name = "isCorrect", column = @Column(name = "is_correct"))
    })
    private List<SingleChoiceOption> options;

    @Deprecated
    public SingleChoiceTask() {}

    public SingleChoiceTask(String statement, Integer order, Course course, List<SingleChoiceOption> options) {
        super(statement, order, course);
        this.options = options;
    }

    public List<SingleChoiceOption> getOptions() {
        return options;
    }
}
