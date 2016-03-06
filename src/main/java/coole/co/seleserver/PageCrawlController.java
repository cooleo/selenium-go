package coole.co.seleserver;

import java.util.concurrent.atomic.AtomicLong;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@RestController
public class PageCrawlController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/google")
    public @ResponseBody PageResponse google(@RequestParam(value = "name", defaultValue = "World") String name) {
        WebDriver driver = new FirefoxDriver();
        driver.get("http://www.google.com");
        WebElement element = driver.findElement(By.name("q"));
        element.sendKeys("Cheese!");
        element.submit();
        System.out.println("Page title is: " + driver.getTitle());
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });
        System.out.println("Page title is: " + driver.getTitle());
        String title = driver.getTitle();
        driver.quit();
        return new PageResponse(counter.incrementAndGet(),
                String.format(template, title));
    }

    @RequestMapping("/youtube")
    public @ResponseBody
    PageResponse youtube(@RequestParam(value = "url") String url) {
        String html = null;
        WebDriver driver = null;
        try {
            driver = new FirefoxDriver();
            driver.get(url);
            do {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("do load more");
                driver.findElement(By.className("browse-items-load-more-button")).click();
                WebDriverWait wait13 = new WebDriverWait(driver, 5000);
                wait13.until(ExpectedConditions.visibilityOfElementLocated(By.className("browse-items-load-more-button")));
                html = driver.getPageSource();
            }
            while (driver.findElement(By.className("browse-items-load-more-button")).isDisplayed());
            driver.quit();
            return new PageResponse(counter.incrementAndGet(),
                    String.format(template, html));
        } catch (Exception ex) {
            if(driver != null) {
                driver.quit();
            }
            System.out.println("ex:" + ex.getMessage().toString());
            return new PageResponse(counter.incrementAndGet(),
                    String.format(template, html));
        }
    }

    @RequestMapping("/instangram")
    public @ResponseBody  PageResponse instangram(@RequestParam(value = "url") String url) {
        String html = null;
        WebDriver driver = null;
        try {
            driver = new FirefoxDriver();
            driver.get(url);
            WebDriverWait wait13 = new WebDriverWait(driver, 5000);
            wait13.until(ExpectedConditions.visibilityOfElementLocated(By.className("_oidfu")));
            driver.findElement(By.className("_oidfu")).click();
            int total = 0;
            do {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("do load more");
                ((JavascriptExecutor) driver)
                        .executeScript("window.scrollTo(0, 80000)");
                html = driver.getPageSource();
                total++;
            }
            while (total<20);
            driver.quit();
            return new PageResponse(counter.incrementAndGet(),
                    String.format(template, html));
        } catch (Exception ex) {
            if(driver != null) {
                driver.quit();
            }
            System.out.println("ex:" + ex.getMessage().toString());
            return new PageResponse(counter.incrementAndGet(),
                    String.format(template, html));
        }
    }
}
