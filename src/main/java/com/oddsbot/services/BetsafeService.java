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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.oddsbot.constants.BetsafeConst.*;
import static com.oddsbot.enums.Bookmakers.BETSAFE;
import static com.oddsbot.utils.LocatorFormatUtils.formatCss;
import static com.oddsbot.utils.LocatorFormatUtils.formatXpath;

@Component
public class BetsafeService extends BookmakerService {

    @Autowired
    public BetsafeService(MatchRepository matchRepository) {
        super(matchRepository, BETSAFE);
    }

    @Override
    public CompletableFuture<Void> setup() throws ElementNotFoundException {
        driver = getDriver();
        driver.get(URL);
        acceptCookies();
        logger.info(Messages.READY);
        return CompletableFuture.allOf();
    }

    @Override
    public CompletableFuture<Void> addMatchesToRepo() throws ElementNotFoundException {
        driver.get(URL);
        var basketball = SeleniumUtils.waitForElement(driver, BASKETBALL)
                .orElseThrow(ElementNotFoundException::new);
        var leagueUrls = getLeagueUrls(basketball);
        try {
            addFirstLeaguesMatchesToRepo(leagueUrls);
        } catch (Exception e) {
            handleException(e);
        }
        for (var url : leagueUrls) {
            try {
                var body = SeleniumUtils.waitForElement(driver, LEAGUE_BODY)
                        .orElseThrow(ElementNotFoundException::new);
                var leagueButton = SeleniumUtils.waitForElement(driver, formatCss(LEAGUE_BUTTON_BY_HREF, url))
                        .orElseThrow(ElementNotFoundException::new);
                SeleniumUtils.clickJsButton(driver, leagueButton);
                SeleniumUtils.waitForElementToGoStale(driver, body);
                addLeagueMatchesToRepo();
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }
        logger.info(Messages.GENERAL_ADDED_MATCHES);
        return CompletableFuture.allOf();
    }

    private void addFirstLeaguesMatchesToRepo(List<String> leagueUrls) throws ElementNotFoundException {
        var button = SeleniumUtils.waitForElement(driver, formatCss(LEAGUE_BUTTON_BY_HREF, leagueUrls.get(0)))
                .orElseThrow(ElementNotFoundException::new);
        SeleniumUtils.clickJsButton(driver, button);
        addLeagueMatchesToRepo();
        leagueUrls.remove(0);
    }

    @Override
    public CompletableFuture<Optional<Moneyline>> getMoneyline(String team) {
        try {
            var match = getMatch(team.toLowerCase(), bookmaker);
            goToUrl(match.getUrl());
            var moneylineElement = getLineGroupElement(GROUP_MONEYLINE);
            var line = SeleniumUtils.findElement(moneylineElement, MONEYLINE_TEXT)
                    .orElseThrow(LineNotFoundException::new);
            var lineText = line.getText().toLowerCase().split("\n");
            String team1 = lineText[0];
            String coef1 = lineText[1];
            String team2 = lineText[2];
            String coef2 = lineText[3];
            Moneyline ml = new Moneyline(team1, team2, coef1, coef2, BETSAFE);
            return CompletableFuture.completedFuture(Optional.of(ml));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Override
    public CompletableFuture<Optional<Handicap>> getHandicap(String team, String value) {
        try {
            Match match = getMatch(team.toLowerCase(), bookmaker);
            goToUrl(match.getUrl());
            var handicapsElement = getLineGroupElement(GROUP_HANDICAPS);
            if (value.charAt(0) == '+') {
                value = value.substring(1);
            }
            var handicaps = SeleniumUtils.findElements(handicapsElement, formatCss(HANDICAP_TEXT, value))
                    .orElseThrow(LineNotFoundException::new);
            var line = getHandicapByTeam(handicaps, team).orElseThrow(LineNotFoundException::new);
            var lineText = line.getText().split("\n");
            var teamName = lineText[0].substring(0, lineText[0].indexOf("(") - 1).toLowerCase();
            var hcValue = lineText[0].substring(lineText[0].indexOf("(") + 1, lineText[0].length() - 1);
            var coef = lineText[1];
            Handicap hc = new Handicap(BETSAFE, teamName, coef, hcValue);
            return CompletableFuture.completedFuture(Optional.of(hc));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Override
    public CompletableFuture<Optional<Total>> getTotal(String team, String value) {
        try {
            var match = getMatch(team.toLowerCase(), bookmaker);
            goToUrl(match.getUrl());
            var totalsElement = getLineGroupElement(GROUP_TOTALS);
            var totals = SeleniumUtils
                    .findElements(totalsElement, formatCss(TOTAL_TEXT, value.substring(1)))
                    .orElseThrow(() -> new LineGroupNotFoundException(Messages.GENERAL_TOTALS));
            String coef;
            if (value.charAt(0) == 'o') {
                coef = totals.get(0).getText().split("\n")[1];

            } else {
                coef = totals.get(1).getText().split("\n")[1];
            }
            var total = new Total(BETSAFE, coef, value);
            return CompletableFuture.completedFuture(Optional.of(total));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private WebElement getLineGroupElement(String lineGroup) throws ElementNotFoundException {
        return SeleniumUtils.waitForElement(driver, formatXpath(LINE_GROUP, lineGroup))
                .orElseThrow(ElementNotFoundException::new);
    }

    private void addLeagueMatchesToRepo() throws ElementNotFoundException {
        var matches = SeleniumUtils.waitForElements(driver, MATCH_INFO)
                .orElseThrow(ElementNotFoundException::new);
        for (var m : matches) {
            var teams = m.findElements(MATCH_TEAMS);
            var team1 = teams.get(0).getText().toLowerCase();
            var team2 = teams.get(1).getText().toLowerCase();
            var url = m.findElement(By.tagName("a")).getAttribute("href");
            url = url.substring(0, 18) + "/en" + url.substring(18);
            matchRepository.save(new Match(0, team1, team2, url, bookmaker));
        }
    }


    private List<String> getLeagueUrls(WebElement basketball) {
        List<String> leagueUrls = new ArrayList<>();
        var elements = basketball.findElements(LEAGUES);
        for (var element : elements) {
            var url = element.getAttribute("href");
            String temp = url.replace("/", "");
            if (url.length() - temp.length() == 7 && !url.contains("ilgalaik")
                    && !url.contains("futures") && !url.contains("winner") && !url.contains("mvp")
                    && !url.contains("weekend")) {
                url = URLDecoder.decode(url.substring(22), StandardCharsets.UTF_8);
                leagueUrls.add(url);
            }
        }
        return leagueUrls;
    }

    private void acceptCookies() throws ElementNotFoundException {
        SeleniumUtils.waitForElement(driver, ACCEPT_COOKIES_BUTTON).orElseThrow(ElementNotFoundException::new).click();
        SeleniumUtils.waitForElement(driver, CLOSE_COOKIES_BAR).orElseThrow(ElementNotFoundException::new).click();
    }

    private Optional<WebElement> getHandicapByTeam(List<WebElement> handicaps, String team) {
        return handicaps.stream()
                .filter(hc -> hc.getText().toLowerCase().contains(team))
                .findFirst();
    }

}
