package com.alltrack.sdk.criteo;

import android.net.Uri;

import com.alltrack.sdk.AlltrackEvent;
import com.alltrack.sdk.AlltrackFactory;
import com.alltrack.sdk.ILogger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by pfms on 24/02/15.
 */
public class AlltrackCriteo {
    private static ILogger logger = AlltrackFactory.getLogger();
    private static int MAX_VIEW_LISTING_PRODUCTS = 3;
    private static String hashEmailInternal;
    private static String checkInDateInternal;
    private static String checkOutDateInternal;
    private static String partnerIdInternal;
    private static String userSegmentInternal;
    private static String customerIdInternal;

    public static void injectViewListingIntoEvent(AlltrackEvent event, List<String> productIds) {
        String jsonProducts = createCriteoVLFromProducts(productIds);
        event.addPartnerParameter("criteo_p", jsonProducts);

        injectOptionalParams(event);
    }

    public static void injectViewProductIntoEvent(AlltrackEvent event, String productId) {
        event.addPartnerParameter("criteo_p", productId);

        injectOptionalParams(event);
    }

    public static void injectCartIntoEvent(AlltrackEvent event, List<CriteoProduct> products) {
        String jsonProducts = createCriteoVBFromProducts(products);

        event.addPartnerParameter("criteo_p", jsonProducts);

        injectOptionalParams(event);
    }

    public static void injectTransactionConfirmedIntoEvent(AlltrackEvent event, List<CriteoProduct> products, String transactionId, String newCustomer) {
        String jsonProducts = createCriteoVBFromProducts(products);

        event.addPartnerParameter("transaction_id", transactionId);
        event.addPartnerParameter("criteo_p", jsonProducts);
        event.addPartnerParameter("new_customer", newCustomer);

        injectOptionalParams(event);
    }

    public static void injectUserLevelIntoEvent(AlltrackEvent event, long uiLevel) {
        event.addPartnerParameter("ui_level", String.valueOf(uiLevel));

        injectOptionalParams(event);
    }

    public static void injectUserStatusIntoEvent(AlltrackEvent event, String uiStatus) {
        event.addPartnerParameter("ui_status", uiStatus);

        injectOptionalParams(event);
    }

    public static void injectAchievementUnlockedIntoEvent(AlltrackEvent event, String uiAchievement) {
        event.addPartnerParameter("ui_achievmnt", uiAchievement);

        injectOptionalParams(event);
    }

    public static void injectCustomEventIntoEvent(AlltrackEvent event, String uiData) {
        event.addPartnerParameter("ui_data", uiData);

        injectOptionalParams(event);
    }

    public static void injectCustomEvent2IntoEvent(AlltrackEvent event, String uiData2, long uiData3) {
        event.addPartnerParameter("ui_data2", uiData2);
        event.addPartnerParameter("ui_data3", String.valueOf(uiData3));

        injectOptionalParams(event);
    }

    public static void injectHashedEmailIntoCriteoEvents(String hashEmail) {
        hashEmailInternal = hashEmail;
    }

    public static void injectViewSearchDatesIntoCriteoEvents(String checkInDate, String checkOutDate) {
        checkInDateInternal = checkInDate;
        checkOutDateInternal = checkOutDate;
    }

    public static void injectPartnerIdIntoCriteoEvents(String partnerId) {
        partnerIdInternal = partnerId;
    }

    public static void injectUserSegmentIntoCriteoEvents(String userSegment) {
        userSegmentInternal = userSegment;
    }

    public static void injectCustomerIdIntoCriteoEvents(String customerId) {
        customerIdInternal = customerId;
    }

    public static void injectDeeplinkIntoEvent(AlltrackEvent event, Uri url) {
        if (url == null) {
            return;
        }

        event.addPartnerParameter("criteo_deeplink", url.toString());

        injectOptionalParams(event);
    }

    private static void injectOptionalParams(AlltrackEvent event) {
        injectHashEmail(event);
        injectSearchDates(event);
        injectPartnerId(event);
        injectUserSegment(event);
        injectCustomerId(event);
    }

    private static void injectHashEmail(AlltrackEvent event) {
        if (hashEmailInternal == null || hashEmailInternal.isEmpty()) {
            return;
        }

        event.addPartnerParameter("criteo_email_hash", hashEmailInternal);
    }

    private static void injectSearchDates(AlltrackEvent event) {
        if (checkInDateInternal == null || checkInDateInternal.isEmpty() ||
                checkOutDateInternal == null || checkOutDateInternal.isEmpty()) {
            return;
        }

        event.addPartnerParameter("din", checkInDateInternal);
        event.addPartnerParameter("dout", checkOutDateInternal);
    }

    private static void injectPartnerId(AlltrackEvent event) {
        if (partnerIdInternal == null || partnerIdInternal.isEmpty()) {
            return;
        }

        event.addPartnerParameter("criteo_partner_id", partnerIdInternal);
    }

    private static void injectUserSegment(AlltrackEvent event) {
        if (userSegmentInternal == null || userSegmentInternal.isEmpty()) {
            return;
        }

        event.addPartnerParameter("user_segment", userSegmentInternal);
    }

    private static void injectCustomerId(AlltrackEvent event) {
        if (customerIdInternal == null || customerIdInternal.isEmpty()) {
            return;
        }
        event.addPartnerParameter("customer_id", customerIdInternal);
    }

    private static String createCriteoVLFromProducts(List<String> productIds) {
        if (productIds == null) {
            logger.warn("Criteo View Listing product ids list is null. It will sent as empty.");
            productIds = new ArrayList<String>();
        }
        StringBuffer criteoVLValue = new StringBuffer("[");
        int productIdsSize = productIds.size();

        if (productIdsSize > MAX_VIEW_LISTING_PRODUCTS) {
            logger.warn("Criteo View Listing should only have at most 3 product ids. The rest will be discarded.");
        }
        for (int i = 0; i < productIdsSize; ) {
            String productID = productIds.get(i);
            String productString = String.format(Locale.US, "\"%s\"", productID);
            criteoVLValue.append(productString);

            i++;

            if (i == productIdsSize || i >= MAX_VIEW_LISTING_PRODUCTS) {
                break;
            }

            criteoVLValue.append(",");
        }
        criteoVLValue.append("]");
        String result = null;
        try {
            result = URLEncoder.encode(criteoVLValue.toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("error converting criteo product ids (%s)", e.getMessage());
        }
        return result;
    }

    private static String createCriteoVBFromProducts(List<CriteoProduct> products) {
        if (products == null) {
            logger.warn("Criteo Event product list is empty. It will sent as empty.");
            products = new ArrayList<CriteoProduct>();
        }
        StringBuffer criteoVBValue = new StringBuffer("[");
        int productsSize = products.size();
        for (int i = 0; i < productsSize; ) {
            CriteoProduct criteoProduct = products.get(i);
            String productString = String.format(Locale.US, "{\"i\":\"%s\",\"pr\":%f,\"q\":%d}",
                    criteoProduct.productID,
                    criteoProduct.price,
                    criteoProduct.quantity);
            criteoVBValue.append(productString);

            i++;

            if (i == productsSize) {
                break;
            }

            criteoVBValue.append(",");
        }
        criteoVBValue.append("]");
        String result = null;
        try {
            result = URLEncoder.encode(criteoVBValue.toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("error converting criteo products (%s)", e.getMessage());
        }
        return result;
    }
}
