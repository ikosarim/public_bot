package me.ikosarim.cripto_bot.repos;

import me.ikosarim.cripto_bot.db_model.TradeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeHistoryRepository extends JpaRepository<TradeHistoryEntity, Integer> {
}
