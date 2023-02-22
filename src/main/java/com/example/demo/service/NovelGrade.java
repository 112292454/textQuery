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

	public NovelGrade(GradeMapper gradeMapper) {
		this.gradeMapper = gradeMapper;

		HashMap<String,Integer> g=new HashMap<>();
		List<Grade> gradeList = gradeMapper.loadAllTrueGrade();
		gradeList.forEach(a->g.put(a.getStr(),a.getGrade()));
		this.g=g;


		specialAuthorType = new HashMap<>();
		{
			specialAuthorType.put("七叶草（暂停接稿- -）", 1);
			specialAuthorType.put("流星龙（暂停接稿）", 1);
		}
	}

	public Integer getNovelGrade(String novel){
		//TODO:采用字典树存储评分的str字段并查找，能否提高效率未知
		AtomicReference<Double> res = new AtomicReference<>((double) 0);
		HashMap<String, Integer> cnt = getRawStrGradeMap(novel);
		double lengthParam=lengthFunc(novel);
		cnt.forEach((k,v)-> res.updateAndGet(v1 ->
				v1 + Math.log(v+1.718) * g.get(k)));//v次数，k分数
		res.updateAndGet(v -> (v / lengthParam));

		if(novel.contains(".txt"))
			res.set(specialAdjustByAuthor(res.get(),novel.substring(0,novel.indexOf(".txt"))));
		return (int) res.get().longValue();
	}

	public Long getRawGrade(String novel){
		//TODO:采用字典树存储评分的str字段并查找，能否提高效率未知
		AtomicLong res= new AtomicLong();
		HashMap<String, Integer> cnt = getRawStrGradeMap(novel);
		cnt.forEach((k,v)->  res.addAndGet(g.get(k)));//v次数，k分数
		return res.get();
	}

	/**
	 *
	 * @param novel
	 * @return
	 */
	public double getAveGrade(String novel){
		return 1.0*getRawGrade(novel) / novel.length();
	}

	private HashMap<String, Integer> getRawStrGradeMap(String novel) {
		HashMap<String,Integer> cnt=new HashMap<>();
		for (int i = 1; i <=4; i++) {
			String temp;
			for (int j = 0; j < novel.length()-i-1; ) {
				temp= novel.substring(j,j+i);
				if(g.containsKey(temp)) {
					cnt.put(temp,cnt.getOrDefault(temp,0)+1);
					j+=temp.length();
				}else{
					j++;
				}
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

}
