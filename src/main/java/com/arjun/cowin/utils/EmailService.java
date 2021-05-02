package com.arjun.cowin.utils;

import com.arjun.cowin.config.AppProperties;
import com.arjun.cowin.domain.CowinResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class EmailService {

    private Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendMail(List<CowinResponse.VaccinationCenter> availableVaccinationCenters, List<String> recipientList) {

        final String username = AppProperties.INSTANCE.getString("mail.username");
        final String password = AppProperties.INSTANCE.getString("mail.password");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        try {
            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
            String messageBody = getMessageBody(availableVaccinationCenters);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(StringUtils.join(recipientList, ","))
            );

            message.setSubject("Available vaccination centers for 18 above");
            message.setContent(messageBody, "text/html; charset=utf-8");
            logger.info("Message sent successfully");
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }
    }

    public String getMessageBody(List<CowinResponse.VaccinationCenter> availableVaccinationCenters) {
        String messageBody =
                "<html><body><table width='100%' border='1' align='center'>"
                        + "<tr align='center'>"
                        + "<td><b>Name<b></td>"
                        + "<td><b>Pincode<b></td>"
                        + "<td><b>Date<b></td>"
                        + "<td><b>Vaccine<b></td>"
                        + "<td><b>Fee<b></td>"
                        + "<td><b>Fee Type<b></td>"
                        + "<td><b>Available Capacity<b></td>"
                        + "</tr>";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageBody);
        availableVaccinationCenters.forEach(vaccinationCenter -> {
            stringBuilder.append("<tr align='center'>"
                    + "<td>" + vaccinationCenter.getName() + "</td>"
                    + "<td>" + vaccinationCenter.getPincode() + "</td>"
                    + "<td>" + vaccinationCenter.getDate() + "</td>"
                    + "<td>" + vaccinationCenter.getVaccine() + "</td>"
                    + "<td>" + vaccinationCenter.getFee() + "</td>"
                    + "<td>" + vaccinationCenter.getFee_type() + "</td>"
                    + "<td>" + vaccinationCenter.getAvailable_capacity() + "</td>"
                    + "</tr>");
        });
        stringBuilder.append("</table> <br><br>");
        stringBuilder.append("Thanks and Regards <br> Arjun Baiswar<br> </body> </html>");
        return stringBuilder.toString();
    }

}