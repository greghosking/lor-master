package ghosking.lormaster.lor;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public final class LoRRequest {

    // API key expires Fri, March 25, 2022 @ 9:30PM.
    public static final String apiKey = "RGAPI-2e7633cf-777d-4e6d-97b0-488ac8d2ce0c";

    /**
     * @param url The URL of the request.
     * @return The body of the JSON response as a string or null if the request errs.
     */
    public static String get(String url) {

        OkHttpClient client = new OkHttpClient();

        // Try to send a new request to the specified endpoint.
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            int code = response.code();

            switch (code) {
                // Response code 200: OK.
                case 200:
                    String result = response.body().string();
                    response.body().close();
                    return result;
                // Response code 400: Bad request.
                // case 400:
                // Response code 401: Unauthorized.
                // case 401:
                // Response code 403: Forbidden.
                case 403:
                    System.out.println("Error processing request: " + request.toString());
                    System.out.println("Authentication error! Invalid API key.");
                    response.body().close();
                    return null;
                // Response code 404: Data not found.
                // case 404:
                // Response code 405: Method not allowed.
                // case 405:
                // Response code 415: Unsupported media type.
                // case 415:
                // Response code 429: Rate limit exceeded.
                case 429:
                    System.out.println("Error processing request: " + request.toString());
                    System.out.println("Rate limit exceeded! Development rate limit: "
                            + "20 requests every 1 second, 100 requests every 2 minutes.");
                    response.body().close();
                    return null;
                // Response code 500: Internal server error.
                // case 500:
                // Response code 502: Bad gateway.
                // case 502:
                // Response code 503: Service unavailable.
                // case 503:
                // Response code 504: Gateway timeout.
                // case 504:
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        return null;
    }
}
