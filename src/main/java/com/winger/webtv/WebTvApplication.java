package com.winger.webtv;

import com.winger.webtv.service.ServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Objects;

@SpringBootApplication
public class WebTvApplication {
	@Autowired
	Environment environment;
	@Autowired
	ServiceManager serviceManager;

	public static void main(String[] args) {
		SpringApplication.run(WebTvApplication.class, args);
	}

	@Bean
	public CommandLineRunner init (){
		return args -> {
			String refresh = environment.containsProperty("REFRESH") ? environment.getProperty("REFRESH") : "0";
			int pagesToLoad = environment.containsProperty("MAX_PAGE") ? Integer.parseInt(environment.getProperty("MAX_PAGE")) : 1;

			System.out.println("Refresh is = " + refresh);
			System.out.println("Pages to load is = " + pagesToLoad);

			if (refresh.equals("1")) {
				System.out.println("REFRESH Content in progress");
				serviceManager.refreshContent(pagesToLoad);
				System.out.println("Refresh is DONE");
			}
			else System.out.println("No refresh requested");
			serviceManager.fillCacheList();
		};
	}

}
