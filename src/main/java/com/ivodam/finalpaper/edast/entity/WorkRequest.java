package com.ivodam.finalpaper.edast.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "work_request")
public class WorkRequest {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    private String requestName;

    private String workSelection;

    private String fullName;

    private String dateOfBirth;

    private String placeOfBirth;

    private String employersName;

    private String profession;

    private String yearsOfExperience;

    private String periodOfWork;

    private String reasonForRequest;

    private String dateCreated;

    private boolean read;

    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
