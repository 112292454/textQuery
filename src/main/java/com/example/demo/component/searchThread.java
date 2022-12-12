package com.example.demo.component;
import com.example.demo.component.searchThread;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class searchThread extends Thread {
	Logger logger = LoggerFactory.getLogger(searchThread.class);
	private List<Path> searchTask;
	private int id;
	private String target;
	private Pattern match;
	private HashMap<String, String> result;
	private int MODE;
	private CountDownLatch countDownLatch;
	private long startTime;
	private static final int MAX_SEARCH_TIME = 10000;
	public static final int TEXT_COTAINS = 1;
	public static final int REGEX_FIND = 2;
	public static final int REGEX_MATCH = 3;

	public searchThread(int id) {
		this(id, "NULL_TARGET", 1);
	}

	public searchThread(int id, String target) {
		this(id, target, 2);
	}

	public searchThread(int id, String target, int mode) {
		this.searchTask = new ArrayList<>();
		this.result = new HashMap<>();


		this.id = id;
		this.target = target;
		this.match = Pattern.compile(target);
		this.MODE = mode;
		this.startTime = System.currentTimeMillis();
	}

	public void setTarget(String target) {
		this.target = target;
		this.match = Pattern.compile(target);
	}

	public void setMode(int mode) {
		this.MODE = mode;
	}

	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}


	public boolean addSearchTaskFile(Path file) {
		return this.searchTask.add(file);
	}

	private void saveTemp() {
		File temp = new File("temp" + this.id + ".csv");
		if (!temp.exists()) {
			try {
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(temp), "utf-8"))) {
			csvWriter.writeNext(new String[]{"path", "preview"});
			this.result.forEach((k, v) -> csvWriter.writeNext(new String[]{k, v.replaceAll("[\n\r\t]", " ")}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.result.clear();
	}

	public int getTargetIndex(String context) {
		Matcher matcher;
		int index = -1;
		switch (this.MODE) {
			case 1:
				index = context.indexOf(this.target);
				break;
			case 2:
				matcher = this.match.matcher(context);
				if (matcher.find()) {
					index = context.indexOf(matcher.group());
				}
				break;
		}


		return index;
	}


	public void run() {
		for (int i = 0; i < this.searchTask.size(); i++) {
			Path s = this.searchTask.get(i);
			if (System.currentTimeMillis() - this.startTime > 10000L) {
				this.logger.info("未搜索完数量 = {}/{}", Integer.valueOf(this.searchTask.size() - i), Integer.valueOf(this.searchTask.size()));
				break;
			}
			try (FileInputStream fileInputStream = new FileInputStream(s.toFile())) {
				String context = s.toFile().getName() + "     " + IOUtils.toString(fileInputStream, "UTF-8");
				int index = getTargetIndex(context);
				if (index >= 0) {
					String preview = context.substring(Math.max(0, index - 30), Math.min(context.length(), index + 30));
					this.result.put(s.toFile().getName(), preview);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		saveTemp();
		this.countDownLatch.countDown();
	}
}


/* Location:              D:\Spring\mvn_repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\component\searchThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */