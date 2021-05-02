package com.arjun.cowin.utils;

import com.arjun.cowin.domain.CowinResponse;

import java.util.Comparator;

public class CustomComparator implements Comparator<CowinResponse.VaccinationCenter> {

    @Override
    public int compare(CowinResponse.VaccinationCenter o1, CowinResponse.VaccinationCenter o2) {
        return o1.getPincode().compareTo(o2.getPincode());
    }
}
