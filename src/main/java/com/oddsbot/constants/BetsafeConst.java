package com.oddsbot.constants;

import org.openqa.selenium.By;

public class BetsafeConst {

    private BetsafeConst() {
    }

    public static final String URL = "https://betsafe.lt/en/betting";
    public static final By ACCEPT_COOKIES_BUTTON = By.cssSelector("button[id=gdpr-snackbar-accept]");
    public static final By CLOSE_COOKIES_BAR = By.cssSelector(".close.disable-notice-display");
    public static final By BASKETBALL = By.xpath("//a[@href='/en/betting/basketball']/..");
    public static final By LEAGUES = By.cssSelector(".wpm-menu-item__title.item");
    public static final String LEAGUE_BUTTON_BY_HREF = "a[href='%s']";
    public static final By MATCH_INFO = By.cssSelector(".wpt-event-info__wrp.wpt-event-info__wrp--primary");
    public static final By MATCH_TEAMS = By.cssSelector(".wpt-teams__team");
    public static final By LEAGUE_BODY = By.cssSelector(".wpt-tournament__body");


    public static final String LINE_GROUP = "//div[@class='wol-market__header__title' and text()='%s']/../..";
    public static final String GROUP_MONEYLINE = "2-3 Way";

    public static final String GROUP_HANDICAPS = "Handicaps";
    public static final String GROUP_TOTALS = "Totals";
    public static final By MONEYLINE_TEXT = By.cssSelector(".wol-odds-container.wol-odds-container--2");
    public static final String HANDICAP_TEXT = "div[data-additional-value='%s']";
    public static final String TOTAL_TEXT = "div[data-additional-value='%s']";

}
