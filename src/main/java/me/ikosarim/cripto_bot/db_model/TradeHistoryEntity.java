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
public class TradeHistoryEntity {

    @Id
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "currency_pair", nullable = false)
    private String currencyPair;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "quantity", nullable = false)
    private double quantity;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "trade_type", nullable = false)
    private String tradeType;
}
