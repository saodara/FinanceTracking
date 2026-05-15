package com.dara.app;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MailHelper {

    // ✅ Public Key applied from your previous message
    private static final String EMAILJS_USER_ID = "o7aIiTTg8M6ldX6a0";

    // ⏳ Still needed from your EmailJS dashboard:
    private static final String EMAILJS_SERVICE_ID = "service_lj8gq0n";   // e.g., "service_xxxx"
    private static final String EMAILJS_TEMPLATE_ID = "template_wn9l47e"; // e.g., "template_xxxx"
    private static final String EMAILJS_ACCESS_TOKEN = "BF87-z0jkhbBIECHM4M3YckBb31CG8GwRxqyViIFq3L07zAr3PP1xDHXL1o_wBWERx56yXR_slPr4Rm0PP53cMc";

    private static final OkHttpClient client = new OkHttpClient();

    public static void sendOtpEmail(String recipientEmail, String otp) {
        try {
            // Create JSON payload for EmailJS
            JSONObject templateParams = new JSONObject();
            templateParams.put("to_email", recipientEmail);
            templateParams.put("otp_code", otp);

            JSONObject payload = new JSONObject();
            payload.put("service_id", EMAILJS_SERVICE_ID);
            payload.put("template_id", EMAILJS_TEMPLATE_ID);
            payload.put("user_id", EMAILJS_USER_ID);
            payload.put("accessToken", EMAILJS_ACCESS_TOKEN);
            payload.put("template_params", templateParams);

            RequestBody body = RequestBody.create(
                    payload.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url("https://api.emailjs.com/api/v1.0/email/send")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("MailHelper", "EmailJS Error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d("MailHelper", "OTP sent successfully to " + recipientEmail);
                    } else {
                        Log.e("MailHelper", "EmailJS Error Code: " + response.code() + " " + response.body().string());
                    }
                }
            });

        } catch (Exception e) {
            Log.e("MailHelper", "Error preparing EmailJS request: " + e.getMessage());
        }
    }
}