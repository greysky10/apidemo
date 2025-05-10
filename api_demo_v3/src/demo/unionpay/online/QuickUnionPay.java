package demo.unionpay.online;

import com.alibaba.fastjson.JSON;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 收单单产品快捷
 */
public class QuickUnionPay {

    private static final Logger logger = LoggerFactory.getLogger(QuickUnionPay.class);

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
        // 快捷签约申请
        quickSignApply();
        // 快捷签约验证
//        quickSignVerify();
        // 快捷解约
//        quickTerminate();
        // 快捷支付
//        quickPay();
        // 快捷退款
//        quickRefund();
        // 交易状态查询
//        queryOrder();

    }

    /**
     * 快捷签约申请
     */
    public static void quickSignApply(){
        try {
            /** 组装请求报文报文头 */
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.quickSign");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            /** 组装请求报文体 */
            JSONObject params = new JSONObject();
            params.put("busiMainId", msgHead.getPartnerTxSriNo());
            params.put("reqTransTime", SerialNoUtil.getDateTime());
            //收单公共字段
            JSONObject data = new JSONObject();
            data.put("txnCode", "6001");//交易码
            data.put("sourceId", "69");//渠道
            data.put("reqTraceId", msgHead.getPartnerTxSriNo());//请求方流水号
            data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
            data.put("reqReserved", "");//备注
            //业务字段
            data.put("merName", "签约商户");
            data.put("merId", "100510100018644");
//            data.put("merId", "100310100019046");
//            data.put("merId", "100310100018908");
//            data.put("merId", "100510100018711");
            data.put("netInteTxTpCd", "0201");
            data.put("mediumNo", "6217991000010113962");
            data.put("custNm", "富十八");
            data.put("perCertTpCd", "01");
            data.put("personalCertNo", "420582197701111937");
            data.put("txTime", msgHead.getPartnerTxSriNo());
            data.put("mobileNo", "15532568755");
            data.put("bankCardTpCd", "00");
            data.put("smsValidateCode", "");
            data.put("cardBlginstNo", "C1010211000025");
            data.put("vendorNo", msgHead.getPartnerTxSriNo());
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
     * 快捷签约验证
     */
    public static void quickSignVerify(){
        try {
            /** 组装请求报文报文头 */
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.quickSign");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            /** 组装请求报文体 */
            JSONObject params = new JSONObject();
            params.put("busiMainId", msgHead.getPartnerTxSriNo());
            params.put("reqTransTime", SerialNoUtil.getDateTime());
            //收单公共字段
            JSONObject data = new JSONObject();
            data.put("txnCode", "6001");//交易码
            data.put("sourceId", "69");//渠道
            data.put("reqTraceId", msgHead.getPartnerTxSriNo());//请求方流水号
            data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
            data.put("reqReserved", "");//备注
            //业务字段
            data.put("merName", "签约商户");
            data.put("merId", "100510100018644");
            data.put("netInteTxTpCd", "0202");
            data.put("mediumNo", "6217991000010113962");
            data.put("custNm", "富十八");
            data.put("perCertTpCd", "01");
            data.put("personalCertNo", "420582197701111937");
            data.put("txTime", msgHead.getPartnerTxSriNo());
            data.put("mobileNo", "15532568755");
            data.put("bankCardTpCd", "00");
            data.put("smsValidateCode", "113962");
            data.put("cardBlginstNo", "C1010211000025");
            data.put("vendorNo", "");   //签约申请时的流水
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
     * 快捷解约
     */
    public static void quickTerminate(){
        try {
            /** 组装请求报文报文头 */
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.quickTerminate");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            /** 组装请求报文体 */
            JSONObject params = new JSONObject();
            params.put("busiMainId", msgHead.getPartnerTxSriNo());
            params.put("reqTransTime", SerialNoUtil.getDateTime());
            //收单公共字段
            JSONObject data = new JSONObject();
            data.put("txnCode", "6002");//交易码
            data.put("sourceId", "69");//渠道
            data.put("reqTraceId", msgHead.getPartnerTxSriNo());//请求方流水号
            data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
            data.put("reqReserved", "");//备注
            //业务字段
            data.put("merName", "签约商户");
            data.put("merId", "100510100018644");
            data.put("netInteTxTpCd", "0110");
            data.put("txTime", msgHead.getPartnerTxSriNo());
            data.put("pbankTxAccTpCd", "00");
            data.put("qpayAgrNo", "");  //协议号（签约验证时返回）
            data.put("vendorNo", "");
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
     * 快捷支付
     */
    public static void quickPay(){
        try {
            /** 组装请求报文报文头 */
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.quickPay");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            /** 组装请求报文体 */
            JSONObject params = new JSONObject();
            params.put("busiMainId", msgHead.getPartnerTxSriNo());
            params.put("reqTransTime", SerialNoUtil.getDateTime());
            //收单公共字段
            JSONObject data = new JSONObject();
            data.put("txnCode", "6003");//交易码
            data.put("sourceId", "69");//渠道
            data.put("reqTraceId", msgHead.getPartnerTxSriNo());//请求方流水号
            data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
            data.put("reqReserved", "");//备注
            //业务字段
            data.put("merName", "签约商户");
            data.put("merId", "100510100018644");
            data.put("netInteTxTpCd", "0110");
            data.put("txAmt", "1.00");
            data.put("vendorNo", msgHead.getPartnerTxSriNo());
            data.put("paysdTxTmnlTpCd", "02");
            data.put("paysdTxTerminalNo", "0202"); //付款方交易终端编码
            data.put("txTime", msgHead.getPartnerTxSriNo());
            data.put("paymCardTpCd", "00");
            data.put("payesdCountry", "");
            data.put("payesdRegionNo", "220000");
            data.put("netInteBusiKindCd", "100001");
            data.put("smsValidateCode", "");
            data.put("qpayAgrNo", ""); //快捷协议号(签约验证接口返回)
            data.put("recpayAccNo", "6221880000000030");
            data.put("payeeUserName", "名字必填");
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
     * 快捷退款
     */
    public static void quickRefund(){
        try {
            /** 组装请求报文报文头 */
            OpenApiMessageHead msgHead = convertMsgHead("onlinepay.quickRefund");
            logger.info("原始请求报文头: {}", JSON.toJSONString(msgHead));

            /** 组装请求报文体 */
            JSONObject params = new JSONObject();
            params.put("busiMainId", msgHead.getPartnerTxSriNo());
            params.put("reqTransTime", SerialNoUtil.getDateTime());
            //收单公共字段
            JSONObject data = new JSONObject();
            data.put("txnCode", "6004");//交易码
            data.put("sourceId", "69");//渠道
            data.put("reqTraceId", msgHead.getPartnerTxSriNo());//请求方流水号
            data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
            data.put("reqReserved", "");//备注
            //业务字段
            data.put("oldTxDate", "20231117");
            data.put("merName", "签约商户");
            data.put("merId", "100510100018644");
            data.put("txTime", msgHead.getPartnerTxSriNo());
            data.put("txAmt", "1.00");
            data.put("orgVendorNo", "");    //原流水号
            data.put("vendorNo", msgHead.getPartnerTxSriNo());
            data.put("netInteTxTpCd", "0110");
            data.put("qpayAgrNo", ""); //快捷协议号(签约验证接口返回)
            data.put("oldTxAmt", "2.00");
            data.put("oldNetINteTxTpCd", "0110");
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
            data.put("oldTxDate", "20231117");
            data.put("merName", "签约商户");
            data.put("merId", "100510100018644");
            data.put("txTime", msgHead.getPartnerTxSriNo());
            data.put("oldPbankTxAccTpCd", "00");
            data.put("oldQpayAgrNo", "123456");    //原协议号
            data.put("oldNetInteTxTpCd", "0110");
            data.put("queryType", "01");
            data.put("vendorNo", msgHead.getPartnerTxSriNo());
            data.put("oriVendorNo", "202311201412043489927769");    //原流水号
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
