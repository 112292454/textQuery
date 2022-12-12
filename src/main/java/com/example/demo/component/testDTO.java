package com.example.demo.component;

import com.opencsv.bean.CsvBindByName;

public class testDTO {
	@CsvBindByName(column = "name")
	private String path;

	public void setPath(String path) {
		this.path = path;
	}

	@CsvBindByName(column = "prompt")
	private String preview;
	@CsvBindByName(column = "negative_prompt")
	private String t;

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public void setT(String t) {
		this.t = t;
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof com.example.demo.component.testDTO)) return false;
		com.example.demo.component.testDTO other = (com.example.demo.component.testDTO) o;
		if (!other.canEqual(this)) return false;
		Object this$path = getPath(), other$path = other.getPath();
		if ((this$path == null) ? (other$path != null) : !this$path.equals(other$path)) return false;
		Object this$preview = getPreview(), other$preview = other.getPreview();
		if ((this$preview == null) ? (other$preview != null) : !this$preview.equals(other$preview)) return false;
		Object this$t = getT(), other$t = other.getT();
		return !((this$t == null) ? (other$t != null) : !this$t.equals(other$t));
	}

	protected boolean canEqual(Object other) {
		return other instanceof com.example.demo.component.testDTO;
	}


	public String toString() {
		return "testDTO(path=" + getPath() + ", preview=" + getPreview() + ", t=" + getT() + ")";
	}

	public String getPath() {
		return this.path;
	}

	public String getPreview() {
		return this.preview;
	}

	public String getT() {
		return this.t;
	}
}


