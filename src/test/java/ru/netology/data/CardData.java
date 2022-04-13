package ru.netology.data;

import lombok.Value;
import ru.netology.page.DashboardPage;

public class CardData {

    private static DashboardPage dashboardPage;

    public static CardInfo getFirstCardInfo() {
        return new CardInfo("92df3f1c-a033-48e6-8390-206f6b1f56c0","5559 0000 0000 0001");
    }

    public static CardInfo getSecondCardInfo() {
        return new CardInfo("0f3f5c2a-249e-4c3d-8287-09f7a039391d","5559 0000 0000 0002");
    }

    @Value
    public static class CardInfo {
        private String cardDataTestId;
        private String cardNumber;
    }
}
