package com.example.relaxtodrinking;

import android.app.Activity;
import android.widget.Toast;

import tech.cherri.tpdirect.api.TPDCard;


public class Common {
    public static void showToast(Activity activity, String messageRes) {
        Toast.makeText(activity, messageRes, Toast.LENGTH_SHORT).show();
    }

    public static final String TAPPAY_DOMAIN_SANDBOX = "https://sandbox.tappaysdk.com";
    public static final String TAPPAY_PAY_BY_PRIME_URL = "/tpc/payment/pay-by-prime";
    public static final TPDCard.CardType[] CARD_TYPES = new TPDCard.CardType[]{
            TPDCard.CardType.Visa
            , TPDCard.CardType.MasterCard
            , TPDCard.CardType.JCB
            , TPDCard.CardType.AmericanExpress
    };

}

