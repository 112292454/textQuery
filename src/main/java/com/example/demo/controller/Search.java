package com.example.demo.controller;

import DownloadTools.DownLoad;
import com.example.demo.dao.TextDao;
import com.example.demo.entity.SearchResult;
import com.example.demo.entity.TextPath;
import com.example.demo.component.FileSearch;
import com.example.demo.service.SearchService;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/hnovel")
public class Search {
	private static Logger logger = LoggerFactory.getLogger(Search.class);
	@Autowired
	SearchService searchService;

	@GetMapping({"show"})
	public ModelAndView show(String target, Integer type, ModelAndView mv) {
		return getShowContent(target, type, 150, mv);
	}

	@GetMapping({"showall"})
	public ModelAndView showAll(String target, Integer type, ModelAndView mv) {
		return getShowContent(target, type, Integer.MAX_VALUE, mv);
	}

	private ModelAndView getShowContent(String target, Integer type, int limit, ModelAndView mv) {
		List<SearchResult> res = searchService.getSearchResults(target, type, limit, mv);

		mv.setViewName("novelRes");
		mv.addObject("novel", res);
		return mv;
	}





}
