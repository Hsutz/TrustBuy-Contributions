package com.bib.TrustBuy.system.persistence.entity.order;

import com.bib.TrustBuy.system.persistence.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String message;

    @Column(name = "is_read")
    private boolean read = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Integer createdUser;

    private Integer updatedUser;
}

