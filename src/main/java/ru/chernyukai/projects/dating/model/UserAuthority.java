package ru.chernyukai.projects.dating.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserAuthority implements GrantedAuthority {

    DEFAULT_USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}