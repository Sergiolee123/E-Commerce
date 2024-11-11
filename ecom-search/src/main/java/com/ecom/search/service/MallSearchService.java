package com.ecom.search.service;

import com.ecom.search.vo.SearchParam;
import com.ecom.search.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam searchParam);
}
