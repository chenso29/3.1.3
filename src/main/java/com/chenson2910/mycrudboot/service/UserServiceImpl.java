package com.chenson2910.mycrudboot.service;

import com.chenson2910.mycrudboot.model.Role;
import com.chenson2910.mycrudboot.model.User;
import com.chenson2910.mycrudboot.repository.RoleRepository;
import com.chenson2910.mycrudboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service()
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public boolean save(User user) {
        User userFromDB = userRepository.findByEmail(user.getEmail());

        if (userFromDB != null) {
            return false;
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        throw new UserNotFoundException("Couldn't find any users with ID " + id);

    }

    public void delete(Integer id) throws UserNotFoundException {
        Long count = userRepository.countById(id);
        if (count == null || count == 0) {
            throw new UserNotFoundException("Couldn't find any users with ID " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public void updateUser(User user) throws UserNotFoundException {
        user.setPassword(user.getPassword().isEmpty() ? // todo ???????? ?????? ???????????? ?????????? try
                get(user.getId()).getPassword() :
                bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);


    }
}
