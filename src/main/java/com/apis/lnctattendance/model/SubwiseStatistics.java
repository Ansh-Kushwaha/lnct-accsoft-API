package com.apis.lnctattendance.model;

public record SubwiseStatistics(
        String sub,
        Integer classes,
        Integer presCount,
        Integer absCount
) {
}
