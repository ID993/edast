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
@Entity(name = "registry_request")
public class RegistryRequest extends RequestForm{

    private String requestName;

    private String bdmSelection;

    private String fullName;

    private String dateOfBirth;

    private String placeOfBirth;

    private String fathersName;

    private String mothersName;

    private String mothersMaidenName;

    private String dateOfMarriage;

    private String placeOfMarriage;

    private String dateOfDeath;

    private String dateCreated;

}
