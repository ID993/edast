package com.ivodam.finalpaper.edast.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
@Entity(name = "cadastral_request")
public class CadastralRequest {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    private String requestName;

    @NotEmpty(message = "Please select one option")
    private String cadastralSelection;


    @Size(min = 2, max = 50, message = "Municipality must be between 2 and 50 characters")
    private String cadastralMunicipality;

    private String landParcels;

    private String buildingParcels;

    @Size(min = 4, max = 50, message = "Period must be between 4 and 50 characters.")
    private String period;

    private String dateCreated;

    private boolean read;

    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
