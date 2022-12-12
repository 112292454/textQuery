package com.example.demo.controller;

import com.example.demo.service.FileSearch;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Search {
	@GetMapping({"/{target}"})
	public Map init(@PathVariable("target") String target) {
		if (target.length() == 0 || "favicon.ico".equals(target)) return new HashMap<>();
		try {
			target = URLDecoder.decode(target, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("searched for:“" + target + "“");

		if (this.fileSearch == null) {
			this.fileSearch = (new FileSearch(32, "D:\\txt下载")).build();
		}
		Map<String, String> res = this.fileSearch.execute(target, 2);
		writeCsv("searchResult.csv", res);

		return res;
	}

	FileSearch fileSearch;

	@GetMapping({"/t/{target}"})
	public String getContent(@PathVariable("target") String target) {
		if (target.length() == 0) return "输入名称";
		try {
			target = URLDecoder.decode(target, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		FileInputStream reader = null;
		try {
			reader = new FileInputStream("D:\\txt下载\\" + target);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String res = null;
		try {
			res = IOUtils.toString(reader, "gbk");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public void writeCsv(String outFile, Map<String, String> map) {
		File temp = new File(outFile);
		Path path = Paths.get(outFile, new String[0]);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!temp.exists()) {
			try {
				System.out.println(temp.createNewFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(temp), "utf-8"))) {
			csvWriter.writeNext(new String[]{"path", "preview"});
			map.forEach((k, v) -> csvWriter.writeNext(new String[]{k, v.replaceAll("[\n\r\t]", " ")}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
