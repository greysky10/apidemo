package demo.unionpay.online;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pfpj.sm.SM2Utils;
import com.pfpj.sm.SM4Utils;
import com.pfpj.sm.Signature;
import demo.common.exceptions.ServerException;
import demo.common.pojo.common.OpenApiMessage;
import demo.common.pojo.common.OpenApiMessageHead;
import demo.common.pojo.common.OpenApiRequest;
import demo.common.pojo.common.OpenApiResponse;
import demo.common.pojo.ecny.BusiData;
import demo.common.pojo.ecny.CheckoutPayRequest;
import demo.common.pojo.ecny.OrderDetail;
import demo.common.util.HttpClientUtils;
import demo.common.util.SMUtil;
import demo.common.util.SerialNoUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 收银台支付
 */
public class OnlineUnionPay {

    private static final Logger logger = LoggerFactory.getLogger(OnlineUnionPay.class);

    // 合作方编号
    public static final String merchantId = "tradeGroupPre001";
    // 应用ID
    public static final String appID = "1095757516090363904001";
    // 合作方私钥
    public static final String privateKey = "009378BDB7262E282910AAE680E0A83EE30EA2AB8D01E41FE880583D1DA512C51E";
    // 合作方公钥
    public static final String publicKey = "04F0F770FDD6E188E31A27A84AC9D6D820D33CF6088A78B305C948A6D98479AC3E71D0AF0356D3C93229C27C1B345B4110DEFBF86885876977573468063EFD8F4F";
    // 服开公钥
    public static final String sopPublicKey = "040485CEFE14C7AF854C66D5279239E88F2E8B881C3EB1B393003D2B9F09E7064447C1A3615875B05A9164F7F637151F115B89E70DFCCD0C25CF83268E21576921";
    // 请求地址
    public static final String url = "http://220.248.252.123:8443/sop-h5/biz_pre/unionpay/${merchantId}.htm?partnerTxSriNo=${partnerTxSriNo}";

    public static void main(String[] args) {
        // 收银台支付
        onlinePay();
        // 交易状态查询
//        queryOrder();
        // 收银台退款
//        orderRefund();

    }

    /**
     * 收银台支付
     */
    public static void onlinePay(){
        try {
            /** 组装请求报文报文头 */
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.orderPay");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            /** 组装请求报文体 */
            CheckoutPayRequest request=new CheckoutPayRequest();
            request.setBusiMainId(msgHead.getPartnerTxSriNo());
            request.setReqTransTime(SerialNoUtil.getDateTime());
            BusiData busiData=new BusiData();
            busiData.setTxnCode("6007");
            busiData.setSourceId("69");
            busiData.setReqTraceId(msgHead.getPartnerTxSriNo());
            busiData.setReqDate(SerialNoUtil.getDateTime());
            busiData.setReqReserved("");
            busiData.setMercDtTm(SerialNoUtil.getDateTime());
            busiData.setVendorNo(msgHead.getPartnerTxSriNo());
            busiData.setMercCode("100510100077920");//商户号
            busiData.setTransAmt("1");
            busiData.setMercUrl("https://gateway.postonline.gx.cn/newspaper/bankCallback");
            busiData.setReSuccUrl("");
            busiData.setOrderUrl("/order/list");
            busiData.setRemark1("");
            busiData.setRemark2("");
            busiData.setValidTime("600");
            busiData.setMercName("gxpost_newspaper");
            busiData.setBizTp("100001");
            busiData.setOrderTitle("测试订单");
            busiData.setOrderCount("1");
            busiData.setTrxDevcInf("127.0.0.1"); //交易设备
            busiData.setReserveParam("外网");

            List<OrderDetail> detailList=new ArrayList<>();
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setSubMercCode("100510100077920");
            orderDetail.setSubMercName("南宁报刊收款");
            orderDetail.setTotalAmt("1");
            orderDetail.setTotalNum("1");
            orderDetail.setMerUnitDetail("商品简称^1^1");
            detailList.add(orderDetail);


            busiData.setOrderDetail(detailList);
            busiData.setTrxDevcInf("||||||");
            busiData.setPyeeAcctIssrId("C1040311005293");
            busiData.setPyeeAcctTp("00");
            busiData.setPyeeNm("名字必填");
            busiData.setPyeeAcctId("6221880000000030");
            busiData.setTerminalIp("127.0.0.1");
            busiData.setMerCustId("123");
            busiData.setPayEnv("01");   //01-手机端 02-PC端
//            busiData.setWchatAppId("wx65c2cab604c7f6ee");
//            busiData.setOpenId("ovds05fjcpCHVsVlG95K3MWZoRe0");
            busiData.setPhonePayEnv("02");  //01 - app 02 - H5页面
            request.setData(busiData);
            String reqstr = JSONObject.toJSONString(request);
            JSONObject requestJson = JSONObject.parseObject(reqstr);
            // 4. 请求报文对象
            OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
            reqMsg.setHead(msgHead);
            reqMsg.setBody(requestJson);

            sendMsg(msgHead, reqMsg);
        } catch (Exception e) {
            logger.error("发起交易异常: ", e);
        }
    }

