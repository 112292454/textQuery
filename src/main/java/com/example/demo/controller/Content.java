package com.example.demo.controller;

import com.example.demo.dao.TextDao;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/hnovel")
public class Content {
	private static Logger logger = LoggerFactory.getLogger(Content.class);
	private static final int pageSize=20*1000;
	@Autowired
	TextDao textDao;

	@GetMapping({"/t/{target}/{chapter}"})
	public ModelAndView getContent(@PathVariable("target") String target, @PathVariable("chapter") Integer chapter,ModelAndView mv) {
		if (target.length() == 0) return mv;
		//try {
		//	target = URLDecoder.decode(target, "utf-8");
		//	target = target.replace("D:\\txt下载\\", "");
		//} catch (UnsupportedEncodingException e) {
		//	e.printStackTrace();
		//}

		String name = textDao.getPathByTid(target).getPath();
		String title=name.substring(name.lastIndexOf('\\')+1,name.indexOf(".txt"));
		logger.info("请求全文：pid={},    name={}，   page={}", target, name,chapter);
		String res = "文件未找到";

		try {
			FileInputStream reader = new FileInputStream(name);
			res = IOUtils.toString(reader, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		res='\n'+res;

		int start=res.indexOf('\n', (chapter-1)*pageSize);
		int end=res.indexOf('\n', chapter*pageSize);
		int jump=res.indexOf('\n', (chapter+5)*pageSize);
		if(end==-1) end=res.length();
		if(jump==-1) jump=res.length();

		String lastChap="/hnovel/t/"+target+'/'+(chapter-1);
		String nextChap="/hnovel/t/"+target+'/'+(chapter+1);
		String jumpChap="/hnovel/t/"+target+'/'+(chapter+5);
		if(start>0) mv.addObject("lastChap", lastChap);
		if(end<res.length()) mv.addObject("nextChap", nextChap);
		if(jump<res.length()) mv.addObject("jumpChap", jumpChap);



		res=res.substring(start,end);
		res = res.replaceAll("\r", "\n");
		res = res.replaceAll("\n{2,}", "\n");
		HashMap<Integer,Integer> cnt=new HashMap<>();
		for (int i = res.indexOf('\n'); i < res.length()&&i!=-1; ) {
			int nowEnter=res.indexOf('\n', i+1),interval=nowEnter-i;
			cnt.put(interval,cnt.getOrDefault(interval,0)+1);
			i=nowEnter;
		}
		Optional<Map.Entry<Integer, Integer>> max = cnt.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
		int sameInterval=max.get().getKey();
		String[] split = res.split("\n");
		StringBuilder sb=new StringBuilder();
		for (String s : split) {
			sb.append(s);
			if(Math.abs(s.length() - sameInterval)>3) sb.append('\n');
		}
		res=sb.toString();
		//for (int i = res.indexOf('\n'); i < chars.length&&i!=-1; ) {
		//	int nowEnter=res.indexOf('\n', i+1),interval=nowEnter-i;
		//	if(interval==sameInterval) chars[i]=0;
		//	cnt.put(interval,cnt.getOrDefault(interval,0)+1);
		//	i=nowEnter;
		//}
		res = res.replace("\n", "<p>");
		mv.addObject("content", res);


		if(target.length()<10)
			mv.addObject("sourceWeb", "https://www.pixiv.net/novel/show.php?id="+target);
		if(StringUtils.isNotBlank(title)&&!title.contains("\\"))
			mv.addObject("title", title);
		mv.setViewName("novelContent");

		return mv;
	}

	@GetMapping({"/t/{target}"})
	public ModelAndView getContent(@PathVariable("target") String target,ModelAndView mv) {
		return getContent(target,1,mv);
	}

}
