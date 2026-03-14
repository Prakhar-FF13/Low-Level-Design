package com.springmicroservice.lowleveldesignproblems.parkinglot.models;

import lombok.*;

import java.util.Objects;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Long id;

    @NonNull
    private String carNumber;

    @NonNull
    private Slot slot;
    @NonNull
    private Long inTime;

    private Long outTime;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return getId() != null && Objects.equals(getId(), ticket.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
