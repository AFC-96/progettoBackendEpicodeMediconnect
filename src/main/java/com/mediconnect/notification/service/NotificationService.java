package com.mediconnect.notification.service;

import com.mediconnect.notification.dto.NotificationDTO;
import com.mediconnect.users.entity.User;

/*
 Interfaccia che definisce il contratto per il servizio di notifica.
 Utilizzata da altri servizi (es. appuntamenti) per inviare email senza 
 doversi accoppiare con i dettagli di implementazione SMTP e Thymeleaf.
 */
public interface NotificationService {
    
    // Metodo principale per l'invio dell'email; prende il DTO con i dati e l'entità User per il salvataggio log
    void sendEmail(NotificationDTO notificationDTO, User user);
}
