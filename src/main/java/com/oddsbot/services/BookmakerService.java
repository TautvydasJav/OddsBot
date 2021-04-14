package com.oddsbot.services;

import com.oddsbot.enums.Bookmakers;
import com.oddsbot.exceptions.ElementNotFoundException;
import com.oddsbot.exceptions.LineGroupNotFoundException;
import com.oddsbot.exceptions.MatchNotFoundException;
import com.oddsbot.model.BrowserProvider;
import com.oddsbot.model.line.Handicap;
import com.oddsbot.model.line.Moneyline;
import com.oddsbot.model.line.Total;
import com.oddsbot.model.match.Match;
import com.oddsbot.model.match.MatchRepository;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class BookmakerService {

    protected BookmakerService(MatchRepository matchRepository, Bookmakers bookmaker) {
        this.matchRepository = matchRepository;
        this.bookmaker = bookmaker;
    }

    protected static final Logger logger = LoggerFactory.getLogger(BookmakerService.class);
    protected final MatchRepository matchRepository;
    protected ChromeDriver driver;
    protected Bookmakers bookmaker;

    public ChromeDriver getDriver() {
        if (driver == null) {
            driver = BrowserProvider.createDriver();
        }
        return driver;
    }

    @Async
    public abstract CompletableFuture<Void> setup() throws ElementNotFoundException;

    @Async
    public abstract CompletableFuture<Void> addMatchesToRepo() throws InterruptedException, ElementNotFoundException;

    @Async
    public abstract CompletableFuture<Optional<Moneyline>> getMoneyline(String team) throws LineGroupNotFoundException,
            ElementNotFoundException, MatchNotFoundException;

    @Async
    public abstract CompletableFuture<Optional<Handicap>> getHandicap(String team, String value)
            throws LineGroupNotFoundException, ElementNotFoundException, MatchNotFoundException;

    @Async
    public abstract CompletableFuture<Optional<Total>> getTotal(String team, String value) throws LineGroupNotFoundException,
            ElementNotFoundException, MatchNotFoundException;

    @PreDestroy
    private void destroy() {
        driver.quit();
    }

    public Bookmakers getBookmaker() {
        return bookmaker;
    }

    protected void goToUrl(String url) {
        if (!url.equals(driver.getCurrentUrl())) driver.get(url);
    }

    protected Match getMatch(String team, Bookmakers bookmaker) throws MatchNotFoundException {
        return matchRepository.findFirstByNameContainingAndBookmaker(team.toLowerCase(), bookmaker)
                .orElseThrow(MatchNotFoundException::new);
    }

    protected <T> CompletableFuture<Optional<T>> handleException(Exception e) {
        logger.info(e.getMessage());
        Thread.currentThread().interrupt();
        return CompletableFuture.completedFuture(Optional.empty());
    }
}
