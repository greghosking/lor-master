package ghosking.lormaster;

// IMPORT OKHTTP
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class RequestHandler {

    // API key expires Feb. 19 @ 1:00PM.
    public static final String API_KEY = "RGAPI-db4f66d4-cb4d-421b-8463-9c46d4aedd2d";

    /**
     * Makes a GET request to a specified endpoint of the Riot Games API.
     *
     * @param url The URL of the request.
     * @return The body of the JSON response as a string or null if the request fails.
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
                    return response.body().string();
                // Response code 400: Bad request.
                // case 400:
                // Response code 401: Unauthorized.
                // case 401:
                // Response code 403: Forbidden.
                case 403:
                    System.out.println("Error processing request: " + request.toString());
                    System.out.println("Authentication error! Invalid API key.");
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
