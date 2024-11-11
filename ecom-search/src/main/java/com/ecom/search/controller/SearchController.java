package com.ecom.search.controller;


import com.ecom.search.service.MallSearchService;
import com.ecom.search.vo.SearchParam;
import com.ecom.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    private final MallSearchService mallSearchService;

    public SearchController(MallSearchService mallSearchService) {
        this.mallSearchService = mallSearchService;
    }

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model) {
        SearchResult result = mallSearchService.search(searchParam);
        model.addAttribute("result", result);

        return "list";
    }

}
