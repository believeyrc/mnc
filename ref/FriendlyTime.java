import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FriendlyTime {
	String friendlyTime(Date date) {
		Calendar now = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date.getTime());
		if (now.getTimeInMillis() < c.getTimeInMillis())
			return "";
		long diffi = now.getTimeInMillis() - c.getTimeInMillis();
		if (diffi < 60 * 1000L)
			return (diffi / 1000L) + "秒";
		if (diffi < 60 * 60 * 1000L)
			return (diffi / (60 * 1000L)) + "分钟";
		if (diffi < 3600 * 1000L * 24)
			return (diffi / (3600 * 1000L)) + "小时";
		if (diffi < 3600 * 1000L * 24 * 7)
			return (diffi / (3600 * 1000L * 24)) + "天";
		if (diffi < 3600 * 1000L * 24 * 40)
			return (diffi / (3600 * 1000L * 24 * 40)) + "周";
		if (diffi < 3600 * 1000L * 24 * 365)
			return (diffi / (3600 * 1000L * 24 * 30)) + "月";
		return (diffi / (3600 * 1000L * 24 * 365)) + "年";
	}

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FriendlyTime ft = new FriendlyTime();
		System.out.println(ft.friendlyTime(sdf.parse("2010-01-18 16:17:2"))+ "以前");
		System.out.println(ft.friendlyTime(sdf.parse("2010-01-18 16:14:2"))+ "以前");
		System.out.println(ft.friendlyTime(sdf.parse("2010-01-18 16:10:2"))+ "以前");
		System.out.println(ft.friendlyTime(sdf.parse("2010-01-18 12:22:2"))+ "以前");
		System.out.println(ft.friendlyTime(sdf.parse("2010-01-17 12:22:2"))+ "以前");
		System.out.println(ft.friendlyTime(sdf.parse("2009-01-17 12:22:2"))+ "以前");
		System.out.println(ft.friendlyTime(sdf.parse("2009-11-17 12:22:2"))+ "以前");
	}
}
