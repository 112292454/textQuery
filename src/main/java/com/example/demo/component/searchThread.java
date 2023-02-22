package com.example.demo.component;
import com.example.demo.entity.SearchResult;
import com.example.demo.entity.TextPath;
import com.example.demo.service.NovelGrade;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class searchThread extends Thread {
	private static Pattern PID=Pattern.compile("(-\\d{6,}\\.)|(^\\d{6,}-)");

	public static volatile Integer surplus=0;
	public static volatile Integer succeedNum=0;

	Logger logger = LoggerFactory.getLogger(searchThread.class);
	private List<TextPath> searchTask;
	private int Tid;
	private int threadNum=64;
	private String target;
	private Pattern match;
	private List<SearchResult> result;
	private int MODE;
	private CountDownLatch countDownLatch;
	private NovelGrade novelGrade;

	private long startTime;
	private static final int MAX_SEARCH_TIME = 10000;
	private static  int MAX_SEARCH_NUM;
	public static final int TEXT_CONTAINS = 1;
	public static final int REGEX_FIND = 2;
	public static final int REGEX_MATCH = 3;

	public searchThread(int id) {
		this(id, "NULL_TARGET", 1);
	}

	public void setMaxSearchNum(int maxSearchNum) {
		MAX_SEARCH_NUM = maxSearchNum;
	}

	public searchThread(int id, String target, int mode) {
		this.searchTask = new ArrayList<>();
		this.result = new ArrayList<>();


		this.Tid = id;
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


	public boolean addSearchTaskFile(TextPath file) {
		return this.searchTask.add(file);
	}

	private void saveTemp() {
		File temp = new File("D:\\txt下载\\searchResult\\temp\\temp" + this.Tid + ".csv");

		if (temp.exists()) temp.delete();
		try {
			temp.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}


		try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(temp), "utf-8"))) {
			csvWriter.writeNext(new String[]{"PID", "localPath", "name", "preview", "grade"});
			this.result.forEach(a -> csvWriter.
					writeNext(new String[]{a.getPID(), a.getLocalPath(), a.getName(), a.getPreview(), String.valueOf(a.getGrade())}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.result.clear();
	}

	private int getTargetIndex(String context) {
		Matcher matcher;
		int index = -1;
		switch (this.MODE) {
			case 1:
				//空格分隔的contains-NOT模式：
				//key形如“关键词1 关键词2 关键词3 NOT 关键词4 关键词5”：包含1~3，且不包含4、5（not后面的均排除）
				boolean satisfied=true;
				String[] hasWord= Arrays.stream(target.split(" ")).
						filter(StringUtils::isNotBlank).toArray(String[]::new);
				int i;
				if(ArrayUtils.isEmpty(hasWord)) break;
				//包括应有的关键词
				for (i = 0; i < hasWord.length; i++) {
					if(hasWord[i].equals("NOT")) break;
					satisfied = context.contains(hasWord[i]);
					if(!satisfied) {
						i=hasWord.length;
						break;
					}
				}
				i++;
				//不包括应排除的关键词
				for (; i < hasWord.length; i++) {
					satisfied&= !context.contains(hasWord[i]);
				}
				if(satisfied) index=context.indexOf(hasWord[0]);
				break;
			case 2: //正则匹配模式
				matcher = this.match.matcher(context);
				if (matcher.find()) {
					index = context.indexOf(matcher.group());
				}
				break;
			case 3:
				//TODO: 1中分割方式的，关键词为正则的形式
				break;
		}
		return index;
	}


	public void run() {
		for (int i = 0; i < searchTask.size(); i++) {
			TextPath textPath = searchTask.get(i);
			Path s = Paths.get(textPath.getPath());
			//若超时
			if (System.currentTimeMillis() - this.startTime > 15000L) {
				//this.logger.info("未搜索完数量 = {}/{}", this.searchTask.size() - i, this.searchTask.size());
				surplus+=searchTask.size() - i;
				//if(Tid==1){
				//}
				break;
			}else if(succeedNum>MAX_SEARCH_NUM){
				break;
			}

			try {
				String context = getContext(textPath, s);
				int index = getTargetIndex(context);
				if (index >= 0) {
					String PID="888888888888",localPath,name,preview;
					succeedNum++;
					Integer grade=0;

					preview = context.substring(Math.max(0, index - 40), Math.min(context.length(), index + 40));
					preview=preview.replaceAll("[\n\r\t]", " ");
					name=s.toFile().getName();
					if(context.length()>=20000) name+="【"+context.length()/10000+"万长文】";
					localPath=s.toFile().getAbsolutePath();
					Matcher matcher=this.PID.matcher(name);
					if(matcher.find()) PID= matcher.group().replaceAll("[-.]", "");
					//if(PID.length()<7||PID.length()>8) PID="";
					//grade=novelGrade.getNovelGrade(context);
					SearchResult searchResult=new SearchResult(PID,localPath, name, preview, grade);
					//System.out.println(1);

					this.result.add(searchResult);
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		//System.out.println("result.size() = " + result.size());
		saveTemp();
		this.countDownLatch.countDown();
	}

	private String getContext(TextPath textPath, Path s) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(s.toFile());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

		String context;
		Integer fileLength = textPath.getFileLength();
		//测试：长文只搜索部分
		if(fileLength>200000) {
			//对于长文：取前5w字，保证初始观看的地方被检索了，然后全文每1/5的地方各取1w字

			StringBuilder sb=new StringBuilder(100000);
			char[] buffer=new char[1024];
			for (int i = 0; i < 50; i++) {
				int nums=bufferedReader.read(buffer,0,buffer.length);
				sb.append(buffer,0,nums);
			}
			//sb.append(context,0,50000);
			for (int j = 50*1024; j < fileLength; j+=fileLength/5) {
				for (int i = 0; i < 10; i++) {
					int nums=bufferedReader.read(buffer,0,buffer.length);
					nums= Math.max(nums, 0);
					sb.append(buffer,0, nums);
				}
				bufferedReader.skip(fileLength/5-10*1024);
			}
			context=sb.toString();
		}else{
			context= s.toFile().getName() + "     " + IOUtils.toString(bufferedReader);
		}
		return context;
	}
}


/* Location:              D:\Spring\mvn_repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\component\searchThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */