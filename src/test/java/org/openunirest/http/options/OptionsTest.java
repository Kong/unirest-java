package org.openunirest.http.options;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

public class OptionsTest {

    @Before
    public void before(){
        Options.reset();
    }

    @Test
    public void shouldKeepConnectionTimeOutDefault(){
        assertOpDefault(Options.CONNECTION_TIMEOUT, Option.CONNECTION_TIMEOUT, 4L);
    }

    @Test
    public void shouldKeepSocketTimeoutDefault(){
        assertOpDefault(Options.SOCKET_TIMEOUT, Option.SOCKET_TIMEOUT, 4L);
    }

    @Test
    public void shouldKeepMaxTotalDefault(){
        assertOpDefault(Options.MAX_TOTAL, Option.MAX_TOTAL, 4);
    }

    @Test
    public void shouldKeepMaxPerRouteDefault(){
        assertOpDefault(Options.MAX_PER_ROUTE, Option.MAX_PER_ROUTE, 4);
    }

    private void assertOpDefault(Object defValue, Option option, Object newValue) {
        assertEquals(defValue, Options.getOption(option));
        Options.setOption(option, newValue);
        assertEquals(newValue, Options.getOption(option));
    }

    @Test
    public void shouldReuseThreadPool() {
        int startingCount = ManagementFactory.getThreadMXBean().getThreadCount();
        IntStream.range(0,100).forEach(i -> Options.refresh());
        assertThat(ManagementFactory.getThreadMXBean().getThreadCount(), is(lessThan(startingCount + 10)));
    }
}