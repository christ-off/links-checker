package com.ptl.linkschecker;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan
public class LinksCheckerApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(LinksCheckerApplication.class);
		application.setBannerMode(Banner.Mode.OFF);
		application.run(args);
	}

}
