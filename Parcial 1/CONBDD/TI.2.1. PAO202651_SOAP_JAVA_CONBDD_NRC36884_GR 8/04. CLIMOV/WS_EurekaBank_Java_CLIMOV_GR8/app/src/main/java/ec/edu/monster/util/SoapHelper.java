package ec.edu.monster.util;


import android.os.Build;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SoapHelper {

    private static final String TAG = "SoapHelper";
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";

    // IP of the PC running the SOAP server, used only on a physical device.
    // Windows: `ipconfig` (IPv4 address). PC and phone must share the same network.
    private static final String PC_IP = "10.40.73.239";
    private static final String SERVER_PORT = "8080";
    private static final String SERVER_CONTEXT = "WS_EurekaBank_Java_GR8/";

    // Emulator -> 10.0.2.2 (host PC loopback); physical device -> PC_IP.
    private static final String BASE_URL = "http://"
            + (isEmulator() ? "10.0.2.2" : PC_IP) + ":" + SERVER_PORT + "/" + SERVER_CONTEXT;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();
    private static final MediaType XML = MediaType.parse("text/xml; charset=utf-8");

    /**
     * Calls a SOAP operation and returns the raw response envelope.
     *
     * @throws IOException when the server cannot be reached or answers with a non-2xx status,
     *                     so callers can tell a connection problem apart from a business answer.
     */
    public static String callWebService(String serviceName, String methodName, String soapBody)
            throws IOException {
        String soapAction = NAMESPACE + methodName;
        String soapRequest = buildSoapEnvelope(methodName, soapBody);
        String url = BASE_URL + serviceName;

        RequestBody body = RequestBody.create(soapRequest, XML);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("SOAPAction", "\"" + soapAction + "\"")
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("HTTP " + response.code() + " desde " + url);
            }
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "Error llamando a " + url + " (" + methodName + ")", e);
            throw e;
        }
    }

    /** Escapes XML special characters so user input cannot break the SOAP envelope. */
    public static String escapeXml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String buildSoapEnvelope(String methodName, String soapBody) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                " xmlns:ws=\"" + NAMESPACE + "\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ws:" + methodName + ">" +
                soapBody +
                "</ws:" + methodName + ">" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.FINGERPRINT.contains("sdk_gphone")
                || Build.HARDWARE.contains("ranchu")
                || Build.HARDWARE.contains("goldfish")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk");
    }

    public static String getNamespace() {
        return NAMESPACE;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
