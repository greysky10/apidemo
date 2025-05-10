package demo.unionpay.offline;

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
import demo.common.util.HttpClientUtils;
import demo.common.util.QRCodeUtil;
import demo.common.util.SMUtil;
import demo.common.util.SerialNoUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 服务开放平台线下收单Demo
 */
public class Demo {

    static String partnerTxSriNo = SerialNoUtil.getSerialNo();
    //*************************************** 定版 *************************************************
//    static String url = "http://220.248.252.123:8443/sop-h5/biz/unionpay/tradeGroup001.htm?partnerTxSriNo=" + partnerTxSriNo;
//    static String merchantId = "tradeGroup001";
//    static String appID = "1095755893794127872001";
//    static String sopPublicKey = "04680B9F7D73BF3300C75BB0190D9799038B88511BAD90853BF4F83EC229941006FA742CDC44551A1C01572017FDE940C5D495372D3C27A70F9D9F1F9E9531C9D4";
//    static String privateKey = "00CC541434413C70A7043B6F914325331800646087CD0326771786114D28F66EB4";
//    static String publicKey = "04152126DBF4ACD0326866F100C2AEAC8353E34A5832BE86EEEF4EEDD89B01EE873D592F28B95E5D08ECBB24F6F80876DCF541C15A9B86E2E5B7332509C0BCD31E";
    //*************************************** 定版 *************************************************

    //*************************************** 预演 *************************************************
    static String url = "http://220.248.252.123:8443/sop-h5/biz_pre/unionpay/tradeGroupPre001.htm?partnerTxSriNo=" + partnerTxSriNo;
    static String merchantId = "tradeGroupPre001";
    static String appID = "1095757516090363904001";
    static String sopPublicKey = "040485CEFE14C7AF854C66D5279239E88F2E8B881C3EB1B393003D2B9F09E7064447C1A3615875B05A9164F7F637151F115B89E70DFCCD0C25CF83268E21576921";
    static String privateKey = "009378BDB7262E282910AAE680E0A83EE30EA2AB8D01E41FE880583D1DA512C51E";
    static String publicKey = "04F0F770FDD6E188E31A27A84AC9D6D820D33CF6088A78B305C948A6D98479AC3E71D0AF0356D3C93229C27C1B345B4110DEFBF86885876977573468063EFD8F4F";
    //*************************************** 预演 *************************************************

    static String platformID= "300370100000013";
    //系统代码
    static String reqSysId = "JXBHMPAY001";
    //证书序列号
    static String certNum = "1040990809";
    //收单商户号
    static String mchtNo = "100370100000697";

    public static void main(String[] args) throws IOException, ServerException {
        //终端注册接口
//        externalPos();

        //被扫支付
//        orderPay();

        //主扫支付（申请动态二维码）
        reqQr();

        //主扫支付(一码付)
//        reqQr1();

        //小程序支付
//        uniPay();

        //退款
        //orderRefund();

        //查询接口
//        orderQuery();

        //退款查询
//        refundQuery();
    }

