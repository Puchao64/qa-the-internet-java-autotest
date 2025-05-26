package com.theinternet.login;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    @Order(1)
    @DisplayName("01_successful_login")
    void successfulLoginTest() {
        driver.get("https://the-internet.herokuapp.com/login");
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/secure"));

        String url = driver.getCurrentUrl();
        String message = driver.findElement(By.id("flash")).getText();

        Assertions.assertTrue(url.contains("/secure"));
        Assertions.assertTrue(message.contains("You logged into a secure area!"));
    }

    @Test
    @Order(2)
    @DisplayName("02_invalid_password")
    void invalidPasswordTest() {
        driver.get("https://the-internet.herokuapp.com/login");
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("wrongPassword");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String message = driver.findElement(By.id("flash")).getText();
        Assertions.assertTrue(message.contains("Your password is invalid!"));
    }

    @Test
    @Order(3)
    @DisplayName("03_empty_fields")
    void emptyFieldsTest() {
        driver.get("https://the-internet.herokuapp.com/login");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String message = driver.findElement(By.id("flash")).getText();
        Assertions.assertTrue(message.contains("Your username is invalid!"));
    }

    @Test
    @Order(4)
    @DisplayName("04_logout_flow")
    void logoutTest() {
        driver.get("https://the-internet.herokuapp.com/login");
        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        driver.findElement(By.cssSelector("a[href='/logout']")).click();

        String url = driver.getCurrentUrl();
        String message = driver.findElement(By.id("flash")).getText();

        Assertions.assertTrue(url.contains("/login"));
        Assertions.assertTrue(message.contains("You logged out of the secure area!"));
    }

    @AfterEach
    void takeScreenshot(TestInfo testInfo) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File src = screenshot.getScreenshotAs(OutputType.FILE);
            File dest = new File("screenshots/" + testInfo.getDisplayName() + ".png");
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Screenshot capture failed: " + e.getMessage());
        }

        if (driver != null) {
            driver.quit();
        }
    }
}
