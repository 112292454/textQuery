package com.example.demo.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
	@CsvBindByName(column = "PID")
	String PID;

	@CsvBindByName(column = "localPath")
    String localPath;

	@CsvBindByName(column = "name")
    String name;

	@CsvBindByName(column = "preview")
    String preview;

	@CsvBindByName(column = "grade")
    Integer grade;
}
