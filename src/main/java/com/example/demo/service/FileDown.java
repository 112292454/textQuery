package com.example.demo.service;
import com.example.demo.component.TempFileDTO;
import com.example.demo.component.downThread;
import com.example.demo.dao.TextDao;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 下载p站小说爬虫
 */
public class FileDown {
	private String directory;
	private int threadNum;
	private int start;

	public FileDown(String directory, int threadNum, int start, int end) {
		this.directory = directory;
		this.threadNum = threadNum;
		this.start = start;
		this.end = end;
	}

	private int end;
	private List<downThread> threads;
	private TextDao dao;

	public void setDao(TextDao dao) {
		this.dao = dao;
	}


	private List<downThread> creatThreads() {
		List<downThread> threads = new ArrayList<>();
		for (int i = 0; i < this.threadNum; i++) {
			downThread thread = new downThread(i, this.threadNum, this.start + i, this.end);
			thread.setPath(this.directory);
			thread.setDao(this.dao);
			threads.add(thread);
		}
		return threads;
	}

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
				csv.forEach(a -> res.put(a.getPath(), a.getPreview() + "\n"));
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

	public com.example.demo.service.FileDown build() {
		this.threads = creatThreads();
		return this;
	}

	public void execute() {
		for (downThread thread : this.threads)
			thread.start();
	}
}
