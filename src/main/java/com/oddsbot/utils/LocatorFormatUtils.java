package com.oddsbot.utils;

import org.openqa.selenium.By;

public class LocatorFormatUtils {

    private LocatorFormatUtils() {
    }

    public static By formatCss(String locator, String value) {
        return By.cssSelector(String.format(locator, value));
    }

    public static By formatCss(String locator, String... args) {
        return By.cssSelector(String.format(locator, (Object[]) args));
    }

    public static By formatXpath(String locator, String value) {
        return By.xpath(String.format(locator, value));
    }

    public static By formatXpath(String locator, String... args) {
        return By.xpath(String.format(locator, (Object[]) args));
    }
}
