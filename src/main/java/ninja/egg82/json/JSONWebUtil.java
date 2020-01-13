package ninja.egg82.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class JSONWebUtil {
    private JSONWebUtil() { }

    public static JSONArray getJSONArray(URL url) throws IOException, ParseException, ClassCastException { return getJSONArray(url, null, 5000, null, null, null); }

    public static JSONArray getJSONArray(URL url, String method) throws IOException, ParseException, ClassCastException { return getJSONArray(url, method, 5000, null, null, null); }

    public static JSONArray getJSONArray(URL url, String method, int timeout) throws IOException, ParseException, ClassCastException { return getJSONArray(url, method, timeout, null, null, null); }

    public static JSONArray getJSONArray(URL url, String method, int timeout, String userAgent) throws IOException, ParseException, ClassCastException { return getJSONArray(url, method, timeout, userAgent, null, null); }

    public static JSONArray getJSONArray(URL url, String method, int timeout, String userAgent, Map<String, String> headers) throws IOException, ParseException, ClassCastException { return getJSONArray(url, method, timeout, userAgent, headers, null); }

    public static JSONArray getJSONArray(URL url, String method, int timeout, String userAgent, Map<String, String> headers, Map<String, String> postData) throws IOException, ParseException, ClassCastException {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Accept", "application/json");

        return JSONUtil.parseArray(getString(url, method, timeout, userAgent, headers, postData));
    }

    public static JSONObject getJSONObject(URL url) throws IOException, ParseException, ClassCastException { return getJSONObject(url, null, 5000, null, null, null); }

    public static JSONObject getJSONObject(URL url, String method) throws IOException, ParseException, ClassCastException { return getJSONObject(url, method, 5000, null, null, null); }

    public static JSONObject getJSONObject(URL url, String method, int timeout) throws IOException, ParseException, ClassCastException { return getJSONObject(url, method, timeout, null, null, null); }

    public static JSONObject getJSONObject(URL url, String method, int timeout, String userAgent) throws IOException, ParseException, ClassCastException { return getJSONObject(url, method, timeout, userAgent, null, null); }

    public static JSONObject getJSONObject(URL url, String method, int timeout, String userAgent, Map<String, String> headers) throws IOException, ParseException, ClassCastException { return getJSONObject(url, method, timeout, userAgent, headers, null); }

    public static JSONObject getJSONObject(URL url, String method, int timeout, String userAgent, Map<String, String> headers, Map<String, String> postData) throws IOException, ParseException, ClassCastException {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Accept", "application/json");

        return JSONUtil.parseObject(getString(url, method, timeout, userAgent, headers, postData));
    }

    public static String getString(URL url) throws IOException { return getString(url, null, 5000, null, null, null); }

    public static String getString(URL url, String method) throws IOException { return getString(url, method, 5000, null, null, null); }

    public static String getString(URL url, String method, int timeout) throws IOException { return getString(url, method, timeout, null, null, null); }

    public static String getString(URL url, String method, int timeout, String userAgent) throws IOException { return getString(url, method, timeout, userAgent, null, null); }

    public static String getString(URL url, String method, int timeout, String userAgent, Map<String, String> headers) throws IOException { return getString(url, method, timeout, userAgent, headers, null); }

    public static String getString(URL url, String method, int timeout, String userAgent, Map<String, String> headers, Map<String, String> postData) throws IOException {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Connection", "close");
        headers.put("Accept-Language", "en-US,en;q=0.8");

        try (InputStream in = getInputStream(url, method, timeout, userAgent, headers, postData); InputStreamReader reader = new InputStreamReader(in); BufferedReader buffer = new BufferedReader(reader)) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
    }

    public static HttpURLConnection getConnection(URL url) throws IOException { return getConnection(url, null, 5000, null, null, null); }

    public static HttpURLConnection getConnection(URL url, String method) throws IOException { return getConnection(url, method, 5000, null, null, null); }

    public static HttpURLConnection getConnection(URL url, String method, int timeout) throws IOException { return getConnection(url, method, timeout, null, null, null); }

    public static HttpURLConnection getConnection(URL url, String method, int timeout, String userAgent) throws IOException { return getConnection(url, method, timeout, userAgent, null, null); }

    public static HttpURLConnection getConnection(URL url, String method, int timeout, String userAgent, Map<String, String> headers) throws IOException { return getConnection(url, method, timeout, userAgent, headers, null); }

    public static HttpURLConnection getConnection(URL url, String method, int timeout, String userAgent, Map<String, String> headers, Map<String, String> postData) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null.");
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setConnectionProperties(conn, method, timeout, userAgent, headers, postData, null);

        int status;
        boolean redirect;
        do {
            status = conn.getResponseCode();
            redirect = status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER;

            if (redirect) {
                String newUrl = conn.getHeaderField("Location");
                String cookies = conn.getHeaderField("Set-Cookie");

                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                setConnectionProperties(conn, method, timeout, userAgent, headers, postData, cookies);
            }
        } while (redirect);

        return conn;
    }

    private static void setConnectionProperties(HttpURLConnection conn, String method, int timeout, String userAgent, Map<String, String> headers, Map<String, String> postData, String cookies) throws IOException {
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);

        if (method != null && !method.isEmpty()) {
            conn.setRequestMethod(method);
        }
        if (userAgent != null && !userAgent.isEmpty()) {
            conn.setRequestProperty("User-Agent", userAgent);
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> kvp : headers.entrySet()) {
                conn.setRequestProperty(kvp.getKey(), kvp.getValue());
            }
        }
        if (cookies != null && !cookies.isEmpty()) {
            conn.setRequestProperty("Cookie", cookies);
        }
        if (postData != null) {
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            StringBuilder data = new StringBuilder();
            for (Map.Entry<String, String> kvp : postData.entrySet()) {
                data.append(URLEncoder.encode(kvp.getKey(), StandardCharsets.UTF_8.name()));
                data.append('=');
                data.append(URLEncoder.encode(kvp.getValue(), StandardCharsets.UTF_8.name()));
                data.append('&');
            }
            if (data.length() > 0) {
                data.deleteCharAt(data.length() - 1);
            }
            byte[] dataBytes = data.toString().getBytes(StandardCharsets.UTF_8);
            conn.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(dataBytes);
        }
    }

    public static InputStream getInputStream(URL url, String method, int timeout, String userAgent, Map<String, String> headers, Map<String, String> postData) throws IOException {
        HttpURLConnection conn = getConnection(url, method, timeout, userAgent, headers, postData);
        int status = conn.getResponseCode();

        if (status >= 400 && status < 600) {
            // 400-500 errors
            throw new IOException("Server returned status code " + status);
        }

        return conn.getInputStream();
    }
}
