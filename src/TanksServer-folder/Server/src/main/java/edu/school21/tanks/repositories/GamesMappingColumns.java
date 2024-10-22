package edu.school21.tanks.repositories;

import edu.school21.tanks.models.GameStatement;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GamesMappingColumns implements RowMapper<GamesRepository> {

    @Override
    public GamesRepository mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }

}
