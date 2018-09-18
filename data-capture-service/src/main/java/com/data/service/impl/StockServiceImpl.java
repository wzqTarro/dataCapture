package com.data.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.Stock;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.constant.PageRecord;
import com.data.constant.TipsEnum;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.dto.CommonDTO;
import com.data.service.IStockService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.ResultUtil;

@Service
public class StockServiceImpl extends CommonServiceImpl implements IStockService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DataCaptureUtil dataCaptureUtil;
	
	@Override
	public ResultUtil getStockByWeb(CommonDTO common) throws IOException {
		List<Stock> stockList = dataCaptureUtil.getDataByWeb(common, WebConstant.STOCK, Stock.class);
		for (int i = 0, size = stockList.size(); i < size; i++) {
			Stock stock = stockList.get(i);
			
			// 单品编号
			String simpleCode = stock.getSimpleCode();
			
			// 系统名称
			String sysName = stock.getSysName();
			
			// 门店编号
			String storeCode = stock.getStockCode();
		
			TemplateStore store = dataCaptureUtil.getStandardStoreMessage(sysName, storeCode);
			
			// 商品条码
			String simpleBarCode = stock.getSimpleBarCode();		
			
			// 地区
			String localName = stock.getLocalName();
			if (CommonUtil.isBlank(simpleBarCode)) {
				simpleBarCode = dataCaptureUtil.getBarCodeMessage(sysName, simpleCode);
			}
			if (CommonUtil.isBlank(simpleBarCode)) {
				stock.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
				continue;
			}
			stock.setSimpleBarCode(simpleBarCode);
			TemplateProduct product = dataCaptureUtil.getStandardProductMessage(localName, sysName, simpleBarCode);
			
			// 门店信息为空
			if (CommonUtil.isBlank(store)) {
				stock.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
			} else {
				// 大区
				stock.setRegion(store.getRegion());

				// 省区
				stock.setProvinceArea(store.getProvinceArea());

				// 门店名称
				stock.setStoreName(store.getStandardStoreName());

				// 归属
				stock.setAscription(store.getAscription());

				// 业绩归属
				stock.setAscriptionSole(store.getAscriptionSole());
			}
			
			// 单品信息为空
			if (CommonUtil.isBlank(product)) {
				stock.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				continue;
			} else {
				// 单品名称
				stock.setSimpleName(product.getStandardName());
				
				// 品牌
				stock.setBrand(product.getBrand());
				
				// 价格
				stock.setTaxCostPrice(product.getIncludeTaxPrice() == null ? 0 : product.getIncludeTaxPrice());
				
				// 系列
				stock.setSeries(product.getSeries());
				
				// 材质
				stock.setMaterial(product.getMaterial());
				
				// 片数
				stock.setPiecesNum(product.getPiecesNum());
				
				// 日夜
				stock.setDayNight(product.getFunc());
				
				// 货号
				stock.setStockNo(product.getStockNo());
				
				// 箱规
				stock.setBoxStandard(product.getBoxStandard());
				
				// 库存编号
				stock.setStockCode(product.getStockCode());
	
				// 库存金额
				stock.setStockPrice(stock.getTaxCostPrice() * stock.getStockNum());
			}
		}
		dataCaptureUtil.insertData(stockList, InsertId.INSERT_BATCH_STOCK);
		PageRecord<Stock> page = dataCaptureUtil.setPageRecord(stockList, common);
		return ResultUtil.success(page);
	}
}
