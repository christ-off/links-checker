package com.ptl.linkschecker;

import com.ptl.linkschecker.commands.CheckCommand;
import com.ptl.linkschecker.commands.LinkListCommand;
import com.ptl.linkschecker.core.LinksCrawler;
import com.ptl.linkschecker.domain.PageResult;
import com.ptl.linkschecker.utils.ProgressCounter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

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
		List<String> websites = args.getNonOptionArgs();
		if (websites.isEmpty()) {
			IO.println("Usage: links-checker <website>");
			System.exit(1);
		}
		Instant start = Instant.now();
		String result = checkCommand.check(websites.getFirst());
		IO.println(result);
		Duration elapsed = Duration.between(start, Instant.now());
		List<PageResult> links = linksCrawler.getLinks();
		double linksPerSecond = links.size() * 1000.0 / Math.max(1, elapsed.toMillis());
		IO.println("Time: " + elapsed.toSeconds() + "s | Links/s: " + String.format(Locale.ROOT, "%.1f", linksPerSecond));
		progressCounter.printHostStats(linksCrawler.getQueriesPerHost());
		IO.println("--- BAD LINKS ---");
		IO.println(linkListCommand.badLinks(links));
		IO.println("--- MOVED LINKS ---");
		IO.println(linkListCommand.movedLinks(links));
	}
}
