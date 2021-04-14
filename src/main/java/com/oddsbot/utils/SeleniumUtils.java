package com.oddsbot.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class SeleniumUtils {
    private static final int DEFAULT_WAIT_TIME = 10;
    protected static final Logger logger = LoggerFactory.getLogger(SeleniumUtils.class);

    private SeleniumUtils() {
    }

    public static Optional<WebElement> findElement(WebDriver driver, By parameter) {
        try {
            return Optional.of(driver.findElement(parameter));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<List<WebElement>> findElements(WebDriver driver, By parameter) {
        try {
            return Optional.of(driver.findElements(parameter));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<WebElement> findElement(WebElement element, By parameter) {
        try {
            return Optional.of(element.findElement(parameter));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<List<WebElement>> findElements(WebElement element, By parameter) {
        try {
            return Optional.of(element.findElements(parameter));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static void clickJsButton(ChromeDriver driver, WebElement button) {
        try {
            driver.executeScript("arguments[0].click();", button);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static Optional<WebElement> waitForElementToBeVisible(WebDriver driver, By parameter) {
        return waitForElementToBeVisible(driver, parameter, DEFAULT_WAIT_TIME);
    }

    public static Optional<WebElement> waitForElementToBeVisible(WebDriver driver, By parameter, int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        try {
            return Optional.of(wait.until(ExpectedConditions.visibilityOfElementLocated(parameter)));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<WebElement> waitForElement(WebDriver driver, By parameter) {
        return waitForElement(driver, parameter, DEFAULT_WAIT_TIME);
    }

    public static Optional<WebElement> waitForElement(WebDriver driver, By parameter, int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        try {
            return Optional.of(wait.until(ExpectedConditions.presenceOfElementLocated(parameter)));
        } catch (TimeoutException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<List<WebElement>> waitForElements(WebDriver driver, By parameter) {
        return waitForElements(driver, parameter, DEFAULT_WAIT_TIME);
    }

    public static Optional<List<WebElement>> waitForElements(WebDriver driver, By parameter, int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        try {
            return Optional.of(wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(parameter)));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<WebElement> waitForElementToBeClickable(WebDriver driver, By parameter) {
        return waitForElementToBeClickable(driver, parameter, DEFAULT_WAIT_TIME);
    }

    public static Optional<WebElement> waitForElementToBeClickable(WebDriver driver, By parameter, int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        try {
            return Optional.of(wait.until(ExpectedConditions.elementToBeClickable(parameter)));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static void waitForElementToGoStale(ChromeDriver driver, WebElement element) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            wait.until(ExpectedConditions.stalenessOf(element));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public static void switchToIFrame(WebDriver driver, By parameter) {
        try {
            Optional<WebElement> iframe = waitForElementToBeVisible(driver, parameter);
            iframe.ifPresent(webElement -> driver.switchTo().frame(webElement));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}