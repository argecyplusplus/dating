package ru.chernyukai.projects.dating.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "interests")
@Entity(name = "interests")
@AllArgsConstructor
@NoArgsConstructor
public class Interest {
    @Id
    @GeneratedValue(generator = "interest_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "interest_seq", sequenceName = "interest_seq", allocationSize = 1)
    private Long id;

    @Enumerated
    private InterestValue value;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

}