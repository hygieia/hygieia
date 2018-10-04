package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.relation.RelatedCollectorItem;
import com.capitalone.dashboard.testutil.FongoConfig;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;


public class RelatedCollectorItemRepositoryTest extends FongoBaseRepositoryTest {

    @Autowired
    private RelatedCollectorItemRepository relatedCollectorItemRepository;

    @Test
    public void saveRelatedItems() {
        relatedCollectorItemRepository.deleteAll();
        ObjectId left = ObjectId.get();
        ObjectId right = ObjectId.get();
        relatedCollectorItemRepository.saveRelatedItems(left, right, "some source", "some reason");
        List<RelatedCollectorItem> relatedCollectorItemList = Lists.newArrayList(relatedCollectorItemRepository.findAll());
        assertFalse(CollectionUtils.isEmpty(relatedCollectorItemList));
        assertEquals(1, relatedCollectorItemList.size());
        assertEquals(relatedCollectorItemList.get(0).getLeft(), left);
        assertEquals(relatedCollectorItemList.get(0).getRight(), right);
        assertTrue(relatedCollectorItemList.get(0).getReason().equalsIgnoreCase("some reason"));
        assertTrue(relatedCollectorItemList.get(0).getSource().equalsIgnoreCase("some source"));

    }

    @Test
    public void saveRelatedItemsDuplicate() {
        relatedCollectorItemRepository.deleteAll();
        ObjectId left = ObjectId.get();
        ObjectId right = ObjectId.get();
        RelatedCollectorItem rc = new RelatedCollectorItem();
        rc.setLeft(left);
        rc.setRight(right);
        rc.setSource("some source");
        rc.setReason("some reason");
        RelatedCollectorItem saved = relatedCollectorItemRepository.save(rc);

        RelatedCollectorItem savedAgain = relatedCollectorItemRepository.saveRelatedItems(left, right, "some source", "some reason");

        List<RelatedCollectorItem> relatedCollectorItemList = Lists.newArrayList(relatedCollectorItemRepository.findAll());
        assertTrue(!CollectionUtils.isEmpty(relatedCollectorItemList));
        assertEquals(1, relatedCollectorItemList.size());
        assertEquals(relatedCollectorItemList.get(0).getLeft(), left);
        assertEquals(relatedCollectorItemList.get(0).getRight(), right);
        assertTrue(relatedCollectorItemList.get(0).getReason().equalsIgnoreCase("some reason"));
        assertTrue(relatedCollectorItemList.get(0).getSource().equalsIgnoreCase("some source"));
        assertTrue(!Objects.equals(saved.getId(), savedAgain.getId()));


    }

    @Test
    public void saveRelatedItemsDuplicateMany() {
        relatedCollectorItemRepository.deleteAll();
        ObjectId left = ObjectId.get();
        ObjectId right = ObjectId.get();
        for (int i = 0; i < 5; i++) {
            RelatedCollectorItem rc = new RelatedCollectorItem();
            rc.setLeft(left);
            rc.setRight(right);
            rc.setSource("some source");
            rc.setReason("some reason");
            relatedCollectorItemRepository.save(rc);
        }

        RelatedCollectorItem saved = relatedCollectorItemRepository.findAll().iterator().next();

        RelatedCollectorItem savedAgain = relatedCollectorItemRepository.saveRelatedItems(left, right, "some source", "some reason");

        List<RelatedCollectorItem> relatedCollectorItemList = Lists.newArrayList(relatedCollectorItemRepository.findAll());
        assertTrue(!CollectionUtils.isEmpty(relatedCollectorItemList));
        assertEquals(1, relatedCollectorItemList.size());
        assertEquals(relatedCollectorItemList.get(0).getLeft(), left);
        assertEquals(relatedCollectorItemList.get(0).getRight(), right);
        assertTrue(relatedCollectorItemList.get(0).getReason().equalsIgnoreCase("some reason"));
        assertTrue(relatedCollectorItemList.get(0).getSource().equalsIgnoreCase("some source"));
        assertTrue(!Objects.equals(saved.getId(), savedAgain.getId()));


    }

}