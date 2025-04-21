package com.booklog.booklogbackend.controller.common;

import com.booklog.booklogbackend.mapper.UsersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestControlller {

    private final UsersMapper usersMapper;

    public TestControlller(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    @GetMapping("/sample")
    public String sample() {
        log.info("test");
        return "sample";
    }
    @GetMapping("/email-exists")
    public boolean testEmail(@RequestParam String email) {
        log.debug("Checking email: {}", email);
        boolean exists = usersMapper.existsByEmail(email);
        log.debug("Email exists: {} -> {}", email, exists);
        return exists;
    }

}
