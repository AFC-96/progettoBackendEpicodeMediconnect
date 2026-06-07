package com.mediconnect.notification.repo;

import com.mediconnect.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 Interfaccia che estende JpaRepository per gestire il salvataggio dei log di notifica nel DB.
 Non contiene metodi personalizzati perché il sistema attualmente richiede solo
 l'inserimento (save) delle notifiche, che è già fornito di default.
 */
public interface NotificationRepo extends JpaRepository<Notification, Long> {
}
