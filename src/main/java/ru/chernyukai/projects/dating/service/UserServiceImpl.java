package ru.chernyukai.projects.dating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.chernyukai.projects.dating.exceptions.UsernameAlreadyExistsException;
import ru.chernyukai.projects.dating.model.User;
import ru.chernyukai.projects.dating.model.UserAuthority;
import ru.chernyukai.projects.dating.model.UserRole;
import ru.chernyukai.projects.dating.repository.UserRepository;
import ru.chernyukai.projects.dating.repository.UserRolesRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRolesRepository userRolesRepository;
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registration(String username, String password) throws UsernameAlreadyExistsException, IllegalArgumentException {
        if (userRepository.findByUsername(username).isEmpty()) {

            if (username == null || password == null ){
                throw new IllegalArgumentException();
            }

            User user = userRepository.save(
                    new User()
                            .setId(null)
                            .setUsername(username)
                            .setPassword(passwordEncoder.encode(password))
                            .setLocked(false)
                            .setExpired(false)
                            .setEnabled(true)

            );
            userRolesRepository.save(new UserRole(null, UserAuthority.DEFAULT_USER, user));
        }
        else {
            throw new UsernameAlreadyExistsException();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
