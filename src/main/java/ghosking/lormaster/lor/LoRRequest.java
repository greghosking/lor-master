package ghosking.lormaster.lor;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Date;

public final class LoRRequest {

    public static final String API_KEY = "RGAPI-44ce4cff-2c9a-4fad-aa14-11edb3bf0f46";

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
                    response.body().close();
                    throw new AccessForbiddenException("Error processing request: " + request.toString());

                // Response code 404: Data not found.
                 case 404:
                     response.body().close();
                     throw new DataNotFoundException("Error processing request: " + request.toString());
                // Response code 405: Method not allowed.
                // case 405:
                // Response code 415: Unsupported media type.
                // case 415:
                // Response code 429: Rate limit exceeded.
                case 429:
                    response.body().close();
                    throw new RateLimitExceededException("Error processing request: " + request.toString());
                // Response code 500: Internal server error.
                // case 500:
                // Response code 502: Bad gateway.
                // case 502:
                // Response code 503: Service unavailable.
                // case 503:
                // Response code 504: Gateway timeout.
                // case 504:
            }
        }
        catch (AccessForbiddenException | DataNotFoundException | RateLimitExceededException ignored) {
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    public static class AccessForbiddenException extends Exception {
        public AccessForbiddenException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class DataNotFoundException extends Exception {
        public DataNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class RateLimitExceededException extends Exception {
        public RateLimitExceededException(String errorMessage) {
            super(errorMessage);
        }
    }
}
