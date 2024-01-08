package com.ptl.linkschecker.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class LinksManagerImplTest {

    LinksManager tested = new LinksManagerImpl();
    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantValue"})
    @Test
    void should_retrieve_links(){
        // GIVEN
        tested.addNewLinks(Arrays.asList("url1", "url2"));
        // WHEN
        Optional<String> first = tested.getNextUnProcessedLink();
        tested.updateLink(first.get(),200);
        Optional<String> second = tested.getNextUnProcessedLink();
        tested.updateLink(second.get(),200);
        Optional<String> third = tested.getNextUnProcessedLink();
        // THEN
        Assertions.assertTrue(first.isPresent());
        Assertions.assertTrue(second.isPresent());
        Assertions.assertFalse(third.isPresent());
    }

    @Test
    void should_allow_update_new_links(){
        // GIVEN
        tested.updateLink("url",200);
        // WHEN
        List<String> good = tested.getAllGoodLinks();
        List<String> bad = tested.getAllBadLinks();
        List<String> other = tested.getAllUntestedLinks();
        // THEN
        Assertions.assertEquals(1,good.size());
        Assertions.assertTrue(bad.isEmpty());
        Assertions.assertTrue(other.isEmpty());
    }

}