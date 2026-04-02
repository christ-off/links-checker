package com.ptl.linkschecker;

import com.ptl.linkschecker.commands.BadCommand;
import com.ptl.linkschecker.commands.CheckCommand;
import com.ptl.linkschecker.commands.MovedCommand;
import com.ptl.linkschecker.core.LinksCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
public class LinksCheckerApplication implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(LinksCheckerApplication.class);

	private final CheckCommand checkCommand;
	private final BadCommand badCommand;
	private final MovedCommand movedCommand;
	private final LinksCrawler linksCrawler;

	public LinksCheckerApplication(CheckCommand checkCommand, BadCommand badCommand, MovedCommand movedCommand, LinksCrawler linksCrawler) {
		this.checkCommand = checkCommand;
		this.badCommand = badCommand;
		this.movedCommand = movedCommand;
		this.linksCrawler = linksCrawler;
	}

	static void main(String[] args) {
		if (args.length != 1) {
			log.error("Usage: links-checker <website>");
			System.exit(1);
		}
		SpringApplication.run(LinksCheckerApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		String website = args.getNonOptionArgs().getFirst();
		Instant start = Instant.now();
		String result = checkCommand.check(website);
		log.info("{}", result);
		Duration elapsed = Duration.between(start, Instant.now());
		long seconds = elapsed.toSeconds();
		int linkCount = linksCrawler.getLinks().size();
		long linksPerSecond = seconds > 0 ? linkCount / seconds : linkCount;
        if (log.isInfoEnabled()) {
			log.info("Time: {}s | Links/s: {}", seconds, linksPerSecond);
			log.info("--- BAD LINKS ---");
			log.info("{}", badCommand.bad());
			log.info("--- MOVED LINKS ---");
            log.info("{}", movedCommand.good());
        }
    }
}