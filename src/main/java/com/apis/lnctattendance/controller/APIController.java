package com.apis.lnctattendance.controller;

import com.apis.lnctattendance.model.OverallStatistics;
import com.apis.lnctattendance.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/getAttendance")
public class APIController {

    @Autowired
    APIService apiService;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @GetMapping("/overall/user-{username}-{password}")
    public OverallStatistics getOverallAttendance(@PathVariable String username, @PathVariable String password) {
        if (username.isEmpty() && password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!");
        } else {
            return apiService.getOverallAttendance(username, password);
        }
    }
}
