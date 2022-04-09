package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class TransactionPage {
    private SelenideElement heading = $("h1").shouldHave(text("Пополнение карты"));
    private SelenideElement amountField = $("[data-test-id='amount'] input");
    private SelenideElement fromField = $("[data-test-id='from'] input");
    private SelenideElement toField = $("[data-test-id='to'] input");
    private SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private SelenideElement cancelButton = $("[data-test-id='action-cancel']");
    private SelenideElement errorNotification = $("[data-test-id='error-notification'] .notification__content");

    public TransactionPage() {
        heading.shouldBe(visible);
    }

    public DashboardPage validTransfer(String amount, String from) {
        amountField.val(amount);
        fromField.val(from);
        transferButton.click();
        return new DashboardPage();
    }

    public TransactionPage invalidTransfer(String amount, String from) {
        amountField.val(amount);
        fromField.val(from);
        transferButton.click();
        errorNotification.shouldHave(text("Произошла ошибка"));
        return new TransactionPage();
    }

    public DashboardPage cancelTransaction() {
        cancelButton.click();
        return new DashboardPage();
    }

    public SelenideElement getAmountField() {
        return amountField;
    }

    public SelenideElement getFromField() {
        return fromField;
    }

    public SelenideElement getToField() {
        return toField;
    }
}
