package ru.chernyukai.projects.dating.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter //Генерируем геттеры
@Setter //Генерируем сеттеры
@ToString
@Entity(name = "profiles")
@Table(name = "profiles")
public class Profile {

    @Id
    @SequenceGenerator(name = "profile_id_seq", sequenceName = "profile_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_id_seq")
    private Long id;

    @Column(name="name")
    @Nonnull
    private String name;

    @Column(name="age")
    private int age;

    @OneToMany(mappedBy = "profile", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProfilePhoto> photos;

    @Column(name="city")
    @Nonnull
    private String city;

    @Column(name="gender")
    @Nonnull
    private String gender;

    @OneToMany(mappedBy = "profile", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Interest> interests;

    @Column(name="description")
    private String description;

    @Column(name="social_link")
    @Nonnull
    private String socialLink;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="is_visible")
    private boolean isVisible;
}
