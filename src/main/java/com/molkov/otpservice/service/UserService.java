package com.molkov.otpservice.service;

import com.molkov.otpservice.dto.UserDto;
import com.molkov.otpservice.model.user.User;
import com.molkov.otpservice.model.user.UserRole;
import com.molkov.otpservice.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Используется при аутентификации
     *
     * @param username имя пользователя для поиска (должно быть уникальным в системе)
     * @return объект {@link UserDetails}
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return findByUsername(username);
    }

    /**
     * Находит пользователя в системе по его имени пользователя (username).
     *
     * @param username имя пользователя для поиска (должно быть уникальным в системе)
     * @return объект {@link User}, содержащий данные найденного пользователя
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден в системе
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(String)
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.info("Username {} not found", username);
                    return new UsernameNotFoundException("User not found");
                });
    }

    /**
     * Метод для проверки существования пользователя
     *
     * @param username имя пользователя для поиска (должно быть уникальным в системе)
     * @return {@link Boolean}
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Создает нового пользователя в системе на основе переданных данных.
     * <p>
     * Метод выполняет следующие проверки перед созданием пользователя:
     * <ul>
     *   <li>Проверяет существование пользователя с таким же username</li>
     *   <li>Проверяет, не пытается ли пользователь создать аккаунт с ролью ADMIN</li>
     * </ul>
     * При успешном прохождении проверок создается новый пользователь с зашифрованным паролем.
     * Метод выполняется в транзакции для обеспечения целостности данных.
     *
     * @param userDto объект с данными для создания пользователя, включая username, password и др.
     * @return созданный и сохраненный в базе данных объект {@link User}
     * @throws EntityExistsException если пользователь с таким username уже существует
     * @throws EntityExistsException если пользователь пытается создать аккаунт с ролью ADMIN
     */
    @Transactional
    public User create(UserDto userDto) {
        if(existsByUsername(userDto.username())) {
            log.info("Username {} already exists", userDto.username());
            throw new EntityExistsException("User already exists");
        }

        if (userDto.role().equals(UserRole.ADMIN)) {
            log.info("Username {} already exists", userDto.username());
            throw new EntityExistsException("Admin already exists");
        }

        User user = User.builder()
                .password(passwordEncoder.encode(userDto.password()))
                .username(userDto.username())
                .role(userDto.role())
                .phoneNumber(userDto.phoneNumber())
                .email(userDto.email())
                .telegramId(userDto.telegramId())
                .build();

        log.info("User: {} created", user);
        return userRepository.save(user);
    }

    public User getAuthorizedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findByRole(UserRole.USER);
        return users.stream()
                .map(user -> UserDto.builder()
                            .username(user.getUsername())
                            .telegramId(user.getTelegramId())
                            .email(user.getEmail())
                            .phoneNumber(user.getPhoneNumber())
                            .build())
                .toList();
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
