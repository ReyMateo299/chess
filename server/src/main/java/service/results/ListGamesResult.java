package service.results;

import service.results.GameResult;

import java.util.Collection;
import java.util.HashMap;

public record ListGamesResult(Collection<GameResult> games) {
}
