package com.york.cs.couchbaseapi.queryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapExplorer {

	public static void main(String[] args) {

		List<String> listStrings = new ArrayList<String>();
		listStrings.add("a");
		listStrings.add("b");
		listStrings.add("c");

		List<String> listStrings2 = new ArrayList<String>();
		listStrings2.add("a");
		listStrings2.add("e");
		listStrings2.add("f");

		String address = "type.coments";

		// String address = "type.comments.text2";
		// System.out.println(address);
		String[] parts = address.split("\\.");
		// System.out.println(parts[0]);
		for (String s : parts) {
			// System.out.println("s: :"+s);
		}

		Map<String, Object> post = new HashMap<String, Object>();

		post.put("name", "name13");

		Map<String, Object> author = new HashMap<String, Object>();

		author.put("name", "nameAuth");
		author.put("name1", "nameAuth1");

		Map<String, Object> comment0 = new HashMap<String, Object>();
		comment0.put("text", "c0text1");
		comment0.put("text2", "c0text2");
		comment0.put("texts", listStrings);
		Map<String, Object> comment1 = new HashMap<String, Object>();
		comment1.put("text", "c1text1");
		comment1.put("text2", "c1text2");
		comment1.put("texts", listStrings2);

		// relations
		List<Map<String, Object>> comments = new ArrayList<>();
		// comment
		comments.add(comment0);
		comments.add(comment1);

		post.put("comments", comments);
		post.put("Author", author);
		List<Object> listKeys = new ArrayList<Object>();

		find(post, address, listKeys);

		/*
		for (Object obj : listKeys) {
			System.out.println(obj);
		}
		 */
	}

	public static void find(Map<String, Object> properties, String address,
			List<Object> listKeys) {

		String regex = "\\.";

		String[] parts = address.split(regex);
		int lastIndex = parts.length;

		// si lenth <2 error

		Object obj = findValue(properties, parts[1]);

		if (lastIndex > 2) {

			address = address.substring(address.indexOf(".") + 1);

			if (obj instanceof List) {

				List<Object> list = (List<Object>) obj;
				for (Object objList : list) {

					find((Map<String, Object>) objList, address, listKeys);
				}

			} else {

				find((Map<String, Object>) obj, address, listKeys);

			}
		} else {

			if (obj instanceof List) {

				listKeys.addAll((List<Object>) obj);

			} else {
				listKeys.add(obj);

			}
		}

	}

	public static Object findValue(Map<String, Object> properties, String field) {

		if (properties != null && properties.containsKey(field)) {

			return properties.get(field);

		} else
			return null;

	}

	public Object getKeys(Object object) {

		return null;
	}

}
