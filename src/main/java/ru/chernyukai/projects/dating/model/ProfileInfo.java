package ru.chernyukai.projects.dating.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter //Генерируем геттеры
@Setter //Генерируем сеттеры
@ToString
public class ProfileInfo {
    private Long id;
    private String name;
    private int age;
    private List<String> photos;
    private String city;
    private String gender;
    private List<String> interests;
    private String description;
    private String socialLink;
}
