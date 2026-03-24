package results;

import results.GameResult;

import java.util.Collection;

public record ListGamesResult(Collection<GameResult> games) {
}
