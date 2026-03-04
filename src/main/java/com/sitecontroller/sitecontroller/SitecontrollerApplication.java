package com.sitecontroller.sitecontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sitecontroller.sitecontroller.epe.controls.TagMessageManagerControl;  //TO DO: Remove, used only for fakeout.

@SpringBootApplication
public class SitecontrollerApplication {

	public static void main(String[] args) {

		SpringApplication.run(SitecontrollerApplication.class, args);

		//////////////////////////////////////////////////////////////
		 //TO DO: Remove, used only for fakeout.
		var	test = new TagMessageManagerControl();		 
		 try {
			test.init();
			test.activate();
		} catch (Exception e) {			
			e.printStackTrace();
		}
		//////////////////////////////////////////////////////////////
	}
}
