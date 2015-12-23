import java.awt.Point;
import java.util.ArrayList;
import videosummary.*;
public class EntryPoint {
	public static String getenv(String key, String def) {
		String value = System.getenv(key);
		if (value == null)
			return def;
		return value;
	}
	public static void main(String[] args) {
		ArrayList<Point> list_point=new ArrayList<Point>();
		Point p0=new Point(205,44);
		Point p1=new Point(387,43);
		Point p2=new Point(596,322);
		Point p3=new Point(3,317);
		list_point.add(p0);
		list_point.add(p1);
		list_point.add(p2);
		list_point.add(p3);
		int compressedRate = Integer.parseInt(getenv("COMPRESSED_RATE", "15"));
		double overlap = Double.parseDouble(getenv("OVERLAP", "1.0"));
		Tracking tc = new Tracking("/tmp/objects", "/input",list_point);
		tc.begin();
		//视频浓缩过程		
		Concentrate con = new Concentrate(tc.getBG(),
		"/tmp/objects/input",
		"/output/",
		"12Ex.avi",
		"/usr/bin/ffmpeg",
		compressedRate,
		overlap);
		con.sethash(tc.getHash());
		con.start(tc.getBG());
	}
}
