package ru.netology.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransactionPage;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {

    @BeforeEach
    void login() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    void returnCardBalancesToDefault() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
        var dashboardPage = new DashboardPage();
        var firstCardBalance = dashboardPage.getCardBalance("92df3f1c-a033-48e6-8390-206f6b1f56c0");
        var secondCardBalance = dashboardPage.getCardBalance("0f3f5c2a-249e-4c3d-8287-09f7a039391d");
        if (firstCardBalance < secondCardBalance) {
            dashboardPage.firstDepositButtonClick();
            var transactionPage = new TransactionPage();
            transactionPage.validTransfer(String.valueOf((secondCardBalance - firstCardBalance) / 2), "5559 0000 0000 0002");
        } else if (firstCardBalance > secondCardBalance) {
            dashboardPage.secondDepositButtonClick();
            var transactionPage = new TransactionPage();
            transactionPage.validTransfer(String.valueOf((firstCardBalance - secondCardBalance) / 2), "5559 0000 0000 0001");
        }
    }

    @Test
    void shouldDepositFirstCardFromSecond() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 1900;
        transactionPage.validTransfer(String.valueOf(amount), "5559 0000 0000 0002");
        var firstCardBalance = dashboardPage.getCardBalance("92df3f1c-a033-48e6-8390-206f6b1f56c0");
        var secondCardBalance = dashboardPage.getCardBalance("0f3f5c2a-249e-4c3d-8287-09f7a039391d");
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldDepositSecondCardFromFirst() {
        var dashboardPage = new DashboardPage();
        dashboardPage.secondDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0002"));
        var amount = 2800;
        transactionPage.validTransfer(String.valueOf(amount), "5559 0000 0000 0001");
        var firstCardBalance = dashboardPage.getCardBalance("92df3f1c-a033-48e6-8390-206f6b1f56c0");
        var secondCardBalance = dashboardPage.getCardBalance("0f3f5c2a-249e-4c3d-8287-09f7a039391d");
        assertEquals(10000 - amount, firstCardBalance);
        assertEquals(10000 + amount, secondCardBalance);
    }

    @Test
    void shouldDepositFirstCardFromSecondWithKopecks() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 50.50;
        transactionPage.validTransfer(String.valueOf(amount), "5559 0000 0000 0002");
        var firstCardBalance = dashboardPage.getCardBalance("92df3f1c-a033-48e6-8390-206f6b1f56c0");
        var secondCardBalance = dashboardPage.getCardBalance("0f3f5c2a-249e-4c3d-8287-09f7a039391d");
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldNotDepositFirstCardFromSecondOverLimit() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 12000;
        transactionPage.invalidTransfer(String.valueOf(amount), "5559 0000 0000 0002");
    }

    @Test
    void shouldNotAcceptNegative() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = -1000;
        transactionPage.validTransfer(String.valueOf(amount), "5559 0000 0000 0002");
        var firstCardBalance = dashboardPage.getCardBalance("92df3f1c-a033-48e6-8390-206f6b1f56c0");
        var secondCardBalance = dashboardPage.getCardBalance("0f3f5c2a-249e-4c3d-8287-09f7a039391d");
        assertEquals(10000 - amount, firstCardBalance);
        assertEquals(10000 + amount, secondCardBalance);
    }

    @Test
    void shouldNotDepositFirstCardFromNotExisting() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 12000;
        transactionPage.invalidTransfer(String.valueOf(amount), "5559 0000 0000 0003");
    }

    @Test
    void shouldNotDepositFirstCardFromSelf() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 2550;
        transactionPage.invalidTransfer(String.valueOf(amount), "5559 0000 0000 0001");
    }

    @Test
    void shouldCancelDepositFirstCardFromSecond() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 7400;
        transactionPage.getAmountField().val(String.valueOf(amount));
        transactionPage.getFromField().val("5559 0000 0000 0002");
        transactionPage.cancelTransaction();
        var firstCardBalance = dashboardPage.getCardBalance("92df3f1c-a033-48e6-8390-206f6b1f56c0");
        var secondCardBalance = dashboardPage.getCardBalance("0f3f5c2a-249e-4c3d-8287-09f7a039391d");
        assertEquals(10000, firstCardBalance);
        assertEquals(10000, secondCardBalance);
    }
}
