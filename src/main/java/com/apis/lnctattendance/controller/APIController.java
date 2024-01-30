package com.apis.lnctattendance.controller;

import com.apis.lnctattendance.model.OverallStatistics;
import com.apis.lnctattendance.model.StudentInfo;
import com.apis.lnctattendance.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {

    @Autowired
    APIService apiService;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @GetMapping("getOverallAttendance/user-{username}-{password}")
    public OverallStatistics getOverallAttendance(@PathVariable String username, @PathVariable String password) {
        if (username.isEmpty() && password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!");
        } else {
            return apiService.getOverallAttendance(username, password)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!"));
        }
    }

    @GetMapping("getStudentInformation/user-{username}-{password}")
    public StudentInfo getStudentInformation(@PathVariable String username, @PathVariable String password) {
        if (username.isEmpty() && password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found!");
        } else {
            return apiService.getStudentInformation(username, password)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found!"));
        }
    }
}
