package com.booklog.booklogbackend.controller.common;

import com.booklog.booklogbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestControlller {

    private final UserMapper userMapper;

    public TestControlller(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/sample")
    public String sample() {
        log.info("test");
        return "sample";
    }
    @GetMapping("/email-exists")
    public boolean testEmail(@RequestParam String email) {
        log.debug("Checking email: {}", email);
        boolean exists = userMapper.existsByEmail(email);
        log.debug("Email exists: {} -> {}", email, exists);
        return exists;
    }

}
