package kong.unirest.core;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.signer.Aws4Signer;

import java.time.*;

import static java.net.URI.create;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

public class AwsSignerCompatibilityTest {

     static final Clock CLOCK = Clock.fixed(LocalDate.of(1972, 9, 28).atStartOfDay().toInstant(UTC), UTC);
     static final String ACCESS_KEY = "YOUR_ACCESS_KEY";
     static final String SECRET_KEY = "YOUR_SECRET_KEY";
     static final String US_EAST_1 = "us-east-1";
     static final String SERVICE = "execute-api";
     static final String API_GATEWAY_HOST = "your-api-id.execute-api.us-east-1.amazonaws.com";
     static final String PATH = "/your/resource/path";
     static final String URI = "https://" + API_GATEWAY_HOST + PATH;

    @Test
    void canonicalRequestString() {
        var req = Unirest.get(URI);
        var uniSign = new AwsSignerV4.CanonicalRequest(req, ACCESS_KEY, SECRET_KEY, US_EAST_1, SERVICE, CLOCK);

        assertThat(uniSign.toString())
                .isEqualTo("GET\n" +
                        "/your/resource/path\n" +
                        "\n" +
                        "host:your-api-id.execute-api.us-east-1.amazonaws.com\n" +
                        "x-amz-date:19720928T000000Z\n" +
                        "\n" +
                        "host;x-amz-date\n" +
                        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    }

    @Test
    void canonicalRequestStringWithHeaders() {
        var req = Unirest.get(URI).header("fruit", "apple").header("fruit", "orange");
        var uniSign = new AwsSignerV4.CanonicalRequest(req, ACCESS_KEY, SECRET_KEY, US_EAST_1, SERVICE, CLOCK);

        assertThat(uniSign.toString())
                .isEqualTo("GET\n" +
                        "/your/resource/path\n" +
                        "\n" +
                        "fruit:apple,orange\n" +
                        "host:your-api-id.execute-api.us-east-1.amazonaws.com\n" +
                        "x-amz-date:19720928T000000Z\n" +
                        "\n" +
                        "fruit;host;x-amz-date\n" +
                        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    }

    @Test
    void withQueryStrings() {
        var req = Unirest.get(URI).queryString("foo", "bar").queryString("a/s", "with&things");
        var uniSign = new AwsSignerV4.CanonicalRequest(req, ACCESS_KEY, SECRET_KEY, US_EAST_1, SERVICE, CLOCK);

        assertThat(uniSign.toString())
                .isEqualTo("GET\n" +
                        "/your/resource/path\n" +
                        "a%2Fs=with%26things&foo=bar\n" +
                        "host:your-api-id.execute-api.us-east-1.amazonaws.com\n" +
                        "x-amz-date:19720928T000000Z\n" +
                        "\n" +
                        "host;x-amz-date\n" +
                        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");

    }

    @Test
    void signedAuthHeader() {
        var sig = getAwsLibSig();

        var uniSign = new AwsSignerV4.CanonicalRequest(Unirest.get(URI), ACCESS_KEY, SECRET_KEY, US_EAST_1, SERVICE, CLOCK);

        var uni = uniSign.getAuthHeader();

         assertThat(sig)
                 .isEqualTo("AWS4-HMAC-SHA256 Credential=YOUR_ACCESS_KEY/19720928/us-east-1/execute-api/aws4_request, SignedHeaders=host;x-amz-date, Signature=db5680ef20e124f670e335155527ad1d7f510d4da7be32ca4c668f39b35a535d");

         assertThat(uni)
                 .isEqualTo(sig);
    }

    private static String getAwsLibSig() {

        var unsignedRequest = SdkHttpFullRequest.builder()
                .method(SdkHttpMethod.GET)
                .uri(create(URI))
                .putHeader("Host", API_GATEWAY_HOST)
                .build();

        var params = Aws4SignerParams.builder()
                .signingRegion(Region.of(US_EAST_1))
                .signingName(SERVICE)
                .awsCredentials(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY))
                .signingClockOverride(CLOCK)
                .build();

        var signedRequest = Aws4Signer.create().sign(unsignedRequest, params);

        return signedRequest.firstMatchingHeader("Authorization").orElseThrow();
    }

    private static void rando() {
        var unsignedRequest = SdkHttpFullRequest.builder()
                .method(SdkHttpMethod.GET)
                .uri(create(URI))
                .putHeader("Host", API_GATEWAY_HOST)
                .appendRawQueryParameter("foo", "bar")
                .appendRawQueryParameter("a/s", "with&things")
                .build();

        var params = Aws4SignerParams.builder()
                .signingRegion(Region.of(US_EAST_1))
                .signingName(SERVICE)
                .awsCredentials(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY))
                .signingClockOverride(CLOCK)
                .build();

        Aws4Signer.create().sign(unsignedRequest, params);

    }
}
