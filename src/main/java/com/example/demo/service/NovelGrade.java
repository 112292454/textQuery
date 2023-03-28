package com.example.demo.service;

import com.example.demo.dao.GradeMapper;
import com.example.demo.entity.Grade;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.max;

@Service
public class NovelGrade {
	@Resource
	GradeMapper gradeMapper;

	private final HashMap<String,Integer> g;

	private final HashMap<String,Integer> specialAuthorType;

	private static HashMap<Integer, grade> cache=new HashMap<>();

	//存放所有有价值评分的词语的hash，当strs截取的时候，仅当在这里面存在的词才会被截取
	boolean[] wordHash=new boolean[10000];

	//有涩涩至少应该有的单词
	private final String[] LeastContainsWords= ("\u7389\u8DB3|\u7231\u6DB2|\u79C1\u5904|\u6DEB|\u540E\u7A74|\u9A9A\u7A74|\u5C4C|\u62BD\u63D2|\u5C04\u7CBE|\u7CBE\u6DB2|" +
			"\u9634\u9053|\u5C0F\u7A74|\u871C\u7A74|\u4FB5\u72AF|\u80F4\u4F53|\u8089\u68D2|\u9E21\u5DF4")
			.split("\\|");

	public NovelGrade(GradeMapper gradeMapper) {
		this.gradeMapper = gradeMapper;

		HashMap<String,Integer> g=new HashMap<>();
		List<Grade> gradeList = gradeMapper.loadAllTrueGrade();
		gradeList.forEach(a->g.put(a.getStr(),a.getGrade()));
		g.forEach((k,v) ->wordHash[k.hashCode()&8191] =true);
		this.g=g;


		specialAuthorType = new HashMap<>();
		{
			specialAuthorType.put("七叶草（暂停接稿- -）", 1);
			specialAuthorType.put("流星龙（暂停接稿）", 1);
			specialAuthorType.put("薇尔维特", 1);
			specialAuthorType.put("梵摩咸鱼", 1);

			specialAuthorType.put("欢喜天", 0);
			specialAuthorType.put("逛大臣", 0);
			specialAuthorType.put("箔飄燈", 0);
			specialAuthorType.put("陈灵墟", 0);
			specialAuthorType.put("涩到不行的曦", 0);
			specialAuthorType.put("姆拉达大主教", 0);
			specialAuthorType.put("烦内（popote）", 0);
			specialAuthorType.put("我并不色", 0);
			specialAuthorType.put("夜天小姐要我做她的吸血鬼", 0);
			specialAuthorType.put("码字姬", 0);
			specialAuthorType.put("片翼", 0);
			specialAuthorType.put("六合混沌", 0);
			specialAuthorType.put("茶茶", 0);





		}
	}

	public Integer getNovelGrade(String novel){
		//TODO:采用字典树存储评分的str字段并查找，能否提高效率未知
		grade grade = makeCache(novel);
		return grade.main;
	}

	public Long getRawGrade(String novel){
		//TODO:采用字典树存储评分的str字段并查找，能否提高效率未知
		grade grade = makeCache(novel);
		return grade.raw;
	}

	public Double getAveGrade(String novel){
		//TODO:采用字典树存储评分的str字段并查找，能否提高效率未知
		grade grade = makeCache(novel);
		return grade.ave;
	}

	private grade makeCache(String novel) {
		if(cache.containsKey(novel.hashCode())) return cache.get(novel.hashCode());
		HashMap<String, Integer> gradeMap = getRawStrGradeMap(novel);
		long raw = raw(gradeMap);
		grade grade = new grade(raw, getGrade(novel, gradeMap), 1.0 * raw / novel.length());
		cache.put(novel.hashCode(), grade);
		return grade;
	}



	private HashMap<String, Integer> getRawStrGradeMap(String novel) {
		HashMap<String,Integer> cnt=new HashMap<>();

		for (int i = 1; i <=4; i++) {
			String temp;
			for (int j = 0; j < novel.length()-i-1;j++ ) {
				temp = novel.substring(j, j + i);
				if (wordHash[temp.hashCode()&8191] && g.containsKey(temp)) {
					cnt.put(temp, cnt.getOrDefault(temp, 0) + 1);
				}
			}
		}
		if(novel.length()>50000){
			int c=0;
			for (String word : LeastContainsWords) {
				if (cnt.containsKey(word)) {
					c++;
					c += cnt.get(word) / 10;
				}else if(novel.contains(word)){
					c++;
				}
			}
			if(c<2) {
				cnt.clear();//完全没有涩涩最基础的单词
			}
		}


		return cnt;
	}


	private double lengthFunc(String s){
		int x=s.length();
		return Math.sqrt(x*3)+max(0,-218.136*Math.log(x)+1600.528);
	}

	private Double specialAdjustByAuthor(Double res ,String title){

		for (Map.Entry<String, Integer> entry : specialAuthorType.entrySet()) {
			String k = entry.getKey();
			Integer v = entry.getValue();
			if (title.lastIndexOf(k) >= 0) {
				switch (v) {
					case 1: {
						if (res > 200) res += 25;//225+
						else if (res > 150) res += 50;//200-250
						else if (res > 100) res += 100;//200-250
						else if (res > 50) res += 100;//150-200
						else if (res > 0) res += 120;//120-170
					}
				}
			}
		}

		return new Double(res.doubleValue());
	}

	class grade{
		long raw;
		int main;
		double ave;

		public grade(long raw, int main, double ave) {
			this.raw = raw;
			this.main = main;
			this.ave = ave;
		}
	}

	private long raw(HashMap<String, Integer> cnt) {
		AtomicLong res= new AtomicLong();
		cnt.forEach((k, v)->  res.addAndGet(g.get(k)*v));//v次数，get(k)是分数
		return res.get();
	}
	private int getGrade(String novel, HashMap<String, Integer> cnt) {
		AtomicReference<Double> res = new AtomicReference<>((double) 0);
		double lengthParam=lengthFunc(novel);
		cnt.forEach((k, v)-> res.updateAndGet(v1 ->
				v1 + Math.log(v+1.718) * g.get(k)));//v次数，k分数
		res.updateAndGet(v -> (v / lengthParam));

		if(novel.contains(".txt"))
			res.set(specialAdjustByAuthor(res.get(), novel.substring(0, novel.indexOf(".txt"))));
		return (int) res.get().longValue();
	}
}
