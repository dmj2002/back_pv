package com.hust.ewsystem.common.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.ewsystem.common.constant.ResultCodeEnum;
import com.hust.ewsystem.common.exception.EwsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Json工具类
 */
public class JsonUtil {

	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	private static final ObjectMapper mapper;

	public static ObjectMapper getObjectMapper(){
		return mapper;
	}

	static {
		mapper = new ObjectMapper();
	}

	/**
	 * 对象转为字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String ObjectToJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("对象转字符串异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "对象转字符串异常", e);
		}
	}

	/**
	 * 对象转为byte数组
	 *
	 * @param obj
	 * @return
	 */
	public static byte[] objectToByteArray(Object obj) {
		try {
			return mapper.writeValueAsBytes(obj);
		} catch (Exception e) {
			logger.error("对象转为byte数组异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "对象转为byte数组异常", e);
		}
	}

	/**
	 * json字符串转为对象
	 *
	 * @param jsonStr
	 * @param beanType
	 * @param <T>
	 * @return
	 */
	public static <T> T jsonToObject(String jsonStr, Class<T> beanType) {
		try {
			return mapper.readValue(jsonStr, beanType);
		} catch (Exception e) {
			logger.error("json字符串转为对象异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "json字符串转为对象异常", e);
		}
	}

	/**
	 * byte数组转为对象
	 *
	 * @param byteArray
	 * @param beanType
	 * @param <T>
	 * @return
	 */
	public static <T> T byteArrayToObject(byte[] byteArray, Class<T> beanType) {
		try {
			return mapper.readValue(byteArray, beanType);
		} catch (Exception e) {
			logger.error("byte数组转为对象异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "byte数组转为对象异常", e);
		}
	}

	/**
	 * 集合转为字符串
	 *
	 * @param list
	 * @return
	 */
	public static String listToString(List list) {
		try {
			return mapper.writeValueAsString(list);
		} catch (Exception e) {
			logger.error("集合转为字符串异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "集合转为字符串异常", e);
		}
	}

	/**
	 * 字符串转集合
	 *
	 * @param jsonStr
	 * @return
	 */
	public static List jsonToList(String jsonStr) {
		try {
			return mapper.readValue(jsonStr, List.class);
		} catch (Exception e) {
			logger.error("字符串转集合异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "字符串转集合异常", e);
		}
	}

	/**
	 * Map转为字符串
	 *
	 * @param map
	 * @return
	 */
	public static String mapToString(Map map) {
		try {
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			logger.error("Map转为字符串异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "Map转为字符串异常", e);
		}
	}

	/**
	 * 字符串转Map
	 *
	 * @param jsonStr
	 * @return
	 */
	public static Map jsonToMap(String jsonStr) {
		try {
			return mapper.readValue(jsonStr, Map.class);
		} catch (Exception e) {
			logger.error("字符串转Map异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "字符串转Map异常", e);
		}
	}

	/**
	 * 字符串转jsonNode
	 *
	 * @param jsonStr
	 * @return
	 */
	public static JsonNode jsonToNode(String jsonStr) {
		try {
			return mapper.readTree(jsonStr);
		} catch (Exception e) {
			logger.error("字符串转jsonNode异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "字符串转jsonNode异常", e);
		}
	}
	/**
	 * Object《Map》类型对象转特定对象
	 *
	 * @param object
	 * @param beanType
	 * @return
	 */
	public static <T> T objcetToObjectOfType(Object object, Class<T> beanType) {
		try {
			return mapper.readValue(mapper.writeValueAsString(object), beanType);
		} catch (Exception e) {
			logger.error("Object（Map）类型对象转特定对象异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "Object对象转特定对象异常", e);
		}
	}
	/**
	 * Object《List类型》对象转List
	 *
	 * @param object
	 * @param beanType
	 * @return
	 */
	public static <T> List<T> objcetToList(Object object, Class<T> beanType) {
		try {
			return mapper.readValue(
					mapper.writeValueAsString(object),
					mapper.getTypeFactory().constructCollectionType(List.class, beanType)
			);
		} catch (Exception e) {
			logger.error("Object（List）对象转List异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "Object对象转List异常", e);
		}
	}
	/**
	 * Object对象转Map
	 *
	 * @param object
	 * @return
	 */
	public static Map<String,Object> objcetToMap(Object object) {
		try {
			return mapper.convertValue(object, Map.class);
		} catch (Exception e) {
			logger.error("Object对象转Map异常，异常原因：{}", e.getMessage(), e);
			throw new EwsException(ResultCodeEnum.JACKSON_ERROR.getCode(), "Object对象转Map异常", e);
		}
	}

}
