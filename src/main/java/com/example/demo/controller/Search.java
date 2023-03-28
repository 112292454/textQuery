package com.example.demo.controller;

import com.example.demo.entity.SearchResult;
import com.example.demo.service.SearchService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/hnovel")
public class Search {
	private static Logger logger = LoggerFactory.getLogger(Search.class);
	@Autowired
	SearchService searchService;

	@GetMapping({""})
	public String home() {
		return "home";
	}

	//@GetMapping({"show"})
	public ModelAndView show(String target, Integer type, ModelAndView mv) {
		return getShowContent(target, type, 150, mv);
	}


	@GetMapping({"show"})
	@ResponseBody
	public ModelAndView show(String target, Integer type, @RequestParam(defaultValue = "desc") String order, ModelAndView mv) {
		return getShowContent(target, type, 150, order, mv);
	}
	@GetMapping({"similar"})
	@ResponseBody
	public ModelAndView findSimilar(String text, ModelAndView mv) {
		List<SearchResult> similarResults = searchService.getSimilarResults(text);
		return contensSort("desc",similarResults,mv);
	}

	@GetMapping({"similar_id"})
	@ResponseBody
	public ModelAndView findSimilar(Long id, ModelAndView mv) throws IOException {
		logger.info("查找相似文章：id={}",id);
		List<SearchResult> similarResults = searchService.getSimilarResults(id);
		return contensSort("desc",similarResults,mv);
	}

	@GetMapping({"showall"})
	@ResponseBody
	public ModelAndView showAll(String target, Integer type, Integer limit, String order, ModelAndView mv) {
		getShowContent(target, type, Integer.MAX_VALUE, order, mv);
		if(limit!=null&&limit>0){
			Map<String, Object> model = mv.getModel();
			List<SearchResult> novel = (List<SearchResult>) model.get("novel");
			novel=novel.stream().limit(limit).collect(Collectors.toList());
			model.replace("novel", novel);
			//mv.clear();
			mv.addAllObjects(model);
		}
		return mv;
	}

	private ModelAndView getShowContent(String target, Integer type, int limit, ModelAndView mv) {
		return getShowContent(target, type,limit,"desc",mv);
	}
	private ModelAndView getShowContent(String target, Integer type, int limit, String order, ModelAndView mv) {
		List<SearchResult> res = searchService.getSearchResults(target, type, limit, mv);
		return contensSort(order, res, mv);
	}

	private ModelAndView contensSort(String order, List<SearchResult> res, ModelAndView mv) {
		if(order.equals("asc")){
			res.sort(Comparator.comparingInt(SearchResult::getGrade));
		}

		mv.setViewName("novelRes");
		mv.addObject("novel", res);
		return mv;
	}


}
