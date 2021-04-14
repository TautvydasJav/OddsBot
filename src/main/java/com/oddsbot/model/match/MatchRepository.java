package com.oddsbot.model.match;

import com.oddsbot.enums.Bookmakers;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MatchRepository extends CrudRepository<Match, Integer> {
    Optional<Match> findFirstByNameContainingAndBookmaker(String name, Bookmakers bookmaker);
}
