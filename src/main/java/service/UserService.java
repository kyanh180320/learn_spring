package service;

import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> findAll(){

    }
}
