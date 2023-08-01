package com.ivodam.finalpaper.edast.entity;


import jakarta.persistence.*;
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
@Entity(name = "special_request")
public class SpecialRequest {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    private String requestName;

    @Size(min = 2, max = 255, message = "Title must be between 4 and 255 characters")
    private String title;

    @Size(min = 4, max = 50, message = "Creator name must be between 4 and 50 characters")
    private String creatorName;

    @Size(max = 1024, message = "Description must be less than 1024 characters")
    private String documentDescription;

    private String dateCreated;

    private boolean read;

    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
