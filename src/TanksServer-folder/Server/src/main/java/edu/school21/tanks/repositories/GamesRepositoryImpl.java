package edu.school21.tanks.repositories;

import edu.school21.tanks.models.GameStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("GamesRepositoryImpl")
public class GamesRepositoryImpl implements GamesRepository {
    private NamedParameterJdbcTemplate jdbcTemplate;

    public GamesRepositoryImpl() {

    }

    @Autowired
    public void GamesRepositoryJdbcTemplateImpl(
            @Qualifier("driverManagerDataSource") DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void saveResultOfGame(GameStatement gameStatement, String playerOneName, String playerTwoName) {
        String saveQuery = "INSERT INTO games (p1name, p2name, p1total, p2total, p1strike, p2strike, p1missing, p2missing) VALUES (:p1name, :p2name, :p1total, :p2total, :p1strike, :p2strike, :p1missing, :p2missing)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("p1name", playerOneName);
        params.addValue("p2name", playerTwoName);
        params.addValue("p1total", gameStatement.getPlayerOneTotalShot());
        params.addValue("p2total", gameStatement.getPlayerTwoTotalShot());
        params.addValue("p1strike", (100 - gameStatement.getHpPlayer2()) / 5);
        params.addValue("p2strike", (100 - gameStatement.getHpPlayer1()) / 5);
        params.addValue("p1missing", gameStatement.getPlayerOneMissingShot());
        params.addValue("p2missing", gameStatement.getPlayerTwoMissingShot());

        if (jdbcTemplate.update(saveQuery, params) == 0) {
            System.err.println("Error saving game");
        }
    }
}
