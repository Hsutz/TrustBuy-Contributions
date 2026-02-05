package com.bib.TrustBuy.system.bl.service.order.impl;

import com.bib.TrustBuy.system.bl.dto.order.NotificationDTO;
import com.bib.TrustBuy.system.bl.service.order.NotificationService;
import com.bib.TrustBuy.system.persistence.dao.order.NotificationRepository;
import com.bib.TrustBuy.system.persistence.entity.User;
import com.bib.TrustBuy.system.persistence.entity.order.Notification;
import com.bib.TrustBuy.system.persistence.entity.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public void sendNotification(User recipient, String message, Order order) {
        Notification noti = new Notification();
        noti.setRecipient(recipient);
        noti.setOrder(order);
        noti.setMessage(message);
        noti.setRead(false);
        noti.setCreatedAt(LocalDateTime.now());
        noti.setCreatedUser(recipient.getId());
        notificationRepository.save(noti);
    }

    @Override
    public List<NotificationDTO> getUserNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::toNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Integer notificationId) {
        Notification noti = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        noti.setRead(true);
        noti.setUpdatedUser(noti.getRecipient().getId());
        notificationRepository.save(noti);
    }

    // Mapper
    private NotificationDTO toNotificationDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setRecipientId(notification.getRecipient().getId());
        dto.setOrderId(notification.getOrder() != null ? notification.getOrder().getId() : null);
        return dto;
    }
}

