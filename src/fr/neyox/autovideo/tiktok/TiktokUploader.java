package fr.neyox.autovideo.tiktok;

import fr.neyox.autovideo.Main;
import fr.neyox.autovideo.exceptions.LoginException;
import fr.neyox.autovideo.exceptions.UploadException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Getter
public class TiktokUploader {

    private final String name;
    private final File video;
    private boolean uplaod = false;

    static {
        System.setProperty("webdriver.chrome.driver", ".\\lib\\chromedriver.exe");
    }

    public void upload() throws UploadException, LoginException {
        WebDriver driver = new ChromeDriver(new ChromeOptions().setHeadless(false).setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe"));
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.get("https://www.tiktok.com/upload?lang=fr");
        TikTokLogin login = new TikTokLogin("", "", TikTokLogin.LoginType.FACEBOOK);
        login.login(driver);
        driver = driver.switchTo().window(driver.getWindowHandles().toArray(new String[]{})[0]);
        WebDriverWait wait = new WebDriverWait(driver, 150);
        WebDriver finalDriver = driver;
        wait.until(f-> {
                String nameHtml = ".DraftEditor-editorContainer>div";
                new Actions(finalDriver).moveToElement( finalDriver.findElement(By.cssSelector(nameHtml))).perform();
                finalDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                finalDriver.findElement(By.cssSelector(".button-wrapper.jsx-2658524077 button.jsx-2658524077")).click();
                finalDriver.findElement(By.cssSelector(nameHtml)).sendKeys(this.name);
                finalDriver.findElement(By.cssSelector(".upload-btn-input.jsx-1828163283")).sendKeys(this.video.getAbsolutePath());
            Main.scheduler.scheduleAtFixedRate(()->{
                if (finalDriver.getCurrentUrl().equals("https://www.tiktok.com/upload?lang=fr")){
                    JavascriptExecutor js = (JavascriptExecutor) finalDriver;
                    System.out.println(js.executeAsyncScript("document.getElementsByClassName('tiktok-btn-pc tiktok-btn-pc-large tiktok-btn-pc-primary').click();"));
                 /*   WebElement publish = finalDriver.findElement(By.cssSelector(".tiktok-btn-pc-primary"));
                    System.out.println(publish.getTagName());
                    publish.click();*/
                    System.out.println("Clicked");
                }

            }, 0, 10, TimeUnit.SECONDS);

            return f;
        });

    }

}
