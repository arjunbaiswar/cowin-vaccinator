package com.arjun.cowin.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CowinResponse {

    private List<VaccinationCenter> sessions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VaccinationCenter {
        private String name;
        private String fee;
        private String fee_type;
        private int available_capacity;
        private int min_age_limit;
        private String vaccine;
        private String state_name;
        private String district_name;
        private Integer pincode;
        private String date;
    }
}
