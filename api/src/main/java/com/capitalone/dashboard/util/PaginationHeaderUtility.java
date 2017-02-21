package com.capitalone.dashboard.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class PaginationHeaderUtility {

	public <T> HttpHeaders buildPaginationHeaders(Page<T> page) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("totalEntities", String.valueOf(page.getTotalElements()));
		headers.add("totalPages", String.valueOf(page.getTotalPages()));
		headers.add("lastPage", String.valueOf(page.isLast()));
		headers.add("pageSize", String.valueOf(page.getSize()));
		headers.add("currentPage", String.valueOf(page.getNumber()));
		headers.add("firstPage", String.valueOf(page.isFirst()));
		headers.add("nextPage", String.valueOf(page.hasNext()));

		return headers;
	}
}