package com.oddsbot.services;

import com.oddsbot.constants.Messages;
import com.oddsbot.exceptions.ElementNotFoundException;
import com.oddsbot.exceptions.LineGroupNotFoundException;
import com.oddsbot.exceptions.LineNotFoundException;
import com.oddsbot.model.line.Handicap;
import com.oddsbot.model.line.Moneyline;
import com.oddsbot.model.line.Total;
import com.oddsbot.model.match.Match;
import com.oddsbot.model.match.MatchRepository;
import com.oddsbot.utils.SeleniumUtils;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.oddsbot.constants.OptibetConst.*;
import static com.oddsbot.enums.Bookmakers.OPTIBET;
import static com.oddsbot.utils.LocatorFormatUtils.formatXpath;

@Component
public class OptibetService extends BookmakerService {

    @Autowired
    public OptibetService(MatchRepository matchRepository) {
        super(matchRepository, OPTIBET);
    }

    @Override
    public CompletableFuture<Void> setup() throws ElementNotFoundException {
        driver = getDriver();
        driver.get(URL);
        acceptCookies();
        logger.info(Messages.READY);
        return CompletableFuture.allOf();
    }

    public void acceptCookies() throws ElementNotFoundException {
        SeleniumUtils.waitForElement(driver, ACCEPT_COOKIES_BUTTON).orElseThrow(ElementNotFoundException::new).click();
    }

    @Override
    public CompletableFuture<Void> addMatchesToRepo() throws ElementNotFoundException {
        driver.get(URL);
        SeleniumUtils.switchToIFrame(driver, IFRAME);
        SeleniumUtils.waitForElementToBeClickable(driver, UPCOMING_BUTTON)
                .orElseThrow(ElementNotFoundException::new)
                .click();
        while (true) {
            var matches = SeleniumUtils.waitForElements(driver, MATCHES)
                    .orElseThrow(ElementNotFoundException::new);
            for (WebElement match : matches) {
                String name = match.findElement(MATCH_NAME).getText().toLowerCase();
                String url = match.getAttribute("href");
                Match m = new Match(0, name, url, bookmaker);
                matchRepository.save(m);
            }
            try {
                driver.findElement(NEXT_PAGE_BUTTON).click();
            } catch (Exception e) {
                logger.info(Messages.GENERAL_ADDED_MATCHES);
                return CompletableFuture.allOf();
            }
        }
    }

    @Override
    public CompletableFuture<Optional<Moneyline>> getMoneyline(String team) {
        try {
            Match match = getMatch(team.toLowerCase(), bookmaker);
            goToUrl(match.getUrl());
            var moneyline = getLineCategoryElement("Match");
            var text = moneyline.getText().split("\n");
            String coef1 = text[2];
            String coef2 = text[4];
            Moneyline ml = new Moneyline(match.getTeam1(), match.getTeam2(), coef1, coef2, bookmaker);
            return CompletableFuture.completedFuture(Optional.of(ml));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Override
    public CompletableFuture<Optional<Handicap>> getHandicap(String team, String value) {
        try {
            var match = getMatch(team.toLowerCase(), bookmaker);
            goToUrl(match.getUrl());
            var handicaps = getLineCategoryElement("Handicap");
            var lines = SeleniumUtils.findElements(handicaps, formatXpath(LINE_HANDICAP, String.valueOf(Double.parseDouble(value))))
                    .orElseThrow(ElementNotFoundException::new);
            var line = getHandicapByTeam(lines, team, match).orElseThrow(LineNotFoundException::new);
            var text = line.getText();

            if (text.charAt(0) == '1') {
                team = match.getTeam1();
            } else {
                team = match.getTeam2();
            }
            String coef = text.split("\n")[1];
            Handicap hc = new Handicap(bookmaker, team, coef, value);
            return CompletableFuture.completedFuture(Optional.of(hc));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private Optional<WebElement> getHandicapByTeam(List<WebElement> lines, String team, Match match) {
        if (match.getTeam1().contains(team)) {
            return lines.stream().filter(line -> line.getText().contains("1 (")).findFirst();
        } else {
            return lines.stream().filter(line -> line.getText().contains("2 (")).findFirst();
        }
    }

    @Override
    public CompletableFuture<Optional<Total>> getTotal(String team, String value) {
        try {
            Match match = getMatch(team.toLowerCase(), bookmaker);
            goToUrl(match.getUrl());
            var totals = getLineCategoryElement("Over/Under");
            var text = getTotalLineElement(totals, value).getText();
            String coef = text.split("\n")[1];
            Total total = new Total(bookmaker, coef, value);
            return CompletableFuture.completedFuture(Optional.of(total));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private WebElement getTotalLineElement(WebElement category, String value) throws ElementNotFoundException {
        if (value.charAt(0) == 'o') {
            return SeleniumUtils.findElement(category, formatXpath(LINE_OVER, value.substring(1)))
                    .orElseThrow(ElementNotFoundException::new);
        } else {
            return SeleniumUtils.findElement(category, formatXpath(LINE_UNDER, value.substring(1)))
                    .orElseThrow(ElementNotFoundException::new);
        }
    }

    private WebElement getLineCategoryElement(String category) throws LineGroupNotFoundException,
            ElementNotFoundException {
        return SeleniumUtils.waitForElements(driver, LINE_CATEGORIES).orElseThrow(ElementNotFoundException::new)
                .stream()
                .filter(element -> element.getText().contains(category))
                .findFirst()
                .orElseThrow(() -> new LineGroupNotFoundException(category));
    }
}
