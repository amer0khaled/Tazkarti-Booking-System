package org.amerkhaled.eventservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketType ticketType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TicketStatus ticketStatus = TicketStatus.AVAILABLE;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int quantity;   // Total for this ticket type

    @Column(nullable = false)
    private int remainingQuantity; // updated when booked/cancelled

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Domain Logic ---
    public void reserve(int count) {
        if (remainingQuantity < count) {
            throw new IllegalStateException("Not enough tickets available");
        }
        remainingQuantity -= count;
        ticketStatus = remainingQuantity == 0 ? TicketStatus.BOOKED : TicketStatus.RESERVED;
    }

    public void release(int count) {
        remainingQuantity += count;
        ticketStatus = TicketStatus.AVAILABLE;
    }

    protected void setEvent(Event event) {
        this.event = event;
    }
}
