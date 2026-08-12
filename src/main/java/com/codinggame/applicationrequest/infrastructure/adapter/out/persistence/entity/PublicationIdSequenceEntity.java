package com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_publication_id_sequence")
@Getter
@NoArgsConstructor
public class PublicationIdSequenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "publication_id_seq_generator")
    @SequenceGenerator(
            name = "publication_id_seq_generator",
            sequenceName = "publication_id_seq",
            initialValue = 1_000_000_000,
            allocationSize = 1
    )
    private Long id;
}
