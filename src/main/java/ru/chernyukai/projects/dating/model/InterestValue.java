package ru.chernyukai.projects.dating.model;

import lombok.Getter;

import java.util.Arrays;


@Getter
public enum InterestValue {

    SPORTS ("Спорт"),
    MOVIES ("Фильмы"),
    ANIME ("Аниме"),
    LITERATURE ("Литература"),
    MUSIC ("Музыка"),
    TRAVELING ("Путешествия"),
    PHOTOGRAPHY ("Фотография"),
    COOKING ("Кулинария"),
    TECHNOLOGY ("Технологии"),
    ART ("Искусство"),
    PSYCHOLOGY ("Психология"),
    HISTORY ("История"),
    ARCHITECTURE ("Архитектура"),
    FASHION ("Мода"),
    LANGUAGES ("Языки"),
    NATURE ("Природа"),
    THEATER ("Театр"),
    VOLUNTEERING ("Волонтёрство"),
    HEALTH_AND_FITNESS ("Здоровье и фитнес"),
    ANIMALS ("Животные"),
    READING ("Чтение"),
    DESIGN ("Дизайн"),
    GAMING ("Игры"),
    YOGA ("Йога"),
    SELF_IMPROVEMENT ("Саморазвитие"),
    DANCING ("Танцы"),
    SURFING ("Серфинг"),
    SKIING ("Лыжи"),
    SNOWBOARDING ("Сноубординг"),
    GARDENING ("Садоводство"),
    BOARD_GAMES ("Настольные игры"),
    FISHING ("Рыбалка"),
    ASTROLOGY ("Астрология");

    private final String title;

    InterestValue(String title) {
        this.title = title;
    }

    public static InterestValue getByTitle(String title) {
        return Arrays.stream(values())
                .filter(interest -> interest.title.equals(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum constant with title " + title));
    }

}
