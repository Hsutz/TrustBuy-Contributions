package com.bib.TrustBuy.system.bl.dto.order;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDTO {
    private Integer id;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private Integer recipientId;
    private Integer orderId;
}

