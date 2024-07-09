package com.apis.lnctattendance.model;

public record OverallStatistics(
        Integer totalClasses,
        Integer present,
        Double percentage,
        Integer absent
) {
}
