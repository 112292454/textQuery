package com.example.demo.service;

import DownloadTools.DownLoad;
import com.example.demo.component.FileSearch;
import com.example.demo.controller.Search;
import com.example.demo.dao.TextDao;
import com.example.demo.entity.SearchResult;
import com.example.demo.entity.TextPath;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

	private static Logger logger = LoggerFactory.getLogger(SearchService.class);
	@Autowired
	FileSearch fileSearch;
	@Autowired
	TextDao textDao;

	public List<SearchResult> getSearchResults(String target, Integer type, int limit, ModelAndView mv) {
		List<SearchResult> res;
		if (type == null) type = 0;
		if (StringUtils.isNotBlank(target) && type >= 0) {
			try {
				target = URLDecoder.decode(target, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (type == 0) type = 2;
			logger.info("search {} limit start for key={},type={}", limit ==Integer.MAX_VALUE?"no": limit,target,type);
			long time = System.currentTimeMillis();

			switch (target) {
				case "favor":{
					res=pathTransToSearchRes(textDao.getFavor());
					break;
				}
				default:{
					res = this.fileSearch.execute(target, type, limit);
				}
			}


			res = res.stream().limit(limit).collect(Collectors.toList());
			logger.info("search down!");
			logger.info("use time(ms):{}",System.currentTimeMillis() - time);

			writeCsv("D:\\txt下载\\searchResult\\" + DownLoad.filenameFilter("searchResult  " + target + new Date().toGMTString()) + ".csv", res);
			mv.addObject("searchKey", target);

		} else {
			//mv.addObject("searchKey", "");
			res=pathTransToSearchRes(textDao.getTopK(150));
		}
		return res;
	}

	private List<SearchResult> pathTransToSearchRes(List<TextPath> source) {

		List<SearchResult> res = new ArrayList<>();
		for (TextPath a : source) {
			res.add(new SearchResult(a.getTid(), a.getPath(),
					a.getPath().substring(a.getPath().lastIndexOf('\\')+1), "", a.getGrade()));
		}
		return res;
	}

	private void writeCsv(String outFile, List<SearchResult> searchResults) {
		File temp = new File(outFile);
		Path path = Paths.get(outFile, new String[0]);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!temp.exists()) {
			try {
				//TODO:利用files包，尝试使用temp的库方法
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(temp), "utf-8"))) {
			csvWriter.writeNext(new String[]{"PID", "localPath", "name", "preview", "grade"});
			searchResults.forEach(a -> csvWriter.
					writeNext(new String[]{a.getPID(), a.getLocalPath(), a.getName(), a.getPreview(), String.valueOf(a.getGrade())}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
