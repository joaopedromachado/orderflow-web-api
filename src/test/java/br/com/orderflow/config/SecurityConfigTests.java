package br.com.orderflow.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTests {

    @Test
    void loadsRsaKeysFromClasspathResources() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        ReflectionTestUtils.setField(securityConfig, "privateKeyResource", new ClassPathResource("rsa/server.key"));
        ReflectionTestUtils.setField(securityConfig, "publicKeyResource", new ClassPathResource("rsa/app.pub"));

        assertThat(securityConfig.privateKey()).isNotNull();
        assertThat(securityConfig.publicKey()).isNotNull();
    }
}
