package com.data.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xml工具类
 * @author alex
 *
 */
public class XmlUtil {
	
	private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);
	
	/**
	 * 将xml报文转换为map对象
	 * @param xml
	 * @return
	 */
	public static Map<String, Object> xmlToMap(String xml) {
		Map<String, Object> map = new HashMap<>();
		Document doc;
		try {
			doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			map = reGetElementsValue(root, map);
		} catch (DocumentException e) {
			logger.info("--->>>XmlUtil=>xmlToMap方法异常 msg: " + e);
		}
		return map;
	}
	
	/**
	 * 得到结点下所有子节点的值
	 * @param root
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> reGetElementsValue(Element root, Map<String, Object> map) {
		List<Element> elementLists = root.elements();
		if(elementLists.size() == 0) {
			map.put(root.getName(), root.getTextTrim());
		} else {
			Iterator<Element> it = elementLists.iterator();
			while(it.hasNext()) {
				Element element = it.next();
				reGetElementsValue(element, map);
			}
		}
		return map;
	}
}
