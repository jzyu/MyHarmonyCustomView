package com.example.demo00.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LogTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    public static String replaceFormat(String logMessageFormat) {
        return logMessageFormat
                .replaceAll("%([d|f|s])", "%{public}$1");
                //.replaceAll("%f", "%{public}f")
                //.replaceAll("%s", "%{public}s");
    }

    @Test
    public void replaceFormat() {
        Assert.assertEquals("%{public}d", replaceFormat("%d"));
        //Assert.assertEquals("%{public}02d", replaceFormat("%02d"));
        Assert.assertEquals("width=%{public}d, height=%{public}f, text=%{public}s",  replaceFormat("width=%d, height=%f, text=%s"));
    }
}