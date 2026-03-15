package com.springmicroservice.lowleveldesignproblems.bookmyshow.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theater")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScreenEntity> screens = new ArrayList<>();
}
