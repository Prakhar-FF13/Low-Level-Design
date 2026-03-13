package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Innings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int runs;
    private int wickets;
    private int oversRemaining;
    private boolean active;
}
