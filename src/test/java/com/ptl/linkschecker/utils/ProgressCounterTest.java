package com.ptl.linkschecker.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProgressCounterTest {

    private final ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(outCapture));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void should_print_dot_for_good_status() {
        ProgressCounter counter = new ProgressCounter();
        counter.tick(200, false, false, false);
        assertTrue(outCapture.toString().contains("."));
    }

    @Test
    void should_print_4_for_client_error() {
        ProgressCounter counter = new ProgressCounter();
        counter.tick(404, false, false, false);
        assertTrue(outCapture.toString().contains("4"));
    }

    @Test
    void should_print_5_for_server_error() {
        ProgressCounter counter = new ProgressCounter();
        counter.tick(503, false, false, false);
        assertTrue(outCapture.toString().contains("5"));
    }

    @Test
    void should_print_I_for_internal_link() {
        ProgressCounter counter = new ProgressCounter();
        counter.tick(200, true, false, false);
        assertTrue(outCapture.toString().contains("I"));
    }

    @Test
    void should_print_percent_for_internal_link_with_percent() {
        ProgressCounter counter = new ProgressCounter();
        counter.tick(200, true, false, true);
        assertTrue(outCapture.toString().contains("%"));
    }

    @Test
    void should_print_s_for_skipped_link() {
        ProgressCounter counter = new ProgressCounter();
        counter.tick(200, false, true, false);
        assertTrue(outCapture.toString().contains("s"));
    }

    @Test
    void should_wrap_to_new_line_every_100_symbols() {
        ProgressCounter counter = new ProgressCounter();
        for (int i = 0; i < 100; i++) {
            counter.tick(200, false, false, false);
        }
        String output = outCapture.toString();
        assertTrue(output.contains(" - 100"), "should print line number after 100 ticks");
    }

    @Test
    void should_print_host_stats() {
        ProgressCounter counter = new ProgressCounter();
        counter.printHostStats(Map.of("example.com", 10L, "localhost", 5L));
        String output = outCapture.toString();
        assertTrue(output.contains("example.com"));
        assertFalse(output.contains("localhost"), "should skip localhost entries");
    }
}
