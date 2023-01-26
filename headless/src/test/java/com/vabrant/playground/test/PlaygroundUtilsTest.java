package com.vabrant.playground.test;

import com.vabrant.playground.PlaygroundUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PlaygroundUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"hello:world", "hello", "hello:world:world:hello", ":hello:world"})
    public void splitTest(String str) {
        assertArrayEquals(str.split(":"), PlaygroundUtils.splitByChar(str.toCharArray(), ':'));
    }
}
