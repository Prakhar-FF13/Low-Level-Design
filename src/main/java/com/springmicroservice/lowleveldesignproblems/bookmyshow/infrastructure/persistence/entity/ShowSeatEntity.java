package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity;

import com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "show_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private SeatEntity seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private ShowEntity show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private TicketEntity ticket;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Version
    private Long version;
}
