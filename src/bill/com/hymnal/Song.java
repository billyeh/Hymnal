package bill.com.hymnal;

public class Song {
	private String[] songArray;
	private String chorus;
	private String sheetMusic;
	private String url;

	public Song(String url, String[] songArray, String chorus, String sheetMusic) {
		this.url = url;
		this.songArray = songArray;
		this.chorus = chorus;
		this.sheetMusic = sheetMusic;
	}
	public String[] getSongArray() {
		return this.songArray;
	}
	public String getChorus() {
		return this.chorus;
	}
	public String getUrl() {
		return this.url;
	}
	public String getSheetMusic() {
		return this.sheetMusic;
	}
	public void setSongArray(String[] array) {
		this.songArray = array;
	}
	public void setChorus(String chorus) {
		this.chorus = chorus;
	}
	public void setSheetMusic(String sheetMusic) {
		this.sheetMusic = sheetMusic;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
