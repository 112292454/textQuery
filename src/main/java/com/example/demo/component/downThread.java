//package com.example.demo.component;
//
//import DownloadTools.DownLoad;
//import com.example.demo.dao.TextDao;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Iterator;
//
//import lombok.Data;
//import org.apache.commons.io.IOUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Attributes;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//
//public class downThread extends Thread {
//	private int id;
//	private int step;
//	private int startID;
//	private int endID;
//	private int lastSuccID;
//	private int nowID;
//	private int faildCombo = 0;
//	private int succeedCount = 0;
//	private int lastReport;
//	private static int bannedTimes = 0;
//
//	private static final String url = "https://www.pixiv.net/novel/show.php?id=";
//	private static String path;
//	private File log;
//	private TextDao dao;
//
//	public downThread(int id, int step) {
//		this(id, step, 9000000, 20000000);
//	}
//
//	public downThread(int id, int step, int startID, int endID) {
//		this.id = id;
//		this.step = step;
//		this.startID = startID;
//		this.lastReport = startID;
//		this.nowID = startID;
//		this.lastSuccID = startID;
//		this.endID = endID;
//		this.log = new File("log" + id + ".txt");
//		if (!this.log.exists()) {
//			try {
//				this.log.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public void setPath(String path) {
//		downThread.path = path;
//	}
//
//	public void setDao(TextDao dao) {
//		this.dao = dao;
//	}
//
//	private String textWrite(String name, String content) {
//		name = DownLoad.filenameFilter(name);
//		name = path + "\\" + name + ".jpg";
//		File file = new File(name);
//		if (!file.exists()) {
//			try {
//				file.createNewFile();
//			} catch (IOException e) {
//				errlog("file", name);
//			}
//		}
//		try (FileWriter writer = new FileWriter(file)) {
//			writer.write(content);
//			writer.flush();
//		} catch (IOException e) {
//			errlog("file", name);
//		}
//		return name;
//	}
//
//	private boolean isChina(String page) {
//		if (page.contains("\"tag\":\"中文\"")) return true;
//		return false;
//	}
//
//	private void errlog(String type, String content) {
//		errlog(type, content, "");
//	}
//
//	private void errlog(String type, String content, String url) {
//		try (FileWriter writer = new FileWriter(this.log, true)) {
//			switch (type) {
//				case "file":
//					writer.append("保存本地文件错误：" + content + "\n");
//					break;
//
//				case "open":
//					if (content.startsWith("http")) {
//						break;
//					}
//					this.faildCombo++;
//					writer.append("网页打开错误：" + content + "：" + url + "\n");
//					break;
//
//
//				case "parse":
//					writer.append("解析网页错误：" + content + "：" + url + "\n");
//					break;
//
//				default:
//					writer.append("未知错误：" + content + "：" + url + "\n");
//					break;
//			}
//
//
//			writer.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private String getPage(String url) throws Exception {
//		InputStream inputStream = DownLoad.getUrlInputStreamVpn(url);
//		String page = IOUtils.toString(new InputStreamReader(inputStream));
//		return page;
//	}
//
//	private String parsePage(int id, String page) throws Exception {
//		int titleIndex = page.indexOf("<meta property=\"twitter:title\" content=\"") + "<meta property=\"twitter:title\" content=\"".length();
//		String title = page.substring(titleIndex, page.indexOf('"', titleIndex));
//		int authorIndex = page.indexOf("\"userName\":\"") + "\"userName\":\"".length();
//		String author = page.substring(authorIndex, page.indexOf('"', authorIndex));
//		return id + "-" + title + "-" + author;
//	}
//
//	private String getContent(String page) throws Exception {
//		Document doc = Jsoup.parse(page);
//		Elements elements = doc.getAllElements();
//		Element preload = elements.select("#meta-preload-data").first();
//		Attributes content = preload.attributes();
//		String s = content.get("content");
//		JsonParser parser = new JsonParser();
//		JsonElement rootNode = parser.parse(s);
//
//		if (rootNode.isJsonObject()) {
//			JsonObject details = rootNode.getAsJsonObject();
//			JsonObject novel = details.get("novel").getAsJsonObject();
//			Iterator<String> keys = novel.keySet().iterator();
//			if (keys.hasNext()) {
//				String key = keys.next();
//				String con = novel.get(key).getAsJsonObject().get("content").getAsString();
//				return con;
//			}
//		}
//		return "下载失败的链接：https://www.pixiv.net/novel/show.php?id=";
//	}
//
//	public static int index = 0;
//
//
//	public synchronized void run() {
//		while (index < 10000) {
//
//
//			index = DownLoad.down("https://iw233.cn/api/Random.php", "D:\\test\\", String.valueOf(index));
//		}
//
//
//		for (; this.nowID < this.endID; this.nowID += this.step) {
//			if (this.faildCombo > 10) {
//				bannedTimes++;
//			} else if (this.faildCombo == 0) {
//				bannedTimes = Math.max(0, bannedTimes - 1);
//			}
//
//			if (bannedTimes >= this.step) {
//				System.err.println("连续访问失败，疑似被禁止访问!\n暂停五分钟崽尝试");
//				try {
//					sleep(300000L);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				bannedTimes = 0;
//				this.nowID = this.lastSuccID;
//			}
//
//			if (this.succeedCount > 0 && (this.nowID - this.startID) % 1000 * this.step == 0) {
//				System.out.println("线程" + this.id + "当前处于：" + this.nowID + ",阶段性处理" + ((this.nowID - this.lastReport) / this.step) + "项，其中成功下载" + this.succeedCount + "项");
//				this.lastReport = this.nowID;
//				this.succeedCount = 0;
//			}
//
//			String url = "https://www.pixiv.net/novel/show.php?id=" + this.nowID;
//
//			String page = null, name = null, content = null;
//
//			try {
//				try {
//					page = getPage(url);
//				} catch (Exception e) {
//					errlog("open", e.getMessage(), url);
//				}
//
//
//				if (isChina(page)) {
//					try {
//						name = parsePage(this.nowID, page);
//						content = getContent(page);
//					} catch (Exception e) {
//						errlog("parse", e.getMessage(), url);
//					}
//
//					try {
//						name = textWrite(name, content);
//						this.dao.addTextPath(new TextPath(this.nowID+"", name));
//					} catch (Exception e) {
//						errlog("file", e.getMessage());
//					}
//
//
//					this.faildCombo = 0;
//					this.succeedCount++;
//				}
//			} catch (Exception e) {
//				errlog("", e.getMessage(), url);
//				e.printStackTrace();
//			}
//
//		}
//		System.out.println("下载完毕");
//	}
//}