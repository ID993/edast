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
@Entity(name = "education_request")
public class EducationRequest {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    private String requestName;

    @NotEmpty(message = "Please select one option")
    private String educationSelection;

    @Size(min = 4, max = 50, message = "Name can't be empty and must be between 4 and 50 characters")
    private String fullName;

    @Pattern(regexp = "^(0?[1-9]|[1-2][0-9]|3[0-1])\\.(0?[1-9]|1[0-2])\\.\\d{4}\\.$", message = "Invalid date format. Use dd.mm.yyyy.")
    private String dateOfBirth;

    @Size(min = 4, max = 50, message = "Place of birth can't be empty and must be between 4 and 50 characters")
    private String placeOfBirth;

    @Size(min = 4, max = 50, message = "School name can't be empty and must be between 4 and 50 characters")
    private String schoolName;

    @Size(min = 4, max = 50, message = "Year of enrollment can't be empty and must be between 4 and 50 characters")
    private String yearOfEnrollment;

    @Size(min = 4, max = 50, message = "Year of graduation can't be empty and must be between 4 and 50 characters")
    private String yearOfGraduation;

    @Size(min = 4, max = 50, message = "ID of diploma can't be empty and must be between 4 and 50 characters")
    private String idOfDiploma;

    @Size(min = 4, max = 50, message = "Occupation can't be empty and must be between 4 and 50 characters")
    private String occupation;

    @NotEmpty(message = "Please select one option")
    private String adultEducation;

    private String dateCreated;

    private boolean read;

    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
