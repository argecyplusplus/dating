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
@Entity(name = "profiles")
@Table(name = "profiles")
public class Profile {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_seq")
    @SequenceGenerator(name = "profile_seq", sequenceName = "profile_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name="name")
    @Nonnull
    private String name;

    @Column(name="age")
    private int age;

    @Column(name="avatar")
    @Nonnull
    private String avatar;

    @Column(name="city")
    @Nonnull
    private String city;

    @Column(name="gender")
    @Nonnull
    private String gender;

    @Column(name="description")
    private String description;

    @Column(name="social_link")
    @Nonnull
    private String socialLink;

    @Column(name="min_age")
    private int minAge;

    @Column(name="max_age")
    private int maxAge;

}
