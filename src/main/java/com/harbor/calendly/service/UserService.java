package com.harbor.calendly.service;

import com.harbor.calendly.dao.UserRepository;
import com.harbor.calendly.entities.User;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.errors.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        try {
            userRepository.save(user);
        } catch(DataIntegrityViolationException ex) {
            logger.atError()
                    .setMessage("failed to create user")
                    .setCause(ex)
                    .log();
            if (ex.getMessage().toLowerCase().contains("unique index")) {
                throw new UserException(ErrorCode.USER_ALREADY_EXISTS, ex, "user email already exists");
            } else {
                throw new UserException(ErrorCode.UNKNOWN_ERROR, ex, "unidentified db constraint exception");
            }
        }
        return user;
    }

    public User getUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserException(ErrorCode.USER_NOT_EXISTS, "user with id : "+ userId+" does not exists"));
    }

}
