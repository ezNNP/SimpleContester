package ru.stray27.scontester.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"sender", "task"})
public class Attempt {
    @Id
    @GenericGenerator(name = "attempt_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = @org.hibernate.annotations.Parameter(name = "attempt_generator", value = "SEQUENCE")
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attempt_generator")
    private Long id;

    @Enumerated(EnumType.STRING)
    private AttemptStatus attemptStatus;

    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage programmingLanguage;

    private Integer lastTestNumber;

    private String sourceCodeFilename;

    private String ipAddr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_uid")
    private Sender sender;

    @PrePersist
    private void prePersist() {
        this.attemptStatus = AttemptStatus.IN_QUEUE;
    }


}
