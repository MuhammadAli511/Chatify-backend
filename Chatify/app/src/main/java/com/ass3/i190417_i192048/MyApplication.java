package com.ass3.i190417_i192048;

import android.app.Application;
import android.content.Intent;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "96f1721c-2f55-4ea1-9e50-8f3f29fc47e5";

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.setNotificationOpenedHandler(new MyNotificationOpenedHandler(this));
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();
    }

    public class MyNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {

        private Application application;
        public MyNotificationOpenedHandler(Application application) {
            this.application = application;
        }

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            JSONObject data = result.getNotification().getAdditionalData();
            if (data != null) {
                String myCustomData0 = data.optString("senderId", null);
                String myCustomData1 = data.optString("senderName", null);
                String myCustomData2 = data.optString("senderImage", null);
//                Intent intent = new Intent(application, ChatDetailActivity.class);
//                intent.putExtra("userID", myCustomData0);
//                intent.putExtra("userName", myCustomData1);
//                intent.putExtra("profileURL", myCustomData2);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                application.startActivity(intent);
            }
        }
    }
}