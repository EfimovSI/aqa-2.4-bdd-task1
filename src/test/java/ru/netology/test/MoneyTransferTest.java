package ru.netology.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.CardData;
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
        var firstCardBalance = dashboardPage.getCardBalance(CardData.getFirstCardInfo().getCardDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(CardData.getSecondCardInfo().getCardDataTestId());
        if (firstCardBalance < secondCardBalance) {
            dashboardPage.firstDepositButtonClick();
            var transactionPage = new TransactionPage();
            transactionPage.validTransfer(String.valueOf((secondCardBalance - firstCardBalance) / 2),
                    CardData.getSecondCardInfo().getCardNumber());
        } else if (firstCardBalance > secondCardBalance) {
            dashboardPage.secondDepositButtonClick();
            var transactionPage = new TransactionPage();
            transactionPage.validTransfer(String.valueOf((firstCardBalance - secondCardBalance) / 2),
                    CardData.getFirstCardInfo().getCardNumber());
        }
    }

    @Test
    void shouldDepositFirstCardFromSecond() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 1900;
        transactionPage.validTransfer(String.valueOf(amount), CardData.getSecondCardInfo().getCardNumber());
        var firstCardBalance = dashboardPage.getCardBalance(CardData.getFirstCardInfo().getCardDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(CardData.getSecondCardInfo().getCardDataTestId());
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
        transactionPage.validTransfer(String.valueOf(amount), CardData.getFirstCardInfo().getCardNumber());
        var firstCardBalance = dashboardPage.getCardBalance(CardData.getFirstCardInfo().getCardDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(CardData.getSecondCardInfo().getCardDataTestId());
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
        transactionPage.validTransfer(String.valueOf(amount), CardData.getSecondCardInfo().getCardNumber());
        var firstCardBalance = dashboardPage.getCardBalance(CardData.getFirstCardInfo().getCardDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(CardData.getSecondCardInfo().getCardDataTestId());
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
        transactionPage.invalidTransfer(String.valueOf(amount), CardData.getSecondCardInfo().getCardNumber());
    }

    @Test
    void shouldNotAcceptNegative() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = -1000;
        transactionPage.validTransfer(String.valueOf(amount), CardData.getSecondCardInfo().getCardNumber());
        var firstCardBalance = dashboardPage.getCardBalance(CardData.getFirstCardInfo().getCardDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(CardData.getSecondCardInfo().getCardDataTestId());
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
        transactionPage.invalidTransfer(String.valueOf(amount), CardData.getFirstCardInfo().getCardNumber());
    }

    @Test
    void shouldCancelDepositFirstCardFromSecond() {
        var dashboardPage = new DashboardPage();
        dashboardPage.firstDepositButtonClick();
        var transactionPage = new TransactionPage();
        transactionPage.getToField().shouldHave(attribute("value", "**** **** **** 0001"));
        var amount = 7400;
        transactionPage.cancelTransaction(String.valueOf(amount), CardData.getSecondCardInfo().getCardNumber());
        var firstCardBalance = dashboardPage.getCardBalance(CardData.getFirstCardInfo().getCardDataTestId());
        var secondCardBalance = dashboardPage.getCardBalance(CardData.getSecondCardInfo().getCardDataTestId());
        assertEquals(10000, firstCardBalance);
        assertEquals(10000, secondCardBalance);
    }
}
