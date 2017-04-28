package com.capitalone.dashboard.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

@RunWith(MockitoJUnitRunner.class)
public class PaginationHeaderUtilityTest {

	private PaginationHeaderUtility utility;
	
	@Mock
	private Page<String> page;
	
	@Before
	public void setup() {
		utility = new PaginationHeaderUtility();
		
		when(page.getTotalElements()).thenReturn(5L);
		when(page.getTotalPages()).thenReturn(5);
		when(page.isLast()).thenReturn(false);
		when(page.getSize()).thenReturn(5);
		when(page.getNumber()).thenReturn(5);
		when(page.isFirst()).thenReturn(true);
		when(page.hasNext()).thenReturn(true);
	}
	
	@Test
	public void test() {
		HttpHeaders result = utility.buildPaginationHeaders(page);
		assertEquals("5", result.get("totalEntities").get(0));
		assertEquals("5", result.get("totalPages").get(0));
		assertEquals("false", result.get("lastPage").get(0));
		assertEquals("5", result.get("pageSize").get(0));
		assertEquals("5", result.get("currentPage").get(0));
		assertEquals("true", result.get("firstPage").get(0));
		assertEquals("true", result.get("nextPage").get(0));
	}

}
