package com.teamvoy.teamvoytestasignment.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public final class LineItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private GoodEntity good;
    @Column(nullable = false)
    private Double linePrice;
    @Column(nullable = false)
    private Integer lineQuantity;
}
