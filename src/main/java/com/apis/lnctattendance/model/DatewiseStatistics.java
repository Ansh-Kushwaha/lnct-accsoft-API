package com.apis.lnctattendance.model;

public record DatewiseStatistics(
        String date,
        Integer periodNo,
        String sub,
        Character status
) {
}
