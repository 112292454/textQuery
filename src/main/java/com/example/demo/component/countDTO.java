package com.example.demo.component;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class countDTO {
	@CsvBindByName(column = "str")
	private String str;


	@CsvBindByName(column = "grade")
	private Integer grade;


}


/* Location:              C:\Users\gzy\.m2\repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\component\TempFileDTO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */