package com.arjun.cowin.service;

import com.arjun.cowin.domain.CowinResponse;
import com.arjun.cowin.http.RestClient;
import com.arjun.cowin.utils.CustomComparator;
import com.arjun.cowin.utils.EmailService;
import com.arjun.cowin.utils.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class VaccineLocatorService {

    private Logger log = LoggerFactory.getLogger(VaccineLocatorService.class);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
    private RestClient restClient;
    private EmailService emailService;

    public VaccineLocatorService(RestClient restClient, EmailService emailService) {
        this.restClient = restClient;
        this.emailService = emailService;
    }

    public void notifyIfAvailable(List<String> pinCodes, List<String> recipientList) {
        List<CowinResponse.VaccinationCenter> availableVaccinationCenters = getVaccinationCenterForAbove18(pinCodes);
        if (CollectionUtils.isNotEmpty(availableVaccinationCenters)) {
            emailService.sendMail(availableVaccinationCenters, recipientList);
        }
    }

    private List<CowinResponse.VaccinationCenter> getVaccinationCenterForAbove18(List<String> pinCodes) {
        List<CowinResponse.VaccinationCenter> availableCenters = new ArrayList<>();
        pinCodes.forEach(pinCode -> {
            for (int day = 1; day <= 7; day++) {
                CowinResponse cowinResponse = getVaccinationCentersByPinCode(pinCode, LocalDate.now().plusDays(day));
                if (cowinResponse != null && CollectionUtils.isNotEmpty(cowinResponse.getSessions())) {
                    List<CowinResponse.VaccinationCenter> vaccinationCenters = cowinResponse.getSessions();
                    availableCenters.addAll(vaccinationCenters.stream()
                            .filter(vaccinationCenter -> vaccinationCenter.getMin_age_limit() == 18 && vaccinationCenter.getAvailable_capacity() > 5)
                            .collect(Collectors.toList()));
                }
            }
        });
        Collections.sort(availableCenters, new CustomComparator());
        return availableCenters;
    }


    private CowinResponse getVaccinationCentersByDistrict(String districtId, LocalDate businessDate) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("date", formatter.format(businessDate));
        queryParams.put("district_id", districtId);
        String response = restClient.get("/findByDistrict", queryParams);
        CowinResponse cowinResponse = JsonUtil.toObject(response, CowinResponse.class);
        return cowinResponse;
    }

    private CowinResponse getVaccinationCentersByPinCode(String pinCode, LocalDate businessDate) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("date", formatter.format(businessDate));
        queryParams.put("pincode", pinCode);
        String response = restClient.get("/findByPin", queryParams);
        CowinResponse cowinResponse = JsonUtil.toObject(response, CowinResponse.class);
        return cowinResponse;
    }
}
