package com.ptl.linkschecker.service;


import com.ptl.linkschecker.domain.PageResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class LinksManagerImplTest {

    LinksManager tested = new LinksManager();
    @Test
    void should_retrieve_links(){

        tested.addNewLinks(Arrays.asList("url1", "url2"));

        String first = tested.getNextUnProcessedLink();
        tested.updateLink(first, "Fake Content", 200);
        String second = tested.getNextUnProcessedLink();
        tested.updateLink(second, "Fake Content", 200);
        String third = tested.getNextUnProcessedLink();

        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertNull(third);
    }

    @Test
    void should_allow_update_new_links(){

        tested.updateLink("https://www.example.com", "Fake Content", 200);

        List<PageResult> links = tested.getLinks();

        Assertions.assertEquals(1,links.size());
        Assertions.assertTrue(links.stream().allMatch(pageResult -> pageResult.httpStatusCode() == 200));
    }

    @Test
    void should_clear_state_on_reset() {
        tested.updateLink("https://www.example.com", "Fake Content", 200);
        Assertions.assertEquals(1, tested.getLinks().size());

        tested.reset();

        Assertions.assertEquals(0, tested.getLinks().size());
        Assertions.assertNull(tested.getNextUnProcessedLink());
    }

    @Test
    void tryAdd_returns_true_for_new_url() {
        Assertions.assertTrue(tested.tryAdd("https://www.example.com"));
    }

    @Test
    void tryAdd_returns_false_for_duplicate_url() {
        tested.tryAdd("https://www.example.com");

        Assertions.assertFalse(tested.tryAdd("https://www.example.com"));
    }

    @Test
    void getLinks_excludes_borrowed_entries() {
        tested.addNewLinks(Arrays.asList("https://pending.com"));

        List<PageResult> links = tested.getLinks();

        Assertions.assertTrue(links.isEmpty());
    }

}
