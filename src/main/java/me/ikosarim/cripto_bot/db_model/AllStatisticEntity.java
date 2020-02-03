package me.ikosarim.cripto_bot.db_model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllStatisticEntity {

    @Id
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "currency_pair", nullable = false)
    private String currencyPair;

    @Column(name = "wallet_quantity", nullable = false)
    private String walletQuantity;

    @Column(name = "open_orders_quantity", nullable = false)
    private String openOrdersQuantity;
}
