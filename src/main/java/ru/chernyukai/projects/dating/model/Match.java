package ru.chernyukai.projects.dating.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter //Генерируем геттеры
@Setter //Генерируем сеттеры
@ToString
@Entity(name = "matches")
@Table(name = "matches")
public class Match {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_seq")
    @SequenceGenerator(name = "match_seq", sequenceName = "match_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "is_pair")
    private boolean isPair;

    @JoinColumn (name="profile1_id")
    @ManyToOne
    @Nonnull
    private Profile profile1;

    @JoinColumn (name="profile2_id")
    @ManyToOne
    @Nonnull
    private Profile profile2;
}
