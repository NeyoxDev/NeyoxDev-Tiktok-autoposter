package fr.neyox.autovideo.tiktok;

import fr.neyox.autovideo.exceptions.LoginException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class TikTokLogin {

    private String mail, password;
    private LoginType type;

    public void login(WebDriver browser) throws LoginException {
        if (type == LoginType.FACEBOOK){
            WebElement facebook = browser.findElement(By.cssSelector(".channel-item-wrapper-2gBWB+.channel-item-wrapper-2gBWB+.channel-item-wrapper-2gBWB .channel-name-2qzLW"));
            facebook.click();
            WebDriver login = getFacebookPage(browser);
            login.findElement(By.id("email")).sendKeys(this.getMail());
            login.findElement(By.id("pass")).sendKeys(this.getPassword());
            login.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            if (login.findElements(By.tagName("button")).size() >=2)login.findElements(By.tagName("button")).get(1).click();
            login.findElements(By.tagName("input")).stream().filter(buttons -> buttons.getAttribute("name").equals("login")).findFirst().orElse(null).click();

        }
    }



    private WebDriver getFacebookPage(WebDriver browser) throws LoginException {
        try {
            WebDriver login = browser.switchTo().window(browser.getWindowHandles().toArray(new String[]{})[1]);
            if (login != null && login.getCurrentUrl().contains("facebook")){
                return login;
            }else{
                throw new LoginException("Failed to find facebook login page");
            }
        } catch (Exception e){
            throw new LoginException(e);
        }


    }

    public enum LoginType{
        FACEBOOK;
    }

}
