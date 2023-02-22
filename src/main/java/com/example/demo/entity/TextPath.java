package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextPath {
	private static final long serialVersionUID = 4526045627485604L;

	@TableId(type = IdType.AUTO)
	/**
	 * tid
	 */
	private String tid;

	/**
	 * file_directory
	 */
	private String fileDirectory;

	/**
	 * file_name
	 */
	private String fileName;

	/**
	 * grade
	 */
	private Integer grade;

	/**
	 * raw_grade
	 */
	private Long rawGrade;

	/**
	 * ave_grade
	 */
	private Double aveGrade;

	/**
	 * file_length
	 */
	private Integer fileLength;

	/**
	 * is_favor
	 */
	private Boolean isFavor;



	public TextPath(String tid, File file) {
		this.tid = tid;
		this.fileName=file.getName();
		this.fileDirectory=file.getParent();
	}

	public String getPath() {
		return fileDirectory+"\\"+fileName;
	}
}


/* Location:              C:\Users\gzy\.m2\repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\component\TextPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */