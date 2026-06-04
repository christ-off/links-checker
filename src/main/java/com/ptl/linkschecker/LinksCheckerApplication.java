package com.ptl.linkschecker;

import com.ptl.linkschecker.commands.CheckCommand;
import com.ptl.linkschecker.commands.LinkListCommand;
import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
public class LinksCheckerApplication implements ApplicationRunner {

	private final CheckCommand checkCommand;
	private final LinkListCommand linkListCommand;
	private final LinksCrawler linksCrawler;
	private final ProgressCounter progressCounter;

	public LinksCheckerApplication(CheckCommand checkCommand, LinkListCommand linkListCommand, LinksCrawler linksCrawler, ProgressCounter progressCounter) {
		this.checkCommand = checkCommand;
		this.linkListCommand = linkListCommand;
		this.linksCrawler = linksCrawler;
		this.progressCounter = progressCounter;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			IO.println("Usage: links-checker <website>");
			System.exit(1);
		}
		SpringApplication.run(LinksCheckerApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		String website = args.getNonOptionArgs().getFirst();
		Instant start = Instant.now();
		String result = checkCommand.check(website);
		IO.println(result);
		Duration elapsed = Duration.between(start, Instant.now());
		long seconds = elapsed.toSeconds();
		int linkCount = linksCrawler.getLinks().size();
		long linksPerSecond = seconds > 0 ? linkCount / seconds : linkCount;
		IO.println("Time: " + seconds + "s | Links/s: " + linksPerSecond);
		progressCounter.printHostStats(linksCrawler.getQueriesPerHost());
		IO.println("--- BAD LINKS ---");
		IO.println(linkListCommand.badLinks());
		IO.println("--- MOVED LINKS ---");
		IO.println(linkListCommand.movedLinks());
	}
}
