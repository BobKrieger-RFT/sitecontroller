package com.sitecontroller.sitecontroller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Bob's test commit, 3:22PM ....

@SpringBootTest
@AutoConfigureMockMvc

class SitecontrollerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
	void contextLoads() {}
  
	@Test
	public void testGetDeviceRetrievalServiceInfo() throws Exception {
		
	     //TO DO: Check the endpoint.
 	     mockMvc.perform(get("/device-retrieval-service/devices/{devEui}", "some-dev-eui")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());                
	}

	public void testSyncToRedis() throws Exception {
				
	}
}