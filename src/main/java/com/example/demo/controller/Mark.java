package com.example.demo.controller;

import com.example.demo.dao.TextDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/hnovel")
public class Mark {
	private static Logger logger = LoggerFactory.getLogger(Mark.class);

	@Autowired
	TextDao textDao;
	@Autowired
	Search search;

	@GetMapping({"/favor/add/{id}"})
	public String getContent(@PathVariable("id") String tid) {
		return textDao.setFavor(tid)?"收藏成功！":"出现错误";
	}
	@GetMapping({"/favor/show"})
	public ModelAndView show(ModelAndView mv) {
		return search.show("favor", 1, mv);
	}



}
