package ru.chernyukai.projects.dating.model;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter //Генерируем геттеры
@Setter //Генерируем сеттеры
@ToString
public class ProfileInfo {
    private String name;
    private int age;
    private String avatar;
    private String city;
    private String gender;
    private String description;
    private String socialLink;
}
