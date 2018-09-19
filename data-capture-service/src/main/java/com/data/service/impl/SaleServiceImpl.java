package com.data.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.Sale;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.TipsEnum;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.service.ISaleService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.google.common.collect.Maps;

@Service
public class SaleServiceImpl extends CommonServiceImpl implements ISaleService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final int PAGE_NUM = 1;
	
	private static final int PAGE_SIZE = 50000;
	
	@Autowired
	private DataCaptureUtil dataCaptureUtil;

	@Override
	public ResultUtil getSaleByWeb(CommonDTO common, int sysId, Integer page, Integer limit) throws IOException{
		String saleJson = null;
		PageRecord<Sale> pageRecord = null;
		logger.info("------>>>>>>开始抓取销售数据<<<<<<---------");
		
		// 抓取数据
		List<Sale> saleList = dataCaptureUtil.getDataByWeb(common, sysId, WebConstant.SALE, Sale.class);

		
		logger.info("------>>>>>>结束抓取销售数据<<<<<<---------");
		for (int i = 0, size = saleList.size(); i < size; i++) {
			Sale sale = saleList.get(i);
			//sale.setSysId(common.getId());
			
			// 单品编码
			String simpleCode = sale.getSimpleCode();
			
			// 系统
			String sysName = sale.getSysName();
			
			// 门店编码
			String storeCode = sale.getStoreCode();
			
			// 地区
			String localName = sale.getLocalName();
			
			TemplateStore store = dataCaptureUtil.getStandardStoreMessage(sysName, storeCode);
			
			// 单品条码
			String simpleBarCode = sale.getSimpleBarCode();
			if (CommonUtil.isBlank(simpleBarCode)) {
				simpleBarCode = dataCaptureUtil.getBarCodeMessage(sysName, simpleCode);
			}
			if (CommonUtil.isBlank(simpleBarCode)) {
				sale.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
				continue;
			}
			sale.setSimpleBarCode(simpleBarCode);
			TemplateProduct product = dataCaptureUtil.getStandardProductMessage(localName, sysName, simpleBarCode);
			
			// 门店信息为空
			if (CommonUtil.isBlank(store)) {
				sale.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
			} else {
				// 大区
				sale.setRegion(store.getRegion());
				
				// 省区
				sale.setProvinceArea(store.getProvinceArea());
				
				// 门店名称
				sale.setStoreName(store.getStandardStoreName());
				
				// 归属
				sale.setAscription(store.getAscription());
				
				// 业绩归属
				sale.setAscriptionSole(store.getAscriptionSole());
				
				// 门店负责人
				sale.setStoreManager(store.getStoreManager());
			}
			
			// 单品信息为空
			if (CommonUtil.isBlank(product)) {
				sale.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				continue;
			} else {
				// 单品名称
				sale.setSimpleName(product.getStandardName());
				
				// 品牌
				sale.setBrand(product.getBrand());
				
				// 销售价格
				sale.setSellPrice(product.getSellPrice());
				
				// 系列
				sale.setSeries(product.getSeries());
				
				// 材质
				sale.setMaterial(product.getMaterial());
				
				// 片数
				sale.setPiecesNum(product.getPiecesNum());
				
				// 日夜
				sale.setDayNight(product.getFunc());
				
				// 货号
				sale.setStockNo(product.getStockNo());
				
				// 箱规
				sale.setBoxStandard(product.getBoxStandard());
				
				// 库存编号
				sale.setStockCode(product.getStockCode());
			}
			
			
			
		
		}
		
		// 数据插入数据库
		dataCaptureUtil.insertData(saleList, InsertId.INSERT_BATCH_SALE);
		pageRecord = dataCaptureUtil.setPageRecord(saleList, page, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil getSaleByParam(CommonDTO common, Sale sale, Integer page, Integer limit) throws Exception {
		if (null == common) {
			common = new CommonDTO();
		}
		Map<String, Object> map = Maps.newHashMap();
		logger.info("--------->>>>>>>>common:" + FastJsonUtil.objectToString(common) + "<<<<<<<-----------");		
		if (StringUtils.isNoneBlank(common.getStartDate()) && StringUtils.isNoneBlank(common.getEndDate())) {
			map.put("startDate", common.getStartDate());
			map.put("endDate", common.getEndDate());
		} else {
			// 默认查询当天数据
			map.put("startDate", DateUtil.getDate());
			map.put("endDate", DateUtil.getDate());
		}
		logger.info("--------->>>>>>>>sale:" + FastJsonUtil.objectToString(sale) + "<<<<<<<<----------");
		if (null != sale) {
			
			// 门店名称
			if (StringUtils.isNoneBlank(sale.getStoreName())) {
				map.put("storeName", sale.getStoreName());
			}
			 
			// 区域
			if (StringUtils.isNoneBlank(sale.getRegion())) {
				map.put("region", sale.getRegion());
			}
			
			// 系列
			if (StringUtils.isNoneBlank(sale.getSeries())) {
				map.put("series", sale.getSeries());
			}
			
			// 单品名称
			if (StringUtils.isNoneBlank(sale.getSimpleName())) {
				map.put("simpleName", sale.getSimpleName());
			}
		}
		PageRecord<Sale> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_SALE_BY_PARAM, 
				QueryId.QUERY_SALE_BY_PARAM, map, page, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil querySaleList(Sale sale) {
		//防止一次性查询数据过多 分批查询
		Map<String, Object> params = new HashMap<>();
		params.put("pageNum", PAGE_NUM);
		params.put("pageSize", PAGE_SIZE);
		//此处为测试数据样式
		params.put("createTime", sale.getCreateTime());
		
		int count = queryCountByObject(QueryId.QUERY_COUNT_SALE_LIST, params);
		if(count == 0) {
			logger.info("--->>>销售查询列表数量为0<<<---");
			return ResultUtil.success(Collections.EMPTY_LIST);
		}
		
		/**
		 * TODO
		 * sql语句还没写 此处只是为了写出模板
		 */
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, params);
		int totalCount = count;
		//此处也是为了具体行为检测 如果数据量太大不能将数据打印 否则日志过大 
		logger.info("--->>>第一页销售查询结合列表为: {}, 总数为: {}<<<---", FastJsonUtil.objectToString(saleList), totalCount);
		List<Sale> resultList = new ArrayList<>();
		resultList.addAll(saleList);
		if(totalCount > PAGE_SIZE) {
			int pages = totalCount % PAGE_SIZE == 0 ? totalCount / PAGE_SIZE : totalCount / PAGE_SIZE + 1;
			for(int i = 2; i <= pages; i++) {
				saleList.clear();
				params.put("pageNum", i);
				saleList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, params);
				resultList.addAll(saleList);
			}
		}
		return ResultUtil.success(resultList);
	}
	
	public static void main(String[] args) {
		String json = null;
		try {
			json = FileUtils.readFileToString(new File("E:\\baiya\\sale\\sale.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Sale> list = (List<Sale>) FastJsonUtil.jsonToList(json, Sale.class);
		list.subList((CommonValue.PAGE - 1)*CommonValue.SIZE, list.size());
		System.err.println(FastJsonUtil.objectToString(list));
	}
}
