package com.bib.TrustBuy.system.persistence.dao.order;

import com.bib.TrustBuy.system.persistence.entity.order.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Integer recipientId);
}
