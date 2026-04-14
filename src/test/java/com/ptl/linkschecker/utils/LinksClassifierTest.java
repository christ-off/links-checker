package com.ptl.linkschecker.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinksClassifierTest {

    @Test
    void isGoodLink_200() {
        assertTrue(LinksClassifier.isGoodLink(200));
    }

    @Test
    void isGoodLink_299() {
        assertTrue(LinksClassifier.isGoodLink(299));
    }

    @Test
    void isGoodLink_rejects_300() {
        assertFalse(LinksClassifier.isGoodLink(300));
    }

    @Test
    void isGoodLink_rejects_404() {
        assertFalse(LinksClassifier.isGoodLink(404));
    }

    @Test
    void isRedirectLink_301() {
        assertTrue(LinksClassifier.isRedirectLink(301));
    }

    @Test
    void isRedirectLink_302() {
        assertTrue(LinksClassifier.isRedirectLink(302));
    }

    @Test
    void isRedirectLink_399() {
        assertTrue(LinksClassifier.isRedirectLink(399));
    }

    @Test
    void isRedirectLink_rejects_200() {
        assertFalse(LinksClassifier.isRedirectLink(200));
    }

    @Test
    void isRedirectLink_rejects_400() {
        assertFalse(LinksClassifier.isRedirectLink(400));
    }

    @Test
    void isBadLink_404() {
        assertTrue(LinksClassifier.isBadLink(404));
    }

    @Test
    void isBadLink_500() {
        assertTrue(LinksClassifier.isBadLink(500));
    }

    @Test
    void isBadLink_rejects_200() {
        assertFalse(LinksClassifier.isBadLink(200));
    }

    @Test
    void isBadLink_rejects_302() {
        assertFalse(LinksClassifier.isBadLink(302));
    }

    @Test
    void isUntestedLink_borrowed() {
        assertTrue(LinksClassifier.isUntestedLink(LinksClassifier.BORROWED));
    }

    @Test
    void isUntestedLink_rejects_200() {
        assertFalse(LinksClassifier.isUntestedLink(200));
    }
}