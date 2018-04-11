package BehaviorTests;

import io.github.openunirest.http.Unirest;
import util.NoopCallback;
import org.junit.Test;

import static util.MockCallback.json;

public class CallbackFutureTest extends BddTest {

    @Test(timeout = 5000)
    public void onSuccess() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("Snazzy", "Shoes")
                .asJsonAsync()
                .thenAccept(r -> {
                    parse(r).assertParam("Snazzy", "Shoes");
                    asyncSuccess();
                }).get();

        assertAsync();
    }

    @Test(timeout = 5000)
    public void onSuccessSupplyCallback() throws Exception {
        Unirest.get(MockServer.GET)
                .queryString("Snazzy", "Shoes")
                .asJsonAsync(new NoopCallback<>())
                .thenAccept(r -> {
                    parse(r).assertParam("Snazzy", "Shoes");
                    asyncSuccess();
                }).get();

        assertAsync();
    }

    @Test(timeout = 5000)
    public void onFailure() throws Exception {
        Unirest.get("http://localhost:0000")
                .asJsonAsync(json(this))
                .isCompletedExceptionally();

        assertFailed("java.net.ConnectException: Connection refused");
    }
}