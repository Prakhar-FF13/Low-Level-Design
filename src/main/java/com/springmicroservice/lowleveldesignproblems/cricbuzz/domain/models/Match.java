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
public class Match {
    private Innings innings1;
    private Innings innings2;
    private Team teamA;
    private Team teamB;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;
}
