package com.apis.lnctattendance.controller;

import com.apis.lnctattendance.model.DatewiseStatistics;
import com.apis.lnctattendance.model.OverallStatistics;
import com.apis.lnctattendance.model.StudentInfo;
import com.apis.lnctattendance.model.SubwiseStatistics;
import com.apis.lnctattendance.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {

    @Autowired
    private APIService apiService;

    @GetMapping("/login/user={username}-pass={password}")
    public ResponseStatusException login(@PathVariable String username, @PathVariable String password) {
        if (apiService.getLoginStatus(username, password)) {
            return new ResponseStatusException(HttpStatus.OK, "success");
        } else {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "not_found");
        }
    }

    @GetMapping("/studentInformation/user={username}-pass={password}")
    public StudentInfo getStudentInformation(@PathVariable String username, @PathVariable String password) {
        if (username.isEmpty() && password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found!");
        } else {
            return apiService.getStudentInformation(username, password)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found!"));
        }
    }

    @GetMapping("/overallAttendance/user={username}-pass={password}")
    public OverallStatistics getOverallAttendance(@PathVariable String username, @PathVariable String password) {
        if (username.isEmpty() && password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!");
        } else {
            return apiService.getOverallAttendance(username, password)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!"));
        }
    }

    @GetMapping("/subwiseAttendance/user={username}-pass={password}")
    public List<SubwiseStatistics> getSubjectWiseAttendance(@PathVariable String username, @PathVariable String password) {
        if (username.isEmpty() && password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!");
        } else {
            return apiService.getSubjectWiseAttendance(username, password)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!"));
        }
    }

    @GetMapping("/datewiseAttendance/user={username}-pass={password}")
    public List<DatewiseStatistics> getDateWiseAttendance(@PathVariable String username, @PathVariable String password) {
        if (username.isEmpty() && password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!");
        } else {
            return apiService.getDatewiseAttendance(username, password)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found!"));
        }
    }
}
