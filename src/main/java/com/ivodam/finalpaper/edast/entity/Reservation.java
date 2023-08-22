package com.ivodam.finalpaper.edast.entity;

import jakarta.persistence.*;
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
@Entity(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;


    String dateOfReservation;

    @Pattern(regexp = "(?i)^HR-DAST-\\d+.*$", message = "Please enter a valid signature. Example: HR-DAST-1234 or hr-dast-1234")
    String fondSignature;

    @Size(min = 2, max = 512, message = "Please enter a valid description")
    String technicalUnits;

    String dateCreated;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
