package com.ayke.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class ReadPropertiesUtil {

	public static List<ClassItem> getFragmentList() {
		List<ClassItem> list = new ArrayList<ClassItem>();
		Properties propertier = new Properties();
		Class<ReadPropertiesUtil> curClass = ReadPropertiesUtil.class;
		InputStream is = null;
		is = curClass.getResourceAsStream("fragment.properties");
		try {
			propertier.load(is);
			Iterator<Entry<Object, Object>> iterator = propertier.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<Object, Object> entry = iterator.next();
				String tag = (String) entry.getKey();
				System.out.println("fragment key=" + tag + " value="
						+ entry.getValue());
				list.add(new ClassItem((String) entry.getValue(),
						(String) entry.getKey(), false));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<ClassItem> getActivityList() {
		List<ClassItem> list = new ArrayList<ClassItem>();
		Properties propertier = new Properties();
		Class<ReadPropertiesUtil> curClass = ReadPropertiesUtil.class;
		InputStream is = null;
		is = curClass.getResourceAsStream("activity.properties");
		try {
			propertier.load(is);
			Iterator<Entry<Object, Object>> iterator = propertier.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<Object, Object> entry = iterator.next();
				String tag = (String) entry.getKey();
				System.out.println("activity key=" + tag + " value="
						+ entry.getValue());
				list.add(new ClassItem((String) entry.getValue(),
						(String) entry.getKey(), true));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
