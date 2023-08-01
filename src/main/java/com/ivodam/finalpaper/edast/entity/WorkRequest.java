package com.ivodam.finalpaper.edast.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotEmpty(message = "Please select one option")
    private String workSelection;

    @Size(min = 4, max = 50, message = "Name must be between 4 and 50 characters")
    private String fullName;

    @NotEmpty(message = "Date of birth is required")
    @Pattern(regexp = "^(0?[1-9]|[1-2][0-9]|3[0-1])\\.(0?[1-9]|1[0-2])\\.\\d{4}\\.$", message = "Invalid date format. Use dd.mm.yyyy.")
    private String dateOfBirth;

    @Size(min = 4, max = 50, message = "Place of birth must be between 4 and 50 characters")
    private String placeOfBirth;

    @Size(min = 2, max = 50, message = "Employer must be between 2 and 50 characters")
    private String employersName;

    @Size(min = 4, max = 50, message = "Profession must be between 4 and 50 characters")
    private String profession;

    @Size(min = 1, max = 3, message = "Years of experience must be between 1 and 3 characters")
    private String yearsOfExperience;

    @Size(min = 4, max = 50, message = "Period of work must be between 4 and 50 characters")
    private String periodOfWork;

    @Size(min = 4, max = 50, message = "Reason for request must be between 4 and 50 characters")
    private String reasonForRequest;

    private String dateCreated;

    private boolean read;

    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
