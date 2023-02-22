package com.example.demo.component;
import com.example.demo.entity.TextPath;
import com.example.demo.dao.TextDao;
import com.example.demo.entity.SearchResult;
import com.example.demo.service.FileDistribute;
import com.example.demo.service.NovelGrade;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 文件中文字搜索
 */
@Service
@ConfigurationProperties(prefix = "search")
@Data
public class FileSearch {

	private static Logger logger=LoggerFactory.getLogger(FileSearch.class);

	@Resource
	NovelGrade novelGrade;

	@Resource
	TextDao textDao;
	@Value("${search.thread-num}")
	private int threadNum;

	private String directory;

	private List<searchThread> threads;

	FileDistribute fileDistribute;

	CountDownLatch latch;
	@PostConstruct
	public void init(){
		this.build();
	}

	public  FileSearch(NovelGrade novelGrade) {
		this.novelGrade=novelGrade;
		this.fileDistribute = new FileDistribute();
		//TODO:为什么在这里的build时，读取不到配置文件里写的属性，但是在controller里去调用此build就可以了
		//this.build();
	}
	public void build() {
		List<TextPath> paths =new ArrayList<>();
		paths=textDao.getAll();
		//for (TextPath textPath : paths)
		//	if(!Files.exists(Paths.get(textPath.getPath())))
		//		textDao.deleteTextPathByTid(textPath.getTid());

		//paths.sort(Comparator.comparingInt(TextPath::getFileLength));
		//try {
		//	paths =Files.walk(Paths.get(this.directory), new java.nio.file.FileVisitOption[0])
		//			.filter(a -> {
		//				String name=a.toFile().getAbsolutePath();
		//				return (!a.toFile().isDirectory())
		//						&&name.endsWith("txt")
		//						&&(name.contains("旧小说合集")||name.contains("pixiv下载"));
		//			})
		//			.collect(Collectors.toList());
		//} catch (Exception e) {
		//	e.printStackTrace();
		//}
		logger.info("小说路径读取完毕");

		creatThreads();
		this.fileDistribute.setAttr(this.directory + "\\");
		this.fileDistribute.setTasks(paths);
		this.fileDistribute.execute();

	}

	private List<searchThread> creatThreads() {
		this.latch = new CountDownLatch(this.threadNum);
		this.threads = new ArrayList<>();
		for (int i = 0; i < this.threadNum; i++) {
			searchThread thread = new searchThread(i);
			thread.setCountDownLatch(this.latch);
			thread.setNovelGrade(novelGrade);
			this.threads.add(thread);
		}
		this.fileDistribute.setThreads(this.threads);
		return this.threads;
	}



	private List<SearchResult> collect() {
		HeaderColumnNameMappingStrategy strategy = new HeaderColumnNameMappingStrategy();
		strategy.setType(SearchResult.class);
		List<SearchResult> res = new ArrayList<>();

		for (int i = 0; i < this.threadNum; i++) {
			try (InputStreamReader in = new InputStreamReader(Files.newInputStream(Paths.get("D:\\txt下载\\searchResult\\temp\\temp" + i + ".csv")), StandardCharsets.UTF_8)) {
				RFC4180Parser rfc4180Parser = (new RFC4180ParserBuilder()).build();
				CSVReader inputStream = (new CSVReaderBuilder(in)).withCSVParser(rfc4180Parser).build();


				CsvToBean csvToBean = (new CsvToBeanBuilder(inputStream)).withMappingStrategy(strategy).build();
				List<SearchResult> csv = csvToBean.parse();
				csv.forEach(a -> res.add(a));
			} catch (IOException e) {
				e.printStackTrace();
			}


			try {
				Files.deleteIfExists(Paths.get("temp" + i + ".csv"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		StringBuilder sb = new StringBuilder();
		res.forEach(a -> {
			if (StringUtils.isNotBlank(a.getPID())) sb.append(a.getPID()).append(',');
		});

		List<TextPath> textPaths = new ArrayList<>();
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
			textPaths = textDao.getAllByTidS(sb.toString());
		}

		HashMap<String, Integer> grades = new HashMap<>();
		textPaths.forEach(a -> grades.put(a.getTid(), a.getGrade()));
		res.forEach(a -> a.setGrade(grades.getOrDefault(a.getPID(), 99999999)));
		if (searchThread.surplus > 0) {
			res.add(new SearchResult("-1",
					"none",
					"提示：此次到达15s限时，未搜索完成",
					"剩余文件数：" + searchThread.surplus,
					114514));
		}
		searchThread.surplus = 0;
		searchThread.succeedNum=0;
		res.sort((a, b) -> b.getGrade() - a.getGrade());
		return res;
	}


	public List<SearchResult> execute(String target, int mode, int searchNum) {
		creatThreads();
		this.fileDistribute.execute();

		for (searchThread thread : this.threads) {
			thread.setTarget(target);
			thread.setMode(mode);
			thread.setMaxSearchNum(searchNum);
			thread.start();
		}

		try {
			this.latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(searchThread.surplus>0) logger.info("剩余{}文件未搜索完成", searchThread.surplus);
		//new Thread(() -> {
		//	creatThreads();
		//	fileDistribute.execute();
		//}).start();
		return collect();
	}
}

