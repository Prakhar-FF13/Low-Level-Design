package com.springmicroservice.lowleveldesignproblems.cricbuzz.domain.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    @OneToOne(cascade = CascadeType.ALL)
    private Innings innings1;
    @OneToOne(cascade = CascadeType.ALL)
    private Innings innings2;
    @ManyToOne(cascade = CascadeType.ALL)
    private Team teamA;
    @ManyToOne(cascade = CascadeType.ALL)
    private Team teamB;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;
}
