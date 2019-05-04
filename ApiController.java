package com.zhonghui_manage.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zhonghui_common.annotation.NoAuth;
import com.zhonghui_manage.service.ReturnGoodsService;
import com.zhonghui_manage.service.WxRefundService;
import com.zhonghui_manage.utils.XmlUtils;


@RestController
@RequestMapping("/api")
public class ApiController {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Value("${wechat.mchID}")
	private String mchID;
	
	@Autowired
	private ReturnGoodsService returnGoodsService;
	
	@Autowired
	private WxRefundService wxRefundService;

	
	
	@PostMapping("/wx_refund")
	@NoAuth
	public void refundNotify(HttpServletRequest request,HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		try {
			String xml = IOUtils.toString(request.getInputStream(), "UTF-8");
			logger.info("接受微信退款回调:"+xml);
			if (StringUtils.isEmpty(xml)) {
				return;
			}
			Map<String, String> resultMap = XmlUtils.toObject(xml, new TypeReference<Map<String, String>>() {});
			String successXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
			boolean result_code = StringUtils.equals("SUCCESS", resultMap.get("return_code"));
			boolean mch_id = StringUtils.equals(mchID, resultMap.get("mch_id"));
			String req_info = resultMap.get("req_info");
			Map<String, String> decryption = XmlUtils.toObject(wxRefundService.decryptData(req_info), new TypeReference<Map<String, String>>() {});
			String out_refund_no = decryption.get("out_refund_no");
			
			logger.info("微信线上退款回调,调用updatePayStatus方法.out_refund_no={},result_code={},mch_id={}",out_refund_no,result_code,mch_id);
			if(result_code && mch_id) {
				returnGoodsService.updatePayStatus(true, out_refund_no);
			}else {
				returnGoodsService.updatePayStatus(false, out_refund_no);
			}
			out.print(successXml);
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	

}
