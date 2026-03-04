package com.sitecontroller.sitecontroller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import static org.mockito.BDDMockito.given;
import com.sitecontroller.sitecontroller.repository.impl.RedisRepository;

//Inspired by https://www.baeldung.com/spring-boot-redis-cache

@SpringBootTest
@Import({ RedisRepository.class})
@EnableCaching

class RedisIntegrationTest {

    @Mock
    private RedisRepository mockRedisRepository;

    @Test
    void getGatewaySyncValueTest(String deviceEui) {
        
        String fakeId = "123";
        String strReturnedDevice = null;
        given(mockRedisRepository.getGatewaySyncValue(fakeId)).willReturn(strReturnedDevice);       
    }
   
    @Test
    void setPendingGatewaySyncValueTest(String deviceEui){
        String fakeId = "123";
        given(mockRedisRepository.setPendingGatewaySyncValue(fakeId)).willReturn("TO DO");       
    }       
}


