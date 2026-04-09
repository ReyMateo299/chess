package results;

import model.GameData;

import java.util.Collection;

public record GetGamesResult(Collection<GameData> games) {
}
