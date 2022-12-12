package com.example.demo.component;

import com.opencsv.bean.CsvBindByName;

public class TempFileDTO {
	@CsvBindByName(column = "path")
	private String path;

	public void setPath(String path) {
		this.path = path;
	}

	@CsvBindByName(column = "preview")
	private String preview;

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof com.example.demo.component.TempFileDTO)) return false;
		com.example.demo.component.TempFileDTO other = (com.example.demo.component.TempFileDTO) o;
		if (!other.canEqual(this)) return false;
		Object this$path = getPath(), other$path = other.getPath();
		if ((this$path == null) ? (other$path != null) : !this$path.equals(other$path)) return false;
		Object this$preview = getPreview(), other$preview = other.getPreview();
		return !((this$preview == null) ? (other$preview != null) : !this$preview.equals(other$preview));
	}

	protected boolean canEqual(Object other) {
		return other instanceof com.example.demo.component.TempFileDTO;
	}



	public String toString() {
		return "TempFileDTO(path=" + getPath() + ", preview=" + getPreview() + ")";
	}

	public String getPath() {
		return this.path;
	}

	public String getPreview() {
		return this.preview;
	}
}


/* Location:              C:\Users\gzy\.m2\repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\component\TempFileDTO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */