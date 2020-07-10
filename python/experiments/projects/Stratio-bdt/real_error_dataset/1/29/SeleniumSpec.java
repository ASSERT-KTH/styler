/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.qa.specs;

import com.stratio.qa.cucumber.converter.NullableString;
import com.stratio.qa.cucumber.converter.Strokes;
import com.stratio.qa.utils.PreviousWebElements;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.*;

import java.util.*;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.stratio.qa.assertions.Assertions.assertThat;
import static org.testng.Assert.fail;

/**
 * Generic Selenium Specs.
 */
public class SeleniumSpec extends BaseGSpec {

    private Scenario scenario;

    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public SeleniumSpec(CommonG spec) {
        this.commonspec = spec;

    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    /**
     * Browse to {@code url} using the current browser.
     *
     * @param path path of running app
     * @throws Exception exception
     */
    @Given("^I( securely)? browse to '(.+?)'$")
    public void seleniumBrowse(String isSecured, String path) throws Exception {
        assertThat(path).isNotEmpty();

        if (commonspec.getWebHost() == null) {
            throw new Exception("Web host has not been set");
        }

        if (commonspec.getWebPort() == null) {
            throw new Exception("Web port has not been set");
        }
        String protocol = "http://";
        if (isSecured != null) {
            protocol = "https://";
        }

        String webURL = protocol + commonspec.getWebHost() + commonspec.getWebPort();

        commonspec.getDriver().get(webURL + path);
        commonspec.setParentWindow(commonspec.getDriver().getWindowHandle());
    }

    /**
     * Set app host and port {@code host, @code port}
     *
     * @param host host where app is running
     * @param port port where app is running
     */
    @Given("^My app is running in '([^:]+?)(:.+?)?'$")
    public void setupApp(String host, String port) {
        assertThat(host).isNotEmpty();

        if (port == null) {
            port = ":80";
        }

        commonspec.setWebHost(host);
        commonspec.setWebPort(port);
        commonspec.setRestHost(host);
        commonspec.setRestPort(port);
    }

    /**
     * Maximizes current browser window. Mind the current resolution could break a test.
     */
    @Given("^I maximize the browser$")
    public void seleniumMaximize() {
        commonspec.getDriver().manage().window().maximize();
    }

    /**
     * Switches to a frame/ iframe.
     */
    @Given("^I switch to the iframe on index '(\\d+)'$")
    public void seleniumSwitchFrame(Integer index) {

        assertThat(commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);

        WebElement elem = commonspec.getPreviousWebElements().getPreviousWebElements().get(index);
        commonspec.getDriver().switchTo().frame(elem);
    }

    /**
     * Swith to the iFrame where id matches idframe
     *
     * @param idframe iframe to swith to
     * @throws IllegalAccessException exception
     * @throws NoSuchFieldException   exception
     * @throws ClassNotFoundException exception
     */
    @Given("^I switch to iframe with '([^:]*?):(.+?)'$")
    public void seleniumIdFrame(String method, String idframe) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        assertThat(commonspec.locateElement(method, idframe, 1));

        if (method.equals("id") || method.equals("name")) {
            commonspec.getDriver().switchTo().frame(idframe);
        } else {
            throw new ClassNotFoundException("Can not use this method to switch iframe");
        }
    }

    /**
     * Switches to a parent frame/ iframe.
     */
    @Given("^I switch to a parent frame$")
    public void seleniumSwitchAParentFrame() {
        commonspec.getDriver().switchTo().parentFrame();
    }

    /**
     * Switches to the frames main container.
     */
    @Given("^I switch to the main frame container$")
    public void seleniumSwitchParentFrame() {
        commonspec.getDriver().switchTo().frame(commonspec.getParentWindow());
    }

    /**
     * Get all opened windows and store it.
     */
    @Given("^a new window is opened$")
    public void seleniumGetwindows() {
        Set<String> wel = commonspec.getDriver().getWindowHandles();

        Assertions.assertThat(wel).as("Element count doesnt match").hasSize(2);
    }

    /**
     * Searchs for two webelements dragging the first one to the second
     *
     * @param source
     * @param destination
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    @When("^I drag '([^:]*?):(.+?)' and drop it to '([^:]*?):(.+?)'$")
    public void seleniumDrag(String smethod, String source, String dmethod, String destination) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Actions builder = new Actions(commonspec.getDriver());

        List<WebElement> sourceElement = commonspec.locateElement(smethod, source, 1);
        List<WebElement> destinationElement = commonspec.locateElement(dmethod, destination, 1);

        builder.dragAndDrop(sourceElement.get(0), destinationElement.get(0)).perform();
    }

    /**
     * Dragging element with offset
     * @param element
     * @param xOffset
     * @param yOffset
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @When("I move element with {string}, '{int}' pixels horizontally and '{int}' pixels vertically")
    public void seleniumDragOffset(String element, Integer xOffset, Integer yOffset) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        String[] elementArray = element.split(":");
        if (elementArray.length != 2) {
            fail("Element argument doesn't match regex: ([^:]*?):(.+?) [" + element + "]");
        }
        Actions builder = new Actions(commonspec.getDriver());
        List<WebElement> sourceElement = commonspec.locateElement(elementArray[0], elementArray[1], 1);
        builder.dragAndDropBy(sourceElement.get(0), xOffset, yOffset).perform();
    }

    /**
     * Click on an numbered {@code url} previously found element.
     *
     * @param index
     * @throws InterruptedException
     */
    @When("^I click on the element on index '(\\d+)'$")
    public void seleniumClick(Integer index) throws InterruptedException {

        try {
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
            commonspec.getPreviousWebElements().getPreviousWebElements().get(index).click();
        } catch (AssertionError e) {
            Thread.sleep(1000);
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
            commonspec.getPreviousWebElements().getPreviousWebElements().get(index).click();
        } catch (WebDriverException e) {
            JavascriptExecutor executor = commonspec.getDriver();
            executor.executeScript("arguments[0].click();", commonspec.getPreviousWebElements().getPreviousWebElements().get(index));
        }
    }

    /**
     * Double Click on an numbered {@code url} previously found element.
     *
     * @param index
     * @throws InterruptedException
     */
    @When("^I double click on the element on index '(\\d+)'$")
    public void seleniumDoubleClick(Integer index) throws InterruptedException {
        Actions action = new Actions(commonspec.getDriver());
        try {
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
            action.doubleClick(commonspec.getPreviousWebElements().getPreviousWebElements().get(index)).perform();

        } catch (AssertionError e) {
            Thread.sleep(1000);
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
            action.doubleClick(commonspec.getPreviousWebElements().getPreviousWebElements().get(index)).perform();
        }
    }

    /**
     * Clear the text on a numbered {@code index} previously found element.
     *
     * @param index
     */
    @When("^I clear the content on text input at index '(\\d+)'$")
    public void seleniumClear(Integer index) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);

        assertThat(this.commonspec, commonspec.getPreviousWebElements().getPreviousWebElements().get(index)).isTextField(commonspec.getTextFieldCondition());

        commonspec.getPreviousWebElements().getPreviousWebElements().get(index).clear();
    }


    /**
     * Delete or replace the text on a numbered {@code index} previously found element.
     *
     * @param index
     */
    @When("^I delete the text '(.+?)' on the element on index '(\\d+)'( and replace it for '(.+?)')?$")
    public void seleniumDelete(String text, Integer index, String replacement) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);

        Actions actions = new Actions(commonspec.getDriver());
        actions.moveToElement(commonspec.getPreviousWebElements().getPreviousWebElements().get(index), (text.length() / 2), 0);
        for (int i = 0; i < (text.length() / 2); i++) {
            actions.sendKeys(Keys.ARROW_LEFT);
            actions.build().perform();
        }
        for (int i = 0; i < text.length(); i++) {
            actions.sendKeys(Keys.DELETE);
            actions.build().perform();
        }
        if (replacement != null && replacement.length() != 0) {
            actions.sendKeys(replacement);
            actions.build().perform();
        }
    }


    /**
     * Type a {@code text} on an numbered {@code index} previously found element.
     *
     * @param nullablestring
     * @param index
     */
    @When("I type {string} on the element on index '{int}'")
    public void seleniumType(String nullablestring, Integer index) {
        String text = NullableString.transform(nullablestring);
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        while (text.length() > 0) {
            Actions actions = new Actions(commonspec.getDriver());
            if (-1 == text.indexOf("\\n")) {
                actions.moveToElement(commonspec.getPreviousWebElements().getPreviousWebElements().get(index));
                actions.click();
                actions.sendKeys(text);
                actions.build().perform();
                text = "";
            } else {
                actions.moveToElement(commonspec.getPreviousWebElements().getPreviousWebElements().get(index));
                actions.click();
                actions.sendKeys(text.substring(0, text.indexOf("\\n")));
                actions.build().perform();
                text = text.substring(text.indexOf("\\n") + 2);
            }
        }
    }

    /**
     * Paste text on {@code text}
     * @param nullableSelector example: div #id_div a .a_class
     * @param nullableTest
     */
    @Given("I type on element {string} the following text {string}")
    public void seleniumAppend(String nullableSelector, String nullableTest) {
        String jsSelector = NullableString.transform(nullableSelector);
        String text = NullableString.transform(nullableTest);
        WebDriver driver = commonspec.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.querySelector('" + jsSelector + "').value = '" + text + "'");
    }

    /**
     * Wait for render a html element
     * @param method
     * @param element
     * @param sTimeout
     */
    @When("^I wait for element '([^:]*?):(.+?)' to be available for '(\\d+?)' seconds$")
    public void seleniumWait(String method, String element, String sTimeout) {
        Integer timeout = sTimeout != null ? Integer.parseInt(sTimeout) : null;
        RemoteWebDriver driver = commonspec.getDriver();
        WebDriverWait driverWait = new WebDriverWait(driver, timeout);
        By criteriaSel = null;
        if ("id".equals(method)) {
            criteriaSel = By.id(element);
        } else if ("name".equals(method)) {
            criteriaSel = By.name(element);
        } else if ("class".equals(method)) {
            criteriaSel = By.className(element);
        } else if ("xpath".equals(method)) {
            criteriaSel = By.xpath(element);
        } else if ("css".equals(method)) {
            criteriaSel = By.cssSelector(element);
        } else {
            fail("Unknown search method: " + method);
        }
        driverWait.until(ExpectedConditions.
                presenceOfElementLocated(criteriaSel));
    }

    /**
     * Send a {@code strokes} list on an numbered {@code url} previously found element or to the driver. strokes examples are "HOME, END"
     * or "END, SHIFT + HOME, DELETE". Each element in the stroke list has to be an element from
     * {@link org.openqa.selenium.Keys} (NULL, CANCEL, HELP, BACK_SPACE, TAB, CLEAR, RETURN, ENTER, SHIFT, LEFT_SHIFT,
     * CONTROL, LEFT_CONTROL, ALT, LEFT_ALT, PAUSE, ESCAPE, SPACE, PAGE_UP, PAGE_DOWN, END, HOME, LEFT, ARROW_LEFT, UP,
     * ARROW_UP, RIGHT, ARROW_RIGHT, DOWN, ARROW_DOWN, INSERT, DELETE, SEMICOLON, EQUALS, NUMPAD0, NUMPAD1, NUMPAD2,
     * NUMPAD3, NUMPAD4, NUMPAD5, NUMPAD6, NUMPAD7, NUMPAD8, NUMPAD9, MULTIPLY, ADD, SEPARATOR, SUBTRACT, DECIMAL,
     * DIVIDE, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12, META, COMMAND, ZENKAKU_HANKAKU) , a plus sign (+), a
     * comma (,) or spaces ( )
     *
     * @param sStrokes
     * @param sIndex
     */
    @When("^I send '(.+?)'( on the element on index '(\\d+)')?$")
    public void seleniumKeys(String sStrokes, String sIndex) {
        Strokes strokes = new Strokes(sStrokes);
        Integer index = sIndex != null ? Integer.valueOf(sIndex) : null;
        if (index != null) {
            assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                    .hasAtLeast(index);
        }
        assertThat(strokes.getStrokesList()).isNotEmpty();

        for (String stroke : strokes.getStrokesList()) {
            if (stroke.contains("+")) {
                List<Keys> csl = new ArrayList<Keys>();
                for (String strokeInChord : stroke.split("\\+")) {
                    csl.add(Keys.valueOf(strokeInChord.trim()));
                }
                Keys[] csa = csl.toArray(new Keys[csl.size()]);
                if (index == null) {
                    new Actions(commonspec.getDriver()).sendKeys(commonspec.getDriver().findElement(By.tagName("body")), csa).perform();
                } else {
                    commonspec.getPreviousWebElements().getPreviousWebElements().get(index).sendKeys(csa);
                }
            } else {
                if (index == null) {
                    new Actions(commonspec.getDriver()).sendKeys(commonspec.getDriver().findElement(By.tagName("body")), Keys.valueOf(stroke)).perform();
                } else {
                    commonspec.getPreviousWebElements().getPreviousWebElements().get(index).sendKeys(Keys.valueOf(stroke));
                }
            }
        }
    }

    /**
     * Choose an @{code option} from a select webelement found previously
     *
     * @param option
     * @param index
     */
    @When("^I select '(.+?)' on the element on index '(\\d+)'$")
    public void elementSelect(String option, Integer index) {
        Select sel = null;
        sel = new Select(commonspec.getPreviousWebElements().getPreviousWebElements().get(index));

        sel.selectByVisibleText(option);
    }

    /**
     * Choose no option from a select webelement found previously
     *
     * @param index
     */
    @When("^I de-select every item on the element on index '(\\d+)'$")
    public void elementDeSelect(Integer index) {
        Select sel = null;
        sel = new Select(commonspec.getPreviousWebElements().getPreviousWebElements().get(index));

        if (sel.isMultiple()) {
            sel.deselectAll();
        }
    }

    /**
     * Change current window to another opened window.
     */
    @When("^I change active window$")
    public void seleniumChangeWindow() {
        String originalWindowHandle = commonspec.getDriver().getWindowHandle();
        Set<String> windowHandles = commonspec.getDriver().getWindowHandles();

        for (String window : windowHandles) {
            if (!window.equals(originalWindowHandle)) {
                commonspec.getDriver().switchTo().window(window);
            }
        }
    }

    /**
     * Verifies that a webelement previously found has {@code text} as text
     *
     * @param index
     * @param text
     */
    @Then("^the element on index '(\\d+)' has '(.+?)' as text$")
    public void assertSeleniumTextOnElementPresent(Integer index, String text) {
        assertThat(commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        String elementText = commonspec.getPreviousWebElements().getPreviousWebElements().get(index).getText().replace("\n", " ").replace("\r", " ");
        if (!elementText.startsWith("regex:")) {
            //We are verifying that a web element contains a string
            assertThat(elementText.matches("(.*)" + text + "(.*)")).isTrue();
        } else {
            //We are verifying that a web element contains a regex
            assertThat(elementText.matches(text.substring(text.indexOf("regex:") + 6, text.length()))).isTrue();
        }
    }

    /**
     * Checks if a text exists in the source of an already loaded URL.
     *
     * @param text
     */
    @Then("^this text exists '(.+?)'$")
    public void assertSeleniumTextInSource(String text) {
        assertThat(this.commonspec, commonspec.getDriver()).as("Expected text not found at page").contains(text);
    }

    /**
     * Checks if {@code expectedCount} webelements are found, with a location {@code method}.
     *
     * @param expectedCount
     * @param method
     * @param element
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    @Then("^'(\\d+)' elements? exists? with '([^:]*?):(.+?)'$")
    public void assertSeleniumNElementExists(Integer expectedCount, String method, String element) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<WebElement> wel = commonspec.locateElement(method, element, expectedCount);
        PreviousWebElements pwel = new PreviousWebElements(wel);
        commonspec.setPreviousWebElements(pwel);
    }

    /**
     * Checks if {@code expectedCount} webelements are found, whithin a {@code timeout} and with a location
     * {@code method}. Each negative lookup is followed by a wait of {@code wait} seconds. Selenium times are not
     * accounted for the mentioned timeout.
     *
     * @param timeout
     * @param wait
     * @param expectedCount
     * @param method
     * @param element
     * @throws InterruptedException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    @Then("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, '(\\d+)' elements exists with '([^:]*?):(.+?)'$")
    public void assertSeleniumNElementExistsOnTimeOut(Integer timeout, Integer wait, Integer expectedCount,
                                                      String method, String element) throws InterruptedException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<WebElement> wel = null;
        for (int i = 0; i < timeout; i += wait) {
            wel = commonspec.locateElement(method, element, -1);
            if (wel.size() == expectedCount) {
                break;
            } else {
                Thread.sleep(wait * 1000);
            }
        }

        PreviousWebElements pwel = new PreviousWebElements(wel);
        assertThat(this.commonspec, pwel).as("Element count doesnt match").hasSize(expectedCount);
        commonspec.setPreviousWebElements(pwel);

    }

    /**
     * Verifies that a webelement previously found {@code isDisplayed}
     *
     * @param index
     * @param isDisplayed
     */
    @Then("the element on index '{int}' {isornot} displayed")
    public void assertSeleniumIsDisplayed(Integer index, Boolean isDisplayed) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        assertThat(this.commonspec, commonspec.getPreviousWebElements().getPreviousWebElements().get(index).isDisplayed()).as(
                "Unexpected element display property").isEqualTo(isDisplayed);
    }

    /**
     * Verifies that a webelement previously found {@code isEnabled}
     *
     * @param index
     * @param isEnabled
     */
    @Then("the element on index '{int}' {isornot} enabled")
    public void assertSeleniumIsEnabled(Integer index, Boolean isEnabled) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        assertThat(this.commonspec, commonspec.getPreviousWebElements().getPreviousWebElements().get(index).isEnabled())
                .as("Unexpected element enabled property").isEqualTo(isEnabled);
    }

    /**
     * Verifies that a webelement previously found {@code isSelected}
     *
     * @param index
     * @param isSelected
     */
    @Then("the element on index '{int}' {isornot} selected")
    public void assertSeleniumIsSelected(Integer index, Boolean isSelected) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        assertThat(this.commonspec, commonspec.getPreviousWebElements().getPreviousWebElements().get(index).isSelected()).as(
                "Unexpected element selected property").isEqualTo(isSelected);
    }

    /**
     * Verifies that a webelement previously found has {@code attribute} with {@code value} (as a regexp)
     *
     * @param index
     * @param attribute
     * @param value
     */
    @Then("^the element on index '(\\d+)' has '(.+?)' as '(.+?)'$")
    public void assertSeleniumHasAttributeValue(Integer index, String attribute, String value) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        String val = commonspec.getPreviousWebElements().getPreviousWebElements().get(index).getAttribute(attribute);
        assertThat(this.commonspec, val).as("Attribute not found").isNotNull();
        assertThat(this.commonspec, val).as("Unexpected value for specified attribute").matches(value);
    }

    /**
     * Takes an snapshot of the current page
     *
     * @throws Exception
     */
    @Then("^I take a snapshot$")
    public void seleniumSnapshot() throws Exception {
        commonspec.captureEvidence(commonspec.getDriver(), "screenCapture", scenario);
    }

    /**
     * Checks that we are in the URL passed
     *
     * @param url
     * @throws Exception
     */
    @Then("^we are in page '(.+?)'$")
    public void checkURL(String url) throws Exception {
        if (commonspec.getWebHost() == null) {
            throw new Exception("Web host has not been set");
        }

        if (commonspec.getWebPort() == null) {
            throw new Exception("Web port has not been set");
        }

        String webURL = commonspec.getWebHost() + commonspec.getWebPort();

        assertThat(commonspec.getDriver().getCurrentUrl()).as("We are not in the expected url: " + webURL.toLowerCase() + url)
                .endsWith(webURL.toLowerCase() + url);
    }

    /**
     * Save cookie in context for future references
     **/
    @Then("^I save selenium cookies in context$")
    public void saveSeleniumCookies() throws Exception {
        commonspec.setSeleniumCookies(commonspec.getDriver().manage().getCookies());
    }


    /**
     * Get dcos-auth-cookie
     **/
    @Then("^I save selenium dcos acs auth cookie in variable '(.+?)'$")
    public void getDcosAcsAuthCookie(String envVar) throws Exception {
        if (commonspec.getSeleniumCookies() != null && commonspec.getSeleniumCookies().size() != 0) {
            for (Cookie cookie: commonspec.getSeleniumCookies()) {
                if (cookie.getName().contains("dcos-acs-auth-cookie")) {
                    //It's this cookie where we have to extract the value
                    ThreadProperty.set(envVar, cookie.getValue());
                    break;
                }
            }
        } else {
            ThreadProperty.set(envVar, null);
        }
    }

    /**
     * Get dcos-auth-cookie
     **/
    @Then("^I save selenium cookie '(.+?)' in variable '(.+?)'$")
    public void getDcosAcsAuthCookie(String cookieName, String envVar) throws Exception {
        if (commonspec.getSeleniumCookies() != null && commonspec.getSeleniumCookies().size() != 0) {
            for (Cookie cookie: commonspec.getSeleniumCookies()) {
                if (cookie.getName().contains(cookieName)) {
                    //It's this cookie where we have to extract the value
                    ThreadProperty.set(envVar, cookie.getValue());
                    break;
                }
            }
        } else {
            ThreadProperty.set(envVar, null);
        }
    }

    /**
     * Check if a cookie exists
     *
     * @param cookieName string with the name of the cookie
     */
    @Then("^The cookie '(.+?)' exists in the saved cookies$")
    public void checkIfCookieExists(String cookieName) {
        Assertions.assertThat(commonspec.cookieExists(cookieName)).isEqualTo(true);
    }
    /**
     * Check if the length of the cookie set match with the number of cookies thas must be saved
     *
     * @param numberOfCookies number of cookies that must be saved
     */
    @Then("^I have '(\\d+)' selenium cookies saved$")
    public void getSeleniumCookiesSize(int numberOfCookies) throws Exception {
        Assertions.assertThat(commonspec.getSeleniumCookies().size()).isEqualTo(numberOfCookies);
    }

    /**
     * Takes the content of a webElement and stores it in the thread environment variable passed as parameter
     *
     * @param index  position of the element in the array of webElements found
     * @param envVar name of the thread environment variable where to store the text
     */
    @Then("^I save content of element in index '(\\d+)' in environment variable '(.+?)'$")
    public void saveContentWebElementInEnvVar(Integer index, String envVar) {
        assertThat(this.commonspec, commonspec.getPreviousWebElements()).as("There are less found elements than required")
                .hasAtLeast(index);
        String text = commonspec.getPreviousWebElements().getPreviousWebElements().get(index).getText();
        ThreadProperty.set(envVar, text);
    }
}