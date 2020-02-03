package me.ikosarim.cripto_bot.db_model;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllStatisticEntity {

    @Id
    @Column(name = "all_statistic_id", nullable = false)
    @GeneratedValue(generator = "statistic_generator")
    @SequenceGenerator(name = "statistic_generator")
    private int allStatisticId;

    @Column(name = "currency_pair", nullable = false)
    private String currencyPair;

    @Column(name = "quantity", nullable = false)
    private double quantity;

    @Column(name = "date", nullable = false)
    private Date date;

    @OneToOne(mappedBy = "wallet_statistic", cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private WalletStatisticEntity walletStatisticEntity;

    @OneToOne(mappedBy = "open_orders_statistic", cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private OpenOrdersStatisticEntity openOrdersStatisticEntity;
}
