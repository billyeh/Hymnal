package bill.com.hymnal;

public class Song {
	private String[] songArray;
	private String chorus;

	public Song(String[] songArray, String chorus) {
		this.songArray = songArray;
		this.chorus = chorus;
	}
	public String[] getSongArray() {
		return this.songArray;
	}
	public String getChorus() {
		return this.chorus;
	}
}
