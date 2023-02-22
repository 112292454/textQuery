package com.example.demo.service;

import com.example.demo.component.countDTO;
import com.opencsv.*;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 统计文件里关键词出现次数
 */
@Service
public class FileKeyWordCount {
	int tempCnt=0;

	private HashMap<String,Integer> wordCount=new HashMap<>();

	Pattern SymbolReplace=Pattern.compile("[\\\\./\\-+\r\n\t\"\\^，。、；‘【】！!@#$%…&*（）()—《》？：”“{}| 　`·~,;'<>?:�銆\\d]");
	public void doCount(String dir){
		collect();
		//File dirt = new File(dir);
		List<Path> paths = new ArrayList<>();
		try {
			paths =Files.walk(Paths.get(dir, new String[0]), new java.nio.file.FileVisitOption[0]).filter(a -> !a.toFile().isDirectory()).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}


		for (int i = 0; i < paths.size(); i++) {
			if(i%1000==0&&i!=0){
				Iterator<Map.Entry<String, Integer>> iterator = wordCount.entrySet().iterator();
				while (iterator.hasNext()){
					//删去一万个文件只出现一次的
					Map.Entry<String, Integer> e=iterator.next();
					if(e.getValue()<=3) iterator.remove();
				}
				saveTemp(i/1000);
			}

			Path path=paths.get(i);
			File file=path.toFile();
			try {
				String str=file.getName() + "     " + IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);
				str=str.toLowerCase(Locale.ROOT).replaceAll(SymbolReplace.toString(),"");
				ArrayList<String> temp;
				//获取文本

				for (int j = 2; j < 6; j++) {
					temp=sub(str,j);
					for (String s : temp) {
						wordCount.put(s,wordCount.getOrDefault(s,0)+1);
					}
					temp.clear();
				}
				//对所有长度1-8的字符串计数
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		Iterator<Map.Entry<String, Integer>> iterator = wordCount.entrySet().iterator();
		while (iterator.hasNext()){
			//删去一千个文件只出现一次的
			Map.Entry<String, Integer> e=iterator.next();
			if(e.getValue()<=3) iterator.remove();
		}
		saveTemp((paths.size()+1000)/1000);

		collect();
	}

	private ArrayList<String> sub(String s,int len){
		ArrayList<String> res=new ArrayList<>(s.length()+5);
		for (int i = 0; i < s.length()-len; i++) {
			res.add(s.substring(i, i + len));
		}
		return res;
	}

	private void saveTemp(int id) {
		File temp = new File("temp" + id + ".csv");
		if (!temp.exists()) {
			try {
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Set<Map.Entry<String, Integer>> entries = wordCount.entrySet();
		List<Map.Entry<String, Integer>> en=entries.stream().sorted((a,b)->b.getValue()-a.getValue()).collect(Collectors.toList());

		try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(temp), "utf-8"))) {
			csvWriter.writeNext(new String[]{"str", "grade"});
			en.forEach(a -> csvWriter.writeNext(new String[]{a.getKey(),String.valueOf(a.getValue())}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		wordCount.clear();
	}

	private void collect() {
		HeaderColumnNameMappingStrategy strategy = new HeaderColumnNameMappingStrategy();
		strategy.setType(countDTO.class);
		HashMap<String, Integer> res = new HashMap<>();

		for (int i = 1; i < 150; i++) {
			try (InputStreamReader in = new InputStreamReader(new FileInputStream("temp" + i + ".csv"), "utf-8")) {
				RFC4180Parser rfc4180Parser = (new RFC4180ParserBuilder()).build();
				CSVReader inputStream = (new CSVReaderBuilder(in)).withCSVParser(rfc4180Parser).build();


				CsvToBean csvToBean = (new CsvToBeanBuilder(inputStream)).withMappingStrategy(strategy).build();
				List<countDTO> csv = csvToBean.parse();
				csv.forEach(a ->  res.put(a.getStr(), a.getGrade()+res.getOrDefault(a.getStr(),0) ));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*try {
				Files.deleteIfExists(Paths.get("temp" + i + ".csv", new String[0]));
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}

		System.out.println("res.size() = " + res.size());

		Set<Map.Entry<String, Integer>> entries = res.entrySet();
		List<Map.Entry<String, Integer>> en=entries.stream().filter(a->a.getValue()>10).sorted((a,b)->b.getValue()-a.getValue()).collect(Collectors.toList());

		try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream("total.csv"), "utf-8"))) {
			csvWriter.writeNext(new String[]{"str", "grade"});
			en.forEach(a -> csvWriter.writeNext(new String[]{a.getKey(),String.valueOf(a.getValue())}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("collect成功");

	}
}
