package ru.chernyukai.projects.dating.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService{
    void registration(String username, String password);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
