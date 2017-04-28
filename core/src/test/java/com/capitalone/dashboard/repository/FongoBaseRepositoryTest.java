package com.capitalone.dashboard.repository;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.fakemongo.junit.FongoRule;

@ContextConfiguration(classes = { FongoConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public abstract class FongoBaseRepositoryTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
}
