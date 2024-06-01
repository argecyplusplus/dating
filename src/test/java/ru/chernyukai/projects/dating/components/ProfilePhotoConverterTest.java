package ru.chernyukai.projects.dating.components;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import ru.chernyukai.projects.dating.model.ProfilePhoto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfilePhotoConverterTest {

    @Test
    void convertProfilePhotosToMultipartFiles_ConvertsProfilePhotosToMultipartFiles() throws IOException {
        // Создаем список ProfilePhoto
        List<ProfilePhoto> profilePhotos = new ArrayList<>();
        profilePhotos.add(new ProfilePhoto(1L, "photo1.jpg", "image/jpeg", new byte[]{1, 2, 3}, null));
        profilePhotos.add(new ProfilePhoto(2L, "photo2.jpg", "image/jpeg", new byte[]{4, 5, 6},null));

        // Вызываем метод для конвертации
        ProfilePhotoConverter converter = new ProfilePhotoConverter();
        List<MultipartFile> multipartFiles = converter.convertProfilePhotosToMultipartFiles(profilePhotos);

        // Проверяем результат
        assertEquals(2, multipartFiles.size());

        // Проверяем содержимое первого MultipartFile
        MultipartFile firstFile = multipartFiles.get(0);
        assertEquals("photo1.jpg", firstFile.getName());
        assertEquals("photo1.jpg", firstFile.getOriginalFilename());
        assertEquals("image/jpeg", firstFile.getContentType());
        assertArrayEquals(new byte[]{1, 2, 3}, firstFile.getBytes());

        // Проверяем содержимое второго MultipartFile
        MultipartFile secondFile = multipartFiles.get(1);
        assertEquals("photo2.jpg", secondFile.getName());
        assertEquals("photo2.jpg", secondFile.getOriginalFilename());
        assertEquals("image/jpeg", secondFile.getContentType());
        assertArrayEquals(new byte[]{4, 5, 6}, secondFile.getBytes());
    }
}