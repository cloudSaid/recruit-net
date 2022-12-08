package com.imooc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-08 17:56
 */
@Component
@Data
@PropertySource("classpath:ReleasePath.properties")
@ConfigurationProperties(prefix = "releases")
public class ReleasePathProperties {

    private List path;
}
