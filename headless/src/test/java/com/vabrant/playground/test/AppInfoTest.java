package com.vabrant.playground.test;

import com.moandjiezana.toml.Toml;
import com.vabrant.playground.AppInfo;
import org.junit.jupiter.api.Test;

import java.io.File;

public class AppInfoTest {

    @Test
    public void basic() {
        AppInfo info = new AppInfo();
        Toml t = new Toml().read(new File("src/test/java/resources/settingsMockup.toml"));

        info.load(t.toMap());

        System.out.println(info.getProjectNames("testplayground").toString());
    }
}
