package com.dara.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OtpManager {

    //  All keys applied
    private static final String SERVICE_ID = "service_lj8gq0n";
    private static final String TEMPLATE_ID = "template_wn9l47e";
    private static final String PUBLIC_KEY = "o7aIiTTg8M6ldX6a0";
    private static final String PRIVATE_KEY = "BF87-z0jkhbBIECHM4M3YckBb31CG8GwRxqyViIFq3L07zAr3PP1xDHXL1o_wBWERx56yXR_slPr4Rm0PP53cMc"; // Added for authorization

    private static final String PREFS_NAME = "OtpPrefs";
    private static final String KEY_OTP = "current_otp";
    private static String generatedOtp = "";
    private static final OkHttpClient client = new OkHttpClient();

    // Generate a 6-digit OTP and save it to SharedPreferences
    public static String generateOtp(Context context) {
        generatedOtp = String.format(Locale.US, "%06d", new Random().nextInt(999999));

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_OTP, generatedOtp).apply();

        return generatedOtp;
    }

    // Send OTP via EmailJS REST API
    public static void sendOtp(String toEmail, String otp, Callback callback) {
        try {
            JSONObject body = createEmailBody(toEmail, otp);
            Request request = buildEmailRequest(body);
            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.e("OtpManager", "PREPARE ERROR: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }

    private static JSONObject createEmailBody(String toEmail, String otp) throws Exception {
        JSONObject params = new JSONObject();
        params.put("to_email", toEmail);
        params.put("otp_code", otp);
        params.put("email", toEmail); // keep original for fallback
        params.put("passcode", otp); // keep original for fallback
        params.put("seconds", getExpiryTime());
        params.put("name", "Dara");
        params.put("message", "Hello from Android App");

        JSONObject body = new JSONObject();
        body.put("service_id", SERVICE_ID);
        body.put("template_id", TEMPLATE_ID);
        body.put("user_id", PUBLIC_KEY);
        body.put("accessToken", PRIVATE_KEY);
        body.put("template_params", params);
        return body;
    }

    private static Request buildEmailRequest(JSONObject body) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(body.toString(), mediaType);

        return new Request.Builder()
                .url("https://api.emailjs.com/api/v1.0/email/send")
                .post(requestBody)
                .build();
    }

    // Verify the OTP
    public static boolean verifyOtp(Context context, String enteredOtp) {
        if (generatedOtp.isEmpty()) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            generatedOtp = prefs.getString(KEY_OTP, "");
        }
        return !generatedOtp.isEmpty() && generatedOtp.equals(enteredOtp);
    }

    private static String getExpiryTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        return sdf.format(cal.getTime());
    }
}
