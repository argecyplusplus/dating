package ru.chernyukai.projects.dating.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "profile_photos")
@Entity(name = "profile_photos")
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePhoto {
    @Id
    @GeneratedValue(generator = "profile_photo_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "profile_photo_seq", sequenceName = "profile_photo_seq", allocationSize = 1)
    private Long id;


    private String link;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

}
