package com.heima.material.test;

import com.codahale.metrics.Gauge;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.junit.Test;

/**
 * @description:
 * @author: 16420
 * @time: 2023/1/12 14:40
 */
public class BaseTest {

    @Test
    public void test01() {
        WmMaterial wmMaterial = new WmMaterial();
        Gauge<Short> getType = wmMaterial::getType;

    }
}
