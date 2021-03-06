package com.winger.webtv;

import com.winger.webtv.service.ServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebTvApplication {

	@Autowired
	ServiceManager serviceManager;

	public static void main(String[] args) {
		SpringApplication.run(WebTvApplication.class, args);
	}

	@Bean
	public CommandLineRunner init (){
		return args -> {
			if (System.getenv().get("REFRESH").equals("1")) {
				System.out.println("REFRESH Content in progress");
				serviceManager.refreshContent();
				System.out.println("Refresh is DONE");
			}
			else System.out.println("No refresh requested");
			serviceManager.fillCacheList();
		};
	}

}
