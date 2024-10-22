package edu.school21.tanks.repositories;

import edu.school21.tanks.models.GameStatement;

public interface GamesRepository {
    void saveResultOfGame(GameStatement gameStatement, String playerOneName, String playerTwoName);
}
