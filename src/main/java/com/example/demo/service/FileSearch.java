package com.example.demo.service;
import com.example.demo.component.TempFileDTO;
import com.example.demo.component.searchThread;
import com.example.demo.service.FileDistribute;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * 文件中文字搜索
 */
public class FileSearch {

	private String directory;
	private int threadNum;

	public FileSearch(int threadNum, String folderPath) {
		this.directory = folderPath;
		this.threadNum = threadNum;
		this.fileDistribute = new FileDistribute();
	}

	private List<searchThread> threads;

	private List<searchThread> creatThreads() {
		this.latch = new CountDownLatch(this.threadNum);
		this.threads = new ArrayList<>();
		for (int i = 0; i < this.threadNum; i++) {
			searchThread thread = new searchThread(i);
			thread.setCountDownLatch(this.latch);
			this.threads.add(thread);
		}
		this.fileDistribute.setThreads(this.threads);
		return this.threads;
	}

	FileDistribute fileDistribute;
	CountDownLatch latch;

	private HashMap<String, String> collect() {
		HeaderColumnNameMappingStrategy strategy = new HeaderColumnNameMappingStrategy();
		strategy.setType(TempFileDTO.class);
		HashMap<String, String> res = new HashMap<>();

		for (int i = 0; i < this.threadNum; i++) {
			try (InputStreamReader in = new InputStreamReader(new FileInputStream("temp" + i + ".csv"), "utf-8")) {
				RFC4180Parser rfc4180Parser = (new RFC4180ParserBuilder()).build();
				CSVReader inputStream = (new CSVReaderBuilder(in)).withCSVParser((ICSVParser) rfc4180Parser).build();


				CsvToBean csvToBean = (new CsvToBeanBuilder(inputStream)).withMappingStrategy((MappingStrategy) strategy).build();
				List<TempFileDTO> csv = csvToBean.parse();
				csv.forEach(a ->  res.put(a.getPath(), a.getPreview() + "\n"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Files.deleteIfExists(Paths.get("temp" + i + ".csv", new String[0]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	public com.example.demo.service.FileSearch build() {
		File dirt = new File(this.directory);
		List<Path> paths = null;
		try {
			paths = (List<Path>) Files.walk(Paths.get(this.directory, new String[0]), new java.nio.file.FileVisitOption[0]).filter(a -> !a.toFile().isDirectory()).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}


		creatThreads();
		this.fileDistribute.setAttr(this.directory + "\\");
		this.fileDistribute.setTasks(paths);
		this.fileDistribute.execute();

		return this;
	}

	public HashMap<String, String> execute(String target, int mode) {
		creatThreads();
		this.fileDistribute.execute();
		System.out.println("search start!");
		long time = System.currentTimeMillis();

		for (searchThread thread : this.threads) {
			thread.setTarget(target);
			thread.setMode(mode);
			thread.start();
		}

		try {
			this.latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("search down!");
		System.out.println("use time(ms):" + (System.currentTimeMillis() - time));
		return collect();
	}
}

