package com.ptl.linkschecker.service;


import com.ptl.linkschecker.domain.PageResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class LinksManagerImplTest {

    LinksManager tested = new LinksManagerImpl();
    @Test
    void should_retrieve_links(){
        // GIVEN
        tested.addNewLinks(Arrays.asList("url1", "url2"));
        // WHEN
        String first = tested.getNextUnProcessedLink();
        tested.updateLink(first,200);
        String second = tested.getNextUnProcessedLink();
        tested.updateLink(second,200);
        String third = tested.getNextUnProcessedLink();
        // THEN
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertNull(third);
    }

    @Test
    void should_allow_update_new_links(){
        // GIVEN
        tested.updateLink("url",200);
        // WHEN
        List<PageResult> good = tested.getAllGoodLinks();
        List<String> bad = tested.getAllBadLinks();
        List<String> other = tested.getAllUntestedLinks();
        // THEN
        Assertions.assertEquals(1,good.size());
        Assertions.assertTrue(bad.isEmpty());
        Assertions.assertTrue(other.isEmpty());
    }

}