package kong.unirest.core;

import org.junit.jupiter.api.Test;

import java.net.URLEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class QueryParamsTest {
    @Test
    void toStringTest() {
        var params = new QueryParams.NameValuePair("foo", "bar");
        assertThat(params.toString()).isEqualTo("foo=bar");
    }
}