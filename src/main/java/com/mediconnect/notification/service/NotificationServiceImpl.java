package com.mediconnect.notification.service;

import com.mediconnect.enums.NotificationType;
import com.mediconnect.notification.dto.NotificationDTO;
import com.mediconnect.notification.entity.Notification;
import com.mediconnect.notification.repo.NotificationRepo;
import com.mediconnect.users.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
// Implementazione asincrona servizio notifiche: compone email con Thymeleaf, invia via SMTP, salva nel DB

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    // Repository per le notifiche
    private final NotificationRepo notificationRepo;
    // Client per l'invio email via SMTP
    private final JavaMailSender mailSender;
    // Dipendenza: TemplateEngine
    private final TemplateEngine templateEngine;

    // Compone e invia la notifica email
    @Override
    // Eseguito in modo asincrono su un thread separato
    @Async
    public void sendEmail(NotificationDTO notificationDTO, User user) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(notificationDTO.getRecipient());
            helper.setSubject(notificationDTO.getSubject());

            // Utilizza il template se fornito
            if (notificationDTO.getTemplateName() != null) {

                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);

                helper.setText(htmlContent, true);

            } else {
                helper.setText(notificationDTO.getMessage(), true);
            }

            mailSender.send(mimeMessage);
            log.info("Email sent out");

            // Salva nel database
            Notification notificationToSave = Notification.builder()
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .message(notificationDTO.getMessage())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();

            notificationRepo.save(notificationToSave);

        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }
}
