package com.example.demo.service;

import com.example.demo.component.searchThread;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import com.example.demo.entity.TextPath;
import org.springframework.stereotype.Service;


/**
 * 多线程分发文件
 */
@Service
public class FileDistribute {

	private List<searchThread> threads;
	private List<TextPath> tasks;
	private String attr;

	public void setAttr(String attr) {
		this.attr = attr;
	}


	public void setThreads(List<searchThread> t) {
		this.threads = t;
	}

	public void setTasks(List<TextPath> t) {
		this.tasks = t;
	}
	public int getTasksNum() {
		return this.tasks.size();
	}

	public void execute() {
		int threadCount = this.threads.size();
		Random random = new Random();
		for (TextPath task : this.tasks) {

			int rand = random.nextInt(threadCount);

			(this.threads.get(rand)).addSearchTaskFile(task);
		}
	}
}

