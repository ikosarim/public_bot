package me.ikosarim.cripto_bot.repos;

import me.ikosarim.cripto_bot.db_model.AllStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface AllStatisticRepository extends JpaRepository<AllStatisticEntity, Date> {
}
