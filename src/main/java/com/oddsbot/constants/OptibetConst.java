package com.oddsbot.constants;

import org.openqa.selenium.By;

public final class OptibetConst {

    private OptibetConst() {
    }

    public static final String URL = "https://www.optibet.lt/en/sport/basketball";
    public static final By IFRAME = By.xpath("//*[@id=\"iFrameResizer0\"]");
    public static final By UPCOMING_BUTTON = By.xpath("//div[@class='sport-page__tab-title' and text()='Upcoming']");
    public static final By MATCHES = By.cssSelector(".event-block-row__info");
    public static final By MATCH_NAME = By.cssSelector(".event-block-row__event-name");
    public static final By NEXT_PAGE_BUTTON = By.cssSelector(".pagination__next-link");
    public static final By ACCEPT_COOKIES_BUTTON = By.cssSelector(".button-base___2T-n8-scss.button___2Gzrn-scss.button_size-default___16JDz-scss.button_size-small___2uYUM-scss.button_intent-default___aKrsp-scss.button_intent-primary___2IMod-scss.acceptButton___DSGhb-scss");
    public static final By LINE_CATEGORIES = By.cssSelector(".collapsible.event-market.sport-event__market");
    public static final String LINE_HANDICAP = "//div[contains(text(), '(%s)')]/..";
    public static final String LINE_OVER = "//div[contains(text(), 'Over %s')]/..";
    public static final String LINE_UNDER = "//div[contains(text(), 'Under %s')]/..";


}
