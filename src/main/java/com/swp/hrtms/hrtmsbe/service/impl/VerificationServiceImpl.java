package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.response.JockeyVerificationRequestResponse;
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final NotificationRecipientRepository notificationRecipientRepository;
    private final JockeyCertRepository jockeyCertRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JockeyVerificationRequestResponse> getJockeyVerificationRequests(Integer recipientId) {
        List<NotificationRecipient> recipients = notificationRecipientRepository.findPendingVerificationRequests(recipientId);
        List<JockeyVerificationRequestResponse> responses = new ArrayList<>();

        for (NotificationRecipient nr : recipients) {
            org.hibernate.Hibernate.initialize(nr.getNotification().getSender());
            Object unproxiedSender = org.hibernate.Hibernate.unproxy(nr.getNotification().getSender());
            if (unproxiedSender instanceof Jockey) {
                Jockey jockey = (Jockey) unproxiedSender;
                List<String> pendingCerts = jockeyCertRepository.findPendingCertificateNamesByJockeyId(jockey.getId());

                if (!pendingCerts.isEmpty()) {
                    JockeyVerificationRequestResponse response = JockeyVerificationRequestResponse.builder()
                            .notificationId(nr.getNotification().getId())
                            .jockeyId(jockey.getId())
                            .jockeyName(jockey.getJockeyName())
                            .pendingCertificates(pendingCerts)
                            .build();
                    responses.add(response);
                }
            }
        }

        return responses;
    }
}
