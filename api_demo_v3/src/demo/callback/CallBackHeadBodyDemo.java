package demo.callback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pfpj.sm.SM2Utils;
import com.pfpj.sm.SM4Utils;
import com.pfpj.sm.Signature;
import demo.common.pojo.common.OpenApiRequest;
import demo.common.pojo.common.OpenApiResponse;
import demo.common.util.SMTool;
import demo.common.util.SMUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author demoAuthor
 * @Description 回调ApiDemo 响应有head body
 * @Version V2.0.3
 * @Notice 服开 -> 合作方
 */
public class CallBackHeadBodyDemo {

    private static final Logger logger = LoggerFactory.getLogger(CallBackHeadBodyDemo.class);

    public static void main(String[] args) throws IOException {
        // 1.读取resource中指定环境商户信息
        Properties properties = new Properties();
        // 获取定版/预演/沙箱/生产环境测试商户信息 默认为定版环境
//        FileInputStream fis = new FileInputStream("resource/sit_config.properties");
//        FileInputStream fis = new FileInputStream("resource/t1_config.properties");
        FileInputStream fis = new FileInputStream("resource/g3_config.properties");
        //FileInputStream fis = new FileInputStream("resource/sandbox_config.properties");
        //FileInputStream fis = new FileInputStream("resource/prod_config.properties");
        properties.load(fis);
        // 合作方编号
        String merchantId = properties.getProperty("merchantId");
        // 合作方私钥
        String privateKey = properties.getProperty("privateKey");
        // 合作方公钥
        String publicKey = properties.getProperty("publicKey");
        // 服开与合作方配对公钥
        String sopPublicKey = properties.getProperty("sopPublicKey");
        // 接到的服开请求密文
        String respJson = "{\"request\":\"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\\n\",\"signature\":\"9ff44fb2178d16fcd29e2f42e3f3c2402e1008ac05608342dd805c01bb9dd184#226cac29cd6c215a71748c95ddaca9201aa7bb5cb46f0d08cef6f785fa2f1804\",\"accessToken\":null,\"encryptKey\":\"042D001D47827FDF447871620BC8B339E9BDE0511200FDAECC1D0266C05355300677C17B512627B6865BFA4B23EA2EB0AD969A45DDBCDD519D724AD195E40107C24E222A0BAEABDCF094B9242E9D7EA706B336EE3B4F5E7E501CD4603AC9464B8E92B5FFD243FA2D7CD22E26C9D37DC40E\"}";
//        String respJson = "";
        //解密请求
        String reqMsg = requestDecrypt(merchantId, privateKey, sopPublicKey, respJson);
        JSONObject responseJson = new JSONObject();
        JSONObject responseHeadJson = new JSONObject();
        JSONObject responseBodyJson = new JSONObject();
        if (StringUtils.isEmpty(reqMsg)) {
            responseBodyJson.put("respCode", "900010");
            responseBodyJson.put("respMsg", "报文解析异常");
            responseJson.put("head", responseHeadJson);
            responseJson.put("body", responseBodyJson);
            responseEncrypt(merchantId, privateKey, publicKey, sopPublicKey, responseJson);
            return;
        }
        //构造响应报文
        JSONObject requestJsonObj = JSONObject.parseObject(reqMsg);
        JSONObject requestHeadJsonObj = requestJsonObj.getJSONObject("head");
        JSONObject requestBodyJsonObj = requestJsonObj.getJSONObject("body");
        requestHeadJsonObj.remove("accessType");
        responseHeadJson = requestHeadJsonObj;
        String transSeq = (String) requestBodyJsonObj.get("transSeq");
        responseBodyJson.put("respCode", "000000");
        responseBodyJson.put("respMsg", "交易成功");
        responseBodyJson.put("transSeq", transSeq);
        responseJson.put("head", responseHeadJson);
        responseJson.put("body", responseBodyJson);
        // 加密响应
        OpenApiResponse response = responseEncrypt(merchantId, privateKey, publicKey, sopPublicKey, responseJson);
        logger.info("加密响应报文response: {}", JSONObject.toJSONString(response));
    }

    /**
     * 加密响应报文
     *
     * @param merchantId   合作方编号
     * @param privateKey   合作方私钥
     * @param publicKey    合作方公钥
     * @param sopPublicKey 服开与合作方配对公钥
     * @param responseJson 响应报文
     * @return OpenApiResponse 加密响应对象
     * @throws IOException
     */
    private static OpenApiResponse responseEncrypt(String merchantId, String privateKey, String publicKey, String sopPublicKey, JSONObject responseJson) throws IOException {
        OpenApiResponse openApiResponse = new OpenApiResponse();
        String orignRespJsonStr = JSON.toJSONString(responseJson);
        logger.debug("原始响应报文: {}", orignRespJsonStr);
        String sm4Key = SMUtil.getSM4Key();
        String encryptResponse = SM4Utils.encrypt(orignRespJsonStr, "CBC", sm4Key, "");
        openApiResponse.setResponse(encryptResponse);
        // 加密sm4密钥
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        openApiResponse.setEncryptKey(encryptKey);
        openApiResponse.setAccessToken("");
        // 计算签名
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(openApiResponse.getResponse(), ""));
        sb.append(StringUtils.defaultString(openApiResponse.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(openApiResponse.getAccessToken(), ""));
        Signature sign = sm2Utils.sign(merchantId, privateKey, sb.toString(), publicKey);
        String signature = SMUtil.toSignStr(sign);
        openApiResponse.setSignature(signature);
        logger.info(signature);
        logger.debug("加密响应报文: {}", JSON.toJSONString(openApiResponse));
        return openApiResponse;
    }

    /**
     * 解密服开的请求
     *
     * @param merchantId   合作方编号
     * @param privateKey   合作方私钥
     * @param sopPublicKey 服开与合作方配对公钥
     * @param respJson     接到的服开请求密文
     * @return 接到的服开请求报文明文
     * @throws IOException
     */
    private static String requestDecrypt(String merchantId, String privateKey, String sopPublicKey, String respJson) throws IOException {
        OpenApiRequest openApiRequest = JSON.parseObject(respJson, OpenApiRequest.class);
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.defaultString(openApiRequest.getRequest(), ""));
        sb.append(StringUtils.defaultString(openApiRequest.getEncryptKey(), ""));
        sb.append(StringUtils.defaultString(openApiRequest.getAccessToken(), ""));
        logger.info("解析得到的请求密文：{}", sb);
        // 验签
        SM2Utils sm2Utils = new SM2Utils();
        boolean checked = sm2Utils.verifySign(merchantId, sopPublicKey, sb.toString(), SMTool.fromString(openApiRequest.getSignature()));
        String respMsg = null;
        if (checked) {
            logger.info("验签成功");
            //解析密钥
            String sm4Key = sm2Utils.decrypt(privateKey, openApiRequest.getEncryptKey());
            logger.info("请求报文 sm4 密钥:{}", sm4Key);
            //解析报文
            respMsg = SM4Utils.decrypt(openApiRequest.getRequest(), "CBC", sm4Key, "");
            logger.info("请求报文明文：{}", respMsg);
        } else {
            logger.info("验签失败");
        }
        return respMsg;
    }

}
