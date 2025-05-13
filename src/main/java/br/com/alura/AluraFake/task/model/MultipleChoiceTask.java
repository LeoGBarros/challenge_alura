package br.com.alura.AluraFake.task.model;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends Task {

    @ElementCollection
    @CollectionTable(name = "multiple_choice_options", joinColumns = @JoinColumn(name = "task_id"))
    private List<SingleChoiceOption> options;

    @Deprecated
    public MultipleChoiceTask() {
    }

    public MultipleChoiceTask(String statement, Integer order, Course course, List<SingleChoiceOption> options) {
        super(statement, order, course);
        this.options = options;
    }

    public List<SingleChoiceOption> getOptions() {
        return options;
    }
}
