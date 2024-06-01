package ru.chernyukai.projects.dating.components;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.chernyukai.projects.dating.model.ProfilePhoto;

import java.util.List;
import java.util.stream.Collectors;

public class ProfilePhotoConverter {

    public List<MultipartFile> convertProfilePhotosToMultipartFiles(List<ProfilePhoto> profilePhotos) {
        return profilePhotos.stream()
                .map(profilePhoto -> new CustomMultipartFile(
                        profilePhoto.getFileName(),
                        profilePhoto.getFileName(),
                        profilePhoto.getFileType(),
                        profilePhoto.getData()
                ))
                .collect(Collectors.toList());
    }
}