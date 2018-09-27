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

import static org.junit.Assert.*;


public class RelatedCollectorItemRepositoryTest extends FongoBaseRepositoryTest{

    @Autowired
    private RelatedCollectorItemRepository relatedCollectorItemRepository;

    @Test
    public void saveRelatedItems() {
        relatedCollectorItemRepository.deleteAll();
        ObjectId left = ObjectId.get();
        ObjectId right = ObjectId.get();
        relatedCollectorItemRepository.saveRelatedItems(left, right, "some source", "some reason");
        List<RelatedCollectorItem> relatedCollectorItemList = Lists.newArrayList(relatedCollectorItemRepository.findAll());
        assertTrue(!CollectionUtils.isEmpty(relatedCollectorItemList));
        assertTrue(relatedCollectorItemList.size() == 1);
        assertTrue(relatedCollectorItemList.get(0).getLeft().equals(left));
        assertTrue(relatedCollectorItemList.get(0).getRight().equals(right));
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
        assertTrue(relatedCollectorItemList.size() == 1);
        assertTrue(relatedCollectorItemList.get(0).getLeft().equals(left));
        assertTrue(relatedCollectorItemList.get(0).getRight().equals(right));
        assertTrue(relatedCollectorItemList.get(0).getReason().equalsIgnoreCase("some reason"));
        assertTrue(relatedCollectorItemList.get(0).getSource().equalsIgnoreCase("some source"));
        assertTrue(saved.getId().equals(savedAgain.getId()));


    }

}