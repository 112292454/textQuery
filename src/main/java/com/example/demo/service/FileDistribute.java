package com.example.demo.service;

import com.example.demo.component.searchThread;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;


/**
 * 多线程分发文件
 */
@Service
public class FileDistribute {

	private List<searchThread> threads;
	private List<Path> tasks;
	private String attr;

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public boolean addThread(searchThread t) {
		return this.threads.add(t);
	}

	public void setThreads(List<searchThread> t) {
		this.threads = t;
	}

	public boolean addTask(String t) {
		return this.tasks.add(Paths.get(t, new String[0]));
	}

	public void setTasks(List<Path> t) {
		this.tasks = t;
	}

	public void execute() {
		int threadCount = this.threads.size();
		Random random = new Random();
		for (Path task : this.tasks) {
			int rand = random.nextInt(threadCount);

			((searchThread) this.threads.get(rand)).addSearchTaskFile(task);
		}
	}
}