    private static void refundQuery() throws IOException, ServerException {
        //服开请求格式
        OpenApiRequest request = new OpenApiRequest();
        //请求头
        OpenApiMessageHead head = new OpenApiMessageHead();
        //合作方流水号
        head.setPartnerTxSriNo(partnerTxSriNo);
        // 接口方法 路由
        head.setMethod("unionpay.refundQuery");
        // 接口版本号 默认为1
        head.setVersion("1");
        // 合作方编号
        head.setMerchantId(merchantId);
        // 接入方式 API/H5
        head.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        head.setAppID(appID);
        // 报文发起时间
        head.setReqTime(SerialNoUtil.getDateTime());

        JSONObject body = new JSONObject();
        //具体接口报文
        JSONObject data = new JSONObject();
        //收单公共字段
        data.put("version", "v1.2.8");//版本号
        data.put("txnCode", "8009");//交易码
        data.put("channelID", "08");//渠道
        data.put("platformID", platformID);//平台编号,收单注册
        data.put("reqTraceID", partnerTxSriNo);//请求方流水号
        data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
        data.put("reqReserved", "");//备注
        data.put("certNum", certNum);// 收单CFCA证书序列号
        data.put("reqSysId", reqSysId);//请求方系统代码

        //业务字段
        data.put("mchtNo", mchtNo);
        data.put("txnAMT", "0.01");
        data.put("orgReqTraceID", "202309011000471312865940");

        data.put("termID", "te961546");
        JSONObject termInfo = new JSONObject();
        termInfo.put("deviceId", "te961546");
        termInfo.put("deviceType", "11");
        data.put("termInfo", termInfo.toJSONString());

        body.put("data", data);
        body.put("busiMainId", partnerTxSriNo);
        body.put("reqTransTime", SerialNoUtil.getDateTime());

        OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
        reqMsg.setHead(head);
        reqMsg.setBody(body);
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        System.out.println("原始请求报文: " +  orignReqJsonStr);

        //1.生成 sm4Key
        String sm4Key = SMUtil.getSM4Key();
        System.out.println("SM4Key:" + sm4Key);

        //2.使用 sm4Key 加密请求报文
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        request.setRequest(encryptRequest);
        //3.加密 sm4Key
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        request.setEncryptKey(encryptKey);
        request.setAccessToken("");
        //4.签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(request.getRequest(), ""));
        sb.append(StringUtils.defaultString(request.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(request.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        request.setSignature(signature);
        System.out.println("请求报文: " + JSON.toJSONString(request));

        String respJson = HttpClientUtils.post(url, JSON.toJSONString(request));
        // 5 解析响应
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
            System.out.println("------------------------------");
            System.out.println("响应报文sm4密钥:" + respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文:" + respMessage);
        } else {
            System.out.println("响应报文验签失败!");
        }
    }

    private static void orderQuery() throws IOException, ServerException {
        //服开请求格式
        OpenApiRequest request = new OpenApiRequest();

        //请求头
        OpenApiMessageHead head = new OpenApiMessageHead();

        //合作方流水号
        head.setPartnerTxSriNo(partnerTxSriNo);
        // 接口方法 路由
        head.setMethod("unionpay.orderQuery");
        // 接口版本号 默认为1
        head.setVersion("1");
        // 合作方编号
        head.setMerchantId(merchantId);
        // 接入方式 API/H5
        head.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        head.setAppID(appID);
        // 报文发起时间
        head.setReqTime(SerialNoUtil.getDateTime());

        JSONObject body = new JSONObject();
        //具体接口报文
        JSONObject data = new JSONObject();
        //收单公共字段
        data.put("version", "v1.2.8");//版本号
        data.put("txnCode", "8006");//交易码
        data.put("channelID", "08");//渠道
        data.put("platformID", platformID);//平台编号,收单注册
        data.put("reqTraceID", partnerTxSriNo);//请求方流水号
        data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
        data.put("reqReserved", "");//备注
        data.put("certNum", certNum);// 收单CFCA证书序列号
        data.put("reqSysId", reqSysId);//请求方系统代码

        //业务字段
        data.put("mchtNo", mchtNo);
        data.put("queryFlag", "1");
        data.put("orgReqTraceID", "202310171034343093657043");

        body.put("data", data);
        body.put("busiMainId", partnerTxSriNo);
        body.put("reqTransTime", SerialNoUtil.getDateTime());

        OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
        reqMsg.setHead(head);
        reqMsg.setBody(body);
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        System.out.println("原始请求报文: " +  orignReqJsonStr);

        //1.生成 sm4Key
        String sm4Key = SMUtil.getSM4Key();
        System.out.println("SM4Key:" + sm4Key);

        //2.使用 sm4Key 加密请求报文
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        request.setRequest(encryptRequest);
        //3.加密 sm4Key
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        request.setEncryptKey(encryptKey);
        request.setAccessToken("");
        //4.签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(request.getRequest(), ""));
        sb.append(StringUtils.defaultString(request.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(request.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        request.setSignature(signature);
        System.out.println("请求报文: " + JSON.toJSONString(request));

        String respJson = HttpClientUtils.post(url, JSON.toJSONString(request));
        // 5 解析响应
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
            System.out.println("------------------------------");
            System.out.println("响应报文sm4密钥:" + respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文:" + respMessage);

            JSONObject j = JSONObject.parseObject(respMessage);
            String qrCode = j.getJSONObject("body").getString("qrCode");
            if(qrCode != null && qrCode != "") {
                try {
                    QRCodeUtil.printQr(qrCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("打印失败");
                }
            }

        } else {
            System.out.println("响应报文验签失败!");
        }

    }

    private static void orderRefund() throws IOException, ServerException {
        //服开请求格式
        OpenApiRequest request = new OpenApiRequest();
        //请求头
        OpenApiMessageHead head = new OpenApiMessageHead();
        //合作方流水号
        head.setPartnerTxSriNo(partnerTxSriNo);
        // 接口方法 路由
        head.setMethod("unionpay.orderRefund");
        // 接口版本号 默认为1
        head.setVersion("1");
        // 合作方编号
        head.setMerchantId(merchantId);
        // 接入方式 API/H5
        head.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        head.setAppID(appID);
        // 报文发起时间
        head.setReqTime(SerialNoUtil.getDateTime());

        JSONObject body = new JSONObject();
        //具体接口报文
        JSONObject data = new JSONObject();
        //收单公共字段
        data.put("version", "v1.2.8");//版本号
        data.put("txnCode", "8005");//交易码
        data.put("channelID", "08");//渠道
        data.put("platformID", platformID);//平台编号,收单注册
        data.put("reqTraceID", partnerTxSriNo);//请求方流水号
        data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
        data.put("reqReserved", "");//备注
        data.put("certNum", certNum);// 收单CFCA证书序列号
        data.put("reqSysId", reqSysId);//请求方系统代码

        //业务字段
        data.put("mchtNo", mchtNo);
        data.put("txnAMT", "0.01");
        data.put("whetherNotify", "01");

        data.put("orgReqTraceID", "202309010959460566359387");


        data.put("termID", "te961546");
        JSONObject termInfo = new JSONObject();
        termInfo.put("deviceId", "te961546");
        termInfo.put("deviceType", "11");
        data.put("termInfo", termInfo.toJSONString());

        body.put("data", data);
        body.put("busiMainId", partnerTxSriNo);
        body.put("reqTransTime", SerialNoUtil.getDateTime());

        OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
        reqMsg.setHead(head);
        reqMsg.setBody(body);
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        System.out.println("原始请求报文: " +  orignReqJsonStr);

        //1.生成 sm4Key
        String sm4Key = SMUtil.getSM4Key();
        System.out.println("SM4Key:" + sm4Key);

        //2.使用 sm4Key 加密请求报文
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        request.setRequest(encryptRequest);
        //3.加密 sm4Key
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        request.setEncryptKey(encryptKey);
        request.setAccessToken("");
        //4.签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(request.getRequest(), ""));
        sb.append(StringUtils.defaultString(request.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(request.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        request.setSignature(signature);
        System.out.println("请求报文: " + JSON.toJSONString(request));

        String respJson = HttpClientUtils.post(url, JSON.toJSONString(request));
        // 5 解析响应
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
            System.out.println("------------------------------");
            System.out.println("响应报文sm4密钥:" + respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文:" + respMessage);
        } else {
            System.out.println("响应报文验签失败!");
        }
    }

    private static void reqQr() throws IOException, ServerException {
        //服开请求格式
        OpenApiRequest request = new OpenApiRequest();

        //请求头
        OpenApiMessageHead head = new OpenApiMessageHead();

        //合作方流水号
        head.setPartnerTxSriNo(partnerTxSriNo);
        // 接口方法 路由
        head.setMethod("unionpay.reqQr");
        // 接口版本号 默认为1
        head.setVersion("1");
        // 合作方编号
        head.setMerchantId(merchantId);
        // 接入方式 API/H5
        head.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        head.setAppID(appID);
        // 报文发起时间
        head.setReqTime(SerialNoUtil.getDateTime());

        JSONObject body = new JSONObject();
        //具体接口报文
        JSONObject data = new JSONObject();
        //收单公共字段
        data.put("version", "v1.2.8");//版本号
        data.put("txnCode", "8002");//交易码
        data.put("channelID", "08");//渠道
        data.put("platformID", platformID);//平台编号,收单注册
        data.put("reqTraceID", partnerTxSriNo);//请求方流水号
        data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
        data.put("reqReserved", "");//备注
        data.put("certNum", certNum);// 收单CFCA证书序列号
        data.put("reqSysId", reqSysId);//请求方系统代码

        //业务字段
        data.put("mchtNo", mchtNo);
        data.put("txnAMT", "0.01");
        data.put("whetherNotify", "00");
        data.put("backUrl", "http://127.0.0.1");

        JSONObject orderData = new JSONObject();
        orderData.put("orderFlag", "0");
        orderData.put("orderTitle", "订单");
        orderData.put("orderAMT", "0.01");
        data.put("orderData", orderData.toJSONString());

        JSONObject termInfo = new JSONObject();
        termInfo.put("deviceId", "te961546");
        termInfo.put("deviceType", "11");
        data.put("termID", "te961546");
        data.put("termInfo", termInfo.toJSONString());

        body.put("data", data);
        body.put("busiMainId", partnerTxSriNo);
        body.put("reqTransTime", SerialNoUtil.getDateTime());

        OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
        reqMsg.setHead(head);
        reqMsg.setBody(body);
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        System.out.println("原始请求报文: " +  orignReqJsonStr);

        //1.生成 sm4Key
        String sm4Key = SMUtil.getSM4Key();
        System.out.println("SM4Key:" + sm4Key);

        //2.使用 sm4Key 加密请求报文
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        request.setRequest(encryptRequest);
        //3.加密 sm4Key
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        request.setEncryptKey(encryptKey);
        request.setAccessToken("");
        //4.签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(request.getRequest(), ""));
        sb.append(StringUtils.defaultString(request.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(request.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        request.setSignature(signature);
        System.out.println("请求报文: " + JSON.toJSONString(request));

        String respJson = HttpClientUtils.post(url, JSON.toJSONString(request));
        // 5 解析响应
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
            System.out.println("------------------------------");
            System.out.println("响应报文sm4密钥:" + respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文:" + respMessage);

            JSONObject j = JSONObject.parseObject(respMessage);
            String qrCode = j.getJSONObject("body").getString("qrCode");
            if(qrCode != null && qrCode != "") {
                try {
                    QRCodeUtil.printQr(qrCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("打印失败");
                }
            }

        } else {
            System.out.println("响应报文验签失败!");
        }

    }

    private static void reqQr1() throws IOException, ServerException {
        //服开请求格式
        OpenApiRequest request = new OpenApiRequest();

        //请求头
        OpenApiMessageHead head = new OpenApiMessageHead();

        //合作方流水号
        head.setPartnerTxSriNo(partnerTxSriNo);
        // 接口方法 路由
        head.setMethod("unionpay.qrPayUrl");
        // 接口版本号 默认为1
        head.setVersion("1");
        // 合作方编号
        head.setMerchantId(merchantId);
        // 接入方式 API/H5
        head.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        head.setAppID(appID);
        // 报文发起时间
        head.setReqTime(SerialNoUtil.getDateTime());

        JSONObject body = new JSONObject();
        //具体接口报文
        JSONObject data = new JSONObject();
        //收单公共字段
        data.put("version", "v1.2.8");//版本号
        data.put("txnCode", "8018");//交易码
        data.put("channelID", "08");//渠道
        data.put("platformID", platformID);//平台编号,收单注册
        data.put("reqTraceID", partnerTxSriNo);//请求方流水号
        data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
        data.put("reqReserved", "");//备注
        data.put("certNum", certNum);// 收单CFCA证书序列号
        data.put("reqSysId", reqSysId);//请求方系统代码

        //业务字段
        data.put("mchtNo", mchtNo);
        data.put("txnAMT", "0.01");
        data.put("whetherNotify", "00");
        data.put("backUrl", "http://127.0.0.1");

        JSONObject orderData = new JSONObject();
        orderData.put("orderFlag", "0");
        orderData.put("orderTitle", "订单");
        orderData.put("orderAMT", "0.01");
        data.put("orderData", orderData.toJSONString());

        JSONObject termInfo = new JSONObject();
        termInfo.put("deviceId", "te961546");
        termInfo.put("deviceType", "11");
        data.put("termID", "te961546");
        data.put("termInfo", termInfo.toJSONString());

        body.put("data", data);
        body.put("busiMainId", partnerTxSriNo);
        body.put("reqTransTime", SerialNoUtil.getDateTime());

        OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
        reqMsg.setHead(head);
        reqMsg.setBody(body);
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        System.out.println("原始请求报文: " +  orignReqJsonStr);

        //1.生成 sm4Key
        String sm4Key = SMUtil.getSM4Key();
        System.out.println("SM4Key:" + sm4Key);

        //2.使用 sm4Key 加密请求报文
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        request.setRequest(encryptRequest);
        //3.加密 sm4Key
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        request.setEncryptKey(encryptKey);
        request.setAccessToken("");
        //4.签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(request.getRequest(), ""));
        sb.append(StringUtils.defaultString(request.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(request.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        request.setSignature(signature);
        System.out.println("请求报文: " + JSON.toJSONString(request));

        String respJson = HttpClientUtils.post(url, JSON.toJSONString(request));
        // 5 解析响应
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
            System.out.println("------------------------------");
            System.out.println("响应报文sm4密钥:" + respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文:" + respMessage);

            JSONObject j = JSONObject.parseObject(respMessage);
            String qrCode = j.getJSONObject("body").getString("qrCode");
            if(qrCode != null && qrCode != "") {
                try {
                    QRCodeUtil.printQr(qrCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("打印失败");
                }
            }

        } else {
            System.out.println("响应报文验签失败!");
        }

    }

    //小程序支付特殊数据可以下单成功
    private static void uniPay() throws IOException, ServerException {
        //服开请求格式
        OpenApiRequest request = new OpenApiRequest();

        //请求头
        OpenApiMessageHead head = new OpenApiMessageHead();

        //合作方流水号
        head.setPartnerTxSriNo(partnerTxSriNo);
        // 接口方法 路由
        head.setMethod("unionpay.uniPay");
        // 接口版本号 默认为1
        head.setVersion("1");
        // 合作方编号
        head.setMerchantId(merchantId);
        // 接入方式 API/H5
        head.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        head.setAppID(appID);
        // 报文发起时间
        head.setReqTime(SerialNoUtil.getDateTime());

        JSONObject body = new JSONObject();
        //具体接口报文
        JSONObject data = new JSONObject();
        //收单公共字段
        data.put("version", "v1.2.8");//版本号
        data.put("txnCode", "8007");//交易码
        data.put("channelID", "08");//渠道
        data.put("platformID", "300440100003632");//平台编号,收单注册
        data.put("reqTraceID", partnerTxSriNo);//请求方流水号
        data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
        data.put("reqReserved", "");//备注
        data.put("certNum", "1056360014");// 收单CFCA证书序列号
        data.put("reqSysId", "YCHGCS00297");//请求方系统代码

        //业务字段
        data.put("mchtNo", "100440100073025");//商户号
        data.put("tradeTypeWX", "JSAPI");//交易类型
        data.put("subAppIDWX", "wx422bf31b9ffbc08c");//子公众号ID
        data.put("bodyWX", "腾讯形象店-  image-QQ公仔");//商品描述
        data.put("txnAMT", "0.01");//交易金额
        data.put("currencyCode", "156");//交易币种
        data.put("subOpenIDWX", "ovVc-0x9zdNTjlbOAQ2dGXhOz0gI");//用户子标识不能为空
        data.put("whetherNotify", "00");//是否需要通知 不需要
        data.put("backUrl", "http://127.0.0.1");
        data.put("isCredit", "0");
        data.put("identity", new JSONObject());//实名支付

        //订单信息
        JSONObject orderData = new JSONObject();
        orderData.put("orderFlag", "0");
        orderData.put("orderTitle", "订单");
        orderData.put("orderAmt", "0.01");
        data.put("orderData", orderData.toJSONString());

        //终端信息
        JSONObject termInfo = new JSONObject();
        termInfo.put("deviceId", "13567890");
        termInfo.put("deviceType", "11");
        data.put("termID", "13567890");
        data.put("termInfo", termInfo.toJSONString());


        body.put("data", data);
        body.put("busiMainId", partnerTxSriNo);
        body.put("reqTransTime", SerialNoUtil.getDateTime());

        OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
        reqMsg.setHead(head);
        reqMsg.setBody(body);
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        System.out.println("原始请求报文: " +  orignReqJsonStr);

        //1.生成 sm4Key
        String sm4Key = SMUtil.getSM4Key();
        System.out.println("SM4Key:" + sm4Key);

        //2.使用 sm4Key 加密请求报文
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        request.setRequest(encryptRequest);
        //3.加密 sm4Key
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        request.setEncryptKey(encryptKey);
        request.setAccessToken("");
        //4.签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(request.getRequest(), ""));
        sb.append(StringUtils.defaultString(request.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(request.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        request.setSignature(signature);
        System.out.println("请求报文: " + JSON.toJSONString(request));

        String respJson = HttpClientUtils.post(url, JSON.toJSONString(request));
        // 5 解析响应
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
            System.out.println("------------------------------");
            System.out.println("响应报文sm4密钥:" + respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文:" + respMessage);
        } else {
            System.out.println("响应报文验签失败!");
        }


    }

    private static void orderPay() throws IOException, ServerException {
        //服开请求格式
        OpenApiRequest request = new OpenApiRequest();

        //请求头
        OpenApiMessageHead head = new OpenApiMessageHead();

        //合作方流水号
        head.setPartnerTxSriNo(partnerTxSriNo);
        // 接口方法 路由
        head.setMethod("unionpay.orderPay");
        // 接口版本号 默认为1
        head.setVersion("1");
        // 合作方编号
        head.setMerchantId(merchantId);
        // 接入方式 API/H5
        head.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        head.setAppID(appID);
        // 报文发起时间
        head.setReqTime(SerialNoUtil.getDateTime());

        JSONObject body = new JSONObject();
        //具体接口报文
        JSONObject data = new JSONObject();
        //收单公共字段
        data.put("version", "v1.2.8");//版本号
        data.put("txnCode", "8003");//交易码
        data.put("channelID", "08");//渠道
        data.put("platformID", platformID);//平台编号,收单注册
        data.put("reqTraceID", partnerTxSriNo);//请求方流水号
        data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
        data.put("reqReserved", "");//备注
        data.put("certNum", certNum);// 收单CFCA证书序列号
        data.put("reqSysId", reqSysId);//请求方系统代码

        //业务字段
        data.put("mchtNo", mchtNo);
        data.put("txnAMT", "0.01");
        data.put("currencyCode", "156");
        data.put("qrCode", "130284053204601734");
        data.put("whetherNotify", "01");

        JSONObject orderData = new JSONObject();
        orderData.put("orderFlag", "0");
        orderData.put("orderTitle", "订单");
        orderData.put("orderAMT", "0.01");
        data.put("orderData", orderData);

        JSONObject termInfo = new JSONObject();
        termInfo.put("deviceId", "te961546");
        termInfo.put("deviceType", "11");
        data.put("termID", "te961546");
        data.put("termInfo", termInfo.toJSONString());

        body.put("data", data);
        body.put("busiMainId", partnerTxSriNo);
        body.put("reqTransTime", SerialNoUtil.getDateTime());

        OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
        reqMsg.setHead(head);
        reqMsg.setBody(body);
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        System.out.println("原始请求报文: " +  orignReqJsonStr);

        //1.生成 sm4Key
        String sm4Key = SMUtil.getSM4Key();
        System.out.println("SM4Key:" + sm4Key);

        //2.使用 sm4Key 加密请求报文
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        request.setRequest(encryptRequest);
        //3.加密 sm4Key
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        request.setEncryptKey(encryptKey);
        request.setAccessToken("");
        //4.签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(request.getRequest(), ""));
        sb.append(StringUtils.defaultString(request.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(request.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        request.setSignature(signature);
        System.out.println("请求报文: " + JSON.toJSONString(request));

        String respJson = HttpClientUtils.post(url, JSON.toJSONString(request));
        // 5 解析响应
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
            System.out.println("------------------------------");
            System.out.println("响应报文sm4密钥:" + respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文:" + respMessage);
        } else {
            System.out.println("响应报文验签失败!");
        }


    }

    private static void externalPos() throws IOException, ServerException {
        //服开请求格式
        OpenApiRequest request = new OpenApiRequest();

        //请求头
        OpenApiMessageHead head = new OpenApiMessageHead();

        //合作方流水号
        head.setPartnerTxSriNo(partnerTxSriNo);
        // 接口方法 路由
        head.setMethod("unionpay.externalPos");
        // 接口版本号 默认为1
        head.setVersion("1");
        // 合作方编号
        head.setMerchantId(merchantId);
        // 接入方式 API/H5
        head.setAccessType("API");
        // 服开门户网站-用户中心-我的应用-APP_ID
        head.setAppID(appID);
        // 报文发起时间
        head.setReqTime(SerialNoUtil.getDateTime());

        JSONObject body = new JSONObject();
        //具体接口报文
        JSONObject data = new JSONObject();
        //收单公共字段
        data.put("version", "v1.2.8");//版本号
        data.put("txnCode", "8019");//交易码
        data.put("channelID", "08");//渠道
        data.put("platformID", platformID);//平台编号,收单注册
        data.put("reqTraceID", partnerTxSriNo);//请求方流水号
        data.put("reqDate", SerialNoUtil.getDateTime());//交易时间
        data.put("reqReserved", "");//备注
        data.put("certNum", certNum);// 收单CFCA证书序列号
        data.put("reqSysId", reqSysId);//请求方系统代码

        //业务字段
        data.put("mchtId", mchtNo);//收单商户号
        data.put("externalType", "02");//交易类型
        data.put("devicedId", "te" + SerialNoUtil.getRandomNum(6));//终端设备号
        data.put("deviceType", "11");//终端类型
        data.put("deviceState", "00");//终端状态
        data.put("operationId", "00");//操作类型
        data.put("deviceAddress", "山西省-长治市-襄垣县-侯堡镇");//终端地址

        body.put("data", data);
        body.put("busiMainId", partnerTxSriNo);
        body.put("reqTransTime", SerialNoUtil.getDateTime());

        OpenApiMessage<JSONObject> reqMsg = new OpenApiMessage<JSONObject>();
        reqMsg.setHead(head);
        reqMsg.setBody(body);
        String orignReqJsonStr = JSON.toJSONString(reqMsg);
        System.out.println("原始请求报文: " +  orignReqJsonStr);

        //1.生成 sm4Key
        String sm4Key = SMUtil.getSM4Key();
        System.out.println("SM4Key:" + sm4Key);

        //2.使用 sm4Key 加密请求报文
        String encryptRequest = SM4Utils.encrypt(orignReqJsonStr, "CBC", sm4Key, "");
        request.setRequest(encryptRequest);
        //3.加密 sm4Key
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        request.setEncryptKey(encryptKey);
        request.setAccessToken("");
        //4.签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(request.getRequest(), ""));
        sb.append(StringUtils.defaultString(request.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(request.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        request.setSignature(signature);
        System.out.println("请求报文: " + JSON.toJSONString(request));

        String respJson = HttpClientUtils.post(url, JSON.toJSONString(request));
        // 5 解析响应
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
            System.out.println("------------------------------");
            System.out.println("响应报文sm4密钥:" + respSm4Key);
            // 解析报文
            String respMessage = SM4Utils.decrypt(openApiResponse.getResponse(), "CBC", respSm4Key, "");
            System.out.println("响应报文:" + respMessage);
        } else {
            System.out.println("响应报文验签失败!");
        }
    }



}
