package com.bib.TrustBuy.system.bl.service.order;

import com.bib.TrustBuy.system.bl.dto.order.NotificationDTO;
import com.bib.TrustBuy.system.persistence.entity.User;
import com.bib.TrustBuy.system.persistence.entity.order.Notification;
import com.bib.TrustBuy.system.persistence.entity.order.Order;

import java.util.List;

public interface NotificationService {
    void sendNotification(User recipient, String message, Order order);

    List<NotificationDTO> getUserNotifications(Integer userId);

    void markAsRead(Integer notificationId);
}

