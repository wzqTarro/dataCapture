package com.data.service;

import java.util.List;

import com.data.bean.Order;
import com.data.bean.Reject;
import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.TemplateSupply;
import com.data.utils.ResultUtil;

public interface IDataService {
	/**
	 * 补全数据
	 * @return
	 */
	ResultUtil completionData() throws Exception;
	/**
	 * 匹配订单数据
	 * @param supplyList
	 * @param storeList
	 * @param productList
	 * @param orderList
	 * @throws Exception
	 */
	void mateOrderData(List<TemplateSupply> supplyList, List<TemplateStore> storeList, List<TemplateProduct> productList, List<Order> orderList) throws Exception;
	/**
	 * 匹配销售数据
	 * @param supplyList
	 * @param storeList
	 * @param productList
	 * @param saleList
	 * @throws Exception
	 */
	void mateSaleData(List<TemplateSupply> supplyList, List<TemplateStore> storeList, List<TemplateProduct> productList, List<Sale> saleList) throws Exception;
	/**
	 * 匹配退单数据
	 * @param supplyList
	 * @param storeList
	 * @param productList
	 * @param rejectList
	 * @throws Exception
	 */
	void mateRejectData(List<TemplateSupply> supplyList, List<TemplateStore> storeList, List<TemplateProduct> productList, List<Reject> rejectList) throws Exception;
	/**
	 * 匹配库存数据
	 * @param supplyList
	 * @param storeList
	 * @param productList
	 * @param stockList
	 * @throws Exception
	 */
	void mateStockData(List<TemplateSupply> supplyList, List<TemplateStore> storeList, List<TemplateProduct> productList, List<Stock> stockList) throws Exception;
}
