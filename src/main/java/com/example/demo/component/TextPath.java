package com.example.demo.component;
public class TextPath {
	private int id;

	public void setId(int id) {
		this.id = id;
	}

	private long tid;
	private String path;

	public void setTid(long tid) {
		this.tid = tid;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof com.example.demo.component.TextPath)) return false;
		com.example.demo.component.TextPath other = (com.example.demo.component.TextPath) o;
		if (!other.canEqual(this)) return false;
		if (getId() != other.getId()) return false;
		if (getTid() != other.getTid()) return false;
		Object this$path = getPath(), other$path = other.getPath();
		return !((this$path == null) ? (other$path != null) : !this$path.equals(other$path));
	}

	protected boolean canEqual(Object other) {
		return other instanceof com.example.demo.component.TextPath;
	}


	public String toString() {
		return "TextPath(id=" + getId() + ", tid=" + getTid() + ", path=" + getPath() + ")";
	}

	public int getId() {
		return this.id;
	}

	public long getTid() {
		return this.tid;
	}

	public String getPath() {
		return this.path;
	}

	public TextPath(long tid, String path) {
		this.tid = tid;
		this.path = path;
	}
}


/* Location:              C:\Users\gzy\.m2\repository\com\example\textQuery\0.0.1-SNAPSHOT\textQuery-0.0.1-SNAPSHOT.jar!\BOOT-INF\classes\com\example\demo\component\TextPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */