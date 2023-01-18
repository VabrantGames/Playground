package com.vabrant.playground.test;

import com.vabrant.playground.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"hello:world", "hello", "hello:world:world:hello", ":hello:world"})
    public void splitTest(String str) {
        assertArrayEquals(str.split(":"), Utils.splitByChar(str.toCharArray(), ':'));
    }
}
