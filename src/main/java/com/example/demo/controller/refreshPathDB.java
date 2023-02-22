package com.example.demo.controller;

import DownloadTools.util.ChineseUtil;
import DownloadTools.util.FileUtil;
import com.example.demo.entity.TextPath;
import com.example.demo.dao.TextDao;
import com.example.demo.service.NovelGrade;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller()
public class refreshPathDB {

	public static final String UTF_8 = "utf-8";
	@Autowired
    TextDao textDao;
	@Autowired
	NovelGrade novelGrade;

	private static final Pattern PID=Pattern.compile("(-\\d{6,10}\\.)|(^\\d{6,10}-)");
	private static final Pattern ANY_ID=Pattern.compile("(-\\d{6,}\\.)|(^\\d{6,}-)");
	ArrayList<TextPath> tran=new ArrayList<>();
	long time=System.currentTimeMillis(),cnt=0;

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@RequestMapping("/refresh")
	public synchronized void run() {
		List<Path> paths = getPaths();

		HashMap<String,TextPath> index=new HashMap<>();

		Iterator<Path> itr = paths.iterator();
		while (itr.hasNext()){
			Path path=itr.next();
			String id="88888888";
			File file = path.toFile();
			String p = file.getAbsolutePath();
			String name = file.getName();
			String content=name+"      "+FileUtil.readText(file, UTF_8);
			long l=novelGrade.getRawGrade(content);
			int g=novelGrade.getNovelGrade(content);
			double ave= 1.0*l/content.length();
			if(content.contains("們")) l++;//权宜 保留繁体的

			if(p.contains("pixiv下载")){//查找对应的txt库路径
				Matcher matcher= PID.matcher(name);
				if(matcher.find()) id= matcher.group();
			}
			else if(p.contains("旧小说合集")){}
			else{
				itr.remove();
				continue;
			}

			if(id.equals("88888888")){//未找到id就无条件赋值一个时间相关的id
				Matcher matcher= ANY_ID.matcher(name);
				if(!matcher.find()) {
					id = getTempIndex();
					try {
						Path newP=Paths.get(file.getParent()+"\\"+id+"-"+file.getName());
						Files.move(path,newP);
						path=newP;
					} catch (IOException e) {
						e.printStackTrace();
						try {
							Files.move(path,Paths.get("D:\\\\txt下载\\不可用的\\"+ name));
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				} else id=matcher.group();
			}
			id=id.replaceAll("[-.]", "");


			if(l==0&&g==0&&ave==0) {
				try {
					if(ChineseUtil.isMessyCode(content)) {
						Files.move(path,Paths.get("D:\\\\txt下载\\不可用的\\乱码\\"+ name));
					} else {
						Files.move(path, Paths.get("D:\\txt下载\\0分的\\" + file.getName()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else{
				if(index.containsKey(id)){
					try {
						Files.move(path,Paths.get("D:\\\\txt下载\\不可用的\\重复的\\"+ name));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					TextPath textPath = new TextPath(id, path.toFile());
					assert new File(textPath.getPath()).exists();
					textPath.setRawGrade(l);
					textPath.setGrade(g);
					textPath.setAveGrade(ave);
					textPath.setFileLength(content.length());
					index.put(id,textPath);
				}
			}

		}

		/*
		Iterator<TextPath> iterator = tran.iterator();
		while (iterator.hasNext()) {
			// 删除元素
			File name= new File(textPath.getPath());
			long l=novelGrade.getRawGrade(FileUtil.readText(name, UTF_8));
			if(l==0) {
				try {
					Files.move(name.toPath(), Paths.get("D:\\txt下载\\0分的\\" + name.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				iterator.remove();
			}else{

			}
		}*/
		//ArrayList<TextPath> temp=new ArrayList<>();
		//for (int i = 0; i < tran.size(); i++) {
		//	if(i%10000==0&&i!=0) {
		//		//StringBuffer sb=new StringBuffer();
		//		//for (TextPath textPath : temp) {
		//		//	sb.append("('").append(textPath.getTid()).append("','").append(textPath.getPath()).append("'),");
		//		//}
		//		//sb.deleteCharAt(sb.length()-1);
		//		//textDao.addTextPathTest(sb.toString());
		//		textDao.addTextPathBatch(temp);
		//		temp.clear();
		//	}
		//	temp.add(tran.get(i));
		//}

		//textDao.addTextPathBatch(tran);
		index.forEach((k,v)-> tran.add(v));
		tran.sort(Comparator.comparingLong(a -> Long.parseLong(a.getTid())));

		System.out.println(new Date());
		batchInsert(tran);
		System.out.println(new Date());

	}

	//@RequestMapping("/temp")
	public void tempUpdate() {
		ArrayList<TextPath> textPaths = (ArrayList<TextPath>) textDao.tempGet();
		for (TextPath textPath : textPaths) {
			Path path = Paths.get(textPath.getPath());
			File file = path.toFile();
			String p = file.getAbsolutePath();
			String name = file.getName();
			String content=name+"      "+FileUtil.readText(file, UTF_8);
			Integer grade=novelGrade.getNovelGrade(content);
			textPath.setGrade(grade);
		}
		batchUpdate(textPaths);

	}

	private List<Path> getPaths() {
		List<Path> paths = null;
		try {
			paths = Files.walk(Paths.get("D:\\\\txt下载"), new java.nio.file.FileVisitOption[0])
					.filter(a -> (!a.toFile().isDirectory())&&a.toFile().getName().endsWith("txt"))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paths;
	}


	/**
	 * 待测试，理论上foreach应该快，但是实操非常慢，15w条插入要3分钟
	 * 之后改了几个其他的均无效（见上注释和dao
	 * 然后再数据库链接的配置加入&rewriteBatchedStatements=true，再采用下面这个提交方式，缩短成1分钟，但是依然很慢
	 * ————更新：加入了上述配置之后，用sqlsession就只要几秒了
	 *
	 * @param index
	 */


	private void batchInsert(ArrayList<TextPath> index){
		SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH);
		TextDao mapper = sqlSession.getMapper(TextDao.class);
		index.forEach(mapper::addTextPath);

		/*for (int i = 1; i < tran.size(); i++) {
			mapper.addTextPath(new TextPath());
		}*/
		//提交到数据库，保存数据
		sqlSession.flushStatements();
	}
	private void batchUpdate(ArrayList<TextPath> index){
		SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH);
		TextDao mapper = sqlSession.getMapper(TextDao.class);
		index.forEach(mapper::updateTextPath);

		/*for (int i = 1; i < tran.size(); i++) {
			mapper.addTextPath(new TextPath());
		}*/
		//提交到数据库，保存数据
		sqlSession.flushStatements();
	}

	private String getTempIndex() {
		if(System.currentTimeMillis()!=time){
			time=System.currentTimeMillis();
			cnt=1;
		}
		String tempIndex="000"+cnt++;
		tempIndex=tempIndex.substring(tempIndex.length()-3);
		tempIndex=System.currentTimeMillis()+tempIndex;
		return tempIndex;
	}

}
