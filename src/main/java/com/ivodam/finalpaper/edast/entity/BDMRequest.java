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
@Entity(name = "bdm_request")
public class BDMRequest {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    private String requestName;

    private String bdmSelection;

    @Size(min = 4, max = 50, message = "Person of interest must be between 4 and 50 characters")
    private String fullName;

    private String dateOfBirth;

    private String placeOfBirth;

    @Size(min = 2, max = 50, message = "Father's name must be between 2 and 50 characters")
    private String fathersName;

    @Size(min = 2, max = 50, message = "Mother's name must be between 2 and 50 characters")
    private String mothersName;

    @Size(min = 3, max = 50, message = "Mother's maiden name must be between 3 and 50 characters")
    private String mothersMaidenName;

    private String dateOfMarriage;

    private String placeOfMarriage;

    private String dateOfDeath;

    private String dateCreated;

    private boolean read;

    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
