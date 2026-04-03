package com.ptl.linkschecker;

import com.ptl.linkschecker.commands.BadCommand;
import com.ptl.linkschecker.commands.CheckCommand;
import com.ptl.linkschecker.commands.MovedCommand;
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
	private final BadCommand badCommand;
	private final MovedCommand movedCommand;
	private final LinksCrawler linksCrawler;
	private final ProgressCounter progressCounter;

	public LinksCheckerApplication(CheckCommand checkCommand, BadCommand badCommand, MovedCommand movedCommand, LinksCrawler linksCrawler, ProgressCounter progressCounter) {
		this.checkCommand = checkCommand;
		this.badCommand = badCommand;
		this.movedCommand = movedCommand;
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
		IO.println(badCommand.bad());
		IO.println("--- MOVED LINKS ---");
		IO.println(movedCommand.good());
	}
}