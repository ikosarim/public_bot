package me.ikosarim.cripto_bot.db_model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name = "wallet_statistic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletStatisticEntity {

    @Column(name = "quantity", nullable = false)
    private double quantity;

    @OneToOne
    @JoinColumn(name = "all_statistic", nullable = false)
    private AllStatisticEntity allStatisticEntity;
}