    /**
     * 收银台退款
     */
    public static void orderRefund(){
        try {
            /** 组装请求报文报文头 */
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.orderRefund");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            /** 组装请求报文体 */
            JSONObject params = new JSONObject();
            params.put("busiMainId", msgHead.getPartnerTxSriNo());
            params.put("reqTransTime", SerialNoUtil.getDateTime());
            JSONObject data = new JSONObject();
            data.put("txnCode", "6008");//交易码
            data.put("sourceId", "69");//渠道
            data.put("reqTraceId", msgHead.getPartnerTxSriNo());//请求方流水号
            data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
            data.put("reqReserved", "");//备注

            data.put("merName", "签约商户");
            data.put("mercDtTm", "20231121150800");
            data.put("vendorNo", msgHead.getPartnerTxSriNo());
            data.put("oriMercDt", "20231121"); //原支付交易商户时间
            data.put("oriVendorNo", "202311211533232976472057"); //原流水号
            data.put("mercCode", "100510100077920");
            data.put("transAmt", "1");
            data.put("orderCount", "1");
            JSONArray jsonArray = new JSONArray();
            JSONObject detail = new JSONObject();
            detail.put("subMercCode","100510100077920");
            detail.put("subMercName","商户名称");
            detail.put("totalAmt","1");
            detail.put("totalNum","1");
            detail.put("merUnitDetail","服开测试^1^1");
            jsonArray.add(detail);

            data.put("orderDetail", jsonArray);
            params.put("data", data.toJSONString());

            // 4. 请求报文对象
            OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
            reqMsg.setHead(msgHead);
            reqMsg.setBody(params);

            sendMsg(msgHead, reqMsg);
        } catch (Exception e) {
            logger.error("发起交易异常: ", e);
        }
    }

    /**
     * 交易状态查询
     */
    public static void queryOrder(){
        try {
            /** 组装请求报文报文头 */
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.queryOrderSta");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            /** 组装请求报文体 */
            JSONObject params = new JSONObject();
            params.put("busiMainId", msgHead.getPartnerTxSriNo());
            params.put("reqTransTime", SerialNoUtil.getDateTime());
            //收单公共字段
            JSONObject data = new JSONObject();
            data.put("txnCode", "6010");//交易码
            data.put("sourceId", "69");//渠道
            data.put("reqTraceId", msgHead.getPartnerTxSriNo());//请求方流水号
            data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
            data.put("reqReserved", "");//备注
            //业务字段
            data.put("merId", "100510100077920");
            data.put("vendorNo", msgHead.getPartnerTxSriNo());
            data.put("oriVendorNo", "202311211533232976472057");    //原流水号
            data.put("mercDtTm", "20231121");
            data.put("transName", "CSQR");
            data.put("transSerial", msgHead.getPartnerTxSriNo());    //交易流水
            data.put("reqDateTime", "20231120145000");
            data.put("queryType", "02");

            params.put("data", data.toJSONString());

            // 4. 请求报文对象
            OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<>();
            reqMsg.setHead(msgHead);
            reqMsg.setBody(params);

            sendMsg(msgHead, reqMsg);
        } catch (Exception e) {
            logger.error("发起交易异常: ", e);
        }
    }

    /**
     * 组装请求头
     * @param method
     * @return
     */
    public static OpenApiMessageHead convertMsgHead(String method){
        OpenApiMessageHead msgHead = new OpenApiMessageHead();
        // 合作放交易流水号
        msgHead.setPartnerTxSriNo(SerialNoUtil.getSerialNo());
        // 接口方法
        msgHead.setMethod(method);
        // 接口版本号 默认为1
        msgHead.setVersion("1");
        // 合作方编号
        msgHead.setMerchantId(merchantId);
        // 接入方式 API/H5
        msgHead.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        msgHead.setAppID(appID);
        // 报文发起时间
        msgHead.setReqTime(SerialNoUtil.getDateTime());
        return msgHead;
    }

    /**
     * 发送请求
     * @param msgHead
     * @param reqMsg
     * @throws IOException
     * @throws ServerException
     */
    public static void sendMsg(OpenApiMessageHead msgHead, OpenApiMessage<JSONObject> reqMsg) throws IOException, ServerException {
        // 5. 加密加签后的请求报文对象
        OpenApiRequest openApiRequest = new OpenApiRequest();
        // 5.1 对称加密请求报文
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        logger.info("原始请求报文: {}", orignReqJsonStr);
        String sm4Key = SMUtil.getSM4Key();
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        openApiRequest.setRequest(encryptRequest);
        // 5.2 加密sm4密钥
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        openApiRequest.setEncryptKey(encryptKey);
        openApiRequest.setAccessToken("");
        // 5.3 计算签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(openApiRequest.getRequest(), ""));
        sb.append(StringUtils.defaultString(openApiRequest.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(openApiRequest.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        openApiRequest.setSignature(signature);

        // 5.4 发起请求
        // 响应
        logger.info("请求报文密文: {}", JSON.toJSONString(openApiRequest));
        // 请求url 替换配置文件中的动态配置
        String address = url.replace("${merchantId}", merchantId)
                .replace("${partnerTxSriNo}", msgHead.getPartnerTxSriNo());
        String respJson = HttpClientUtils.post(address, JSON.toJSONString(openApiRequest));
        // 5.5 解析响应
        OpenApiResponse openApiResponse = JSON.parseObject(respJson, OpenApiResponse.class);
        // 验签
        sb.setLength(0);
        sb.append(StringUtils.defaultString(openApiResponse.getResponse(), ""));
        sb.append(StringUtils.defaultString(openApiResponse.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(openApiResponse.getAccessToken(), ""));
        boolean checked = sm2Utils.verifySign(merchantId, sopPublicKey, sb.toString(), SMUtil.fromString(openApiResponse.getSignature()));
        if (checked) {
            // 解析密钥
            String respSm4Key = sm2Utils.decrypt(privateKey, openApiResponse.getEncryptKey());
            logger.info("------------------------------");
            logger.info("响应报文sm4密钥:{}", respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            logger.info("响应报文:{}", respMessage);
        } else {
            logger.error("响应报文验签失败!");
        }

    }
}
