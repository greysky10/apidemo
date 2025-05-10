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
 * @Description 回调ApiDemo 响应无head body
 * @Version V2.0.3
 * @Notice 服开 -> 合作方
 */
public class CallBackDemo {

    private static final Logger logger = LoggerFactory.getLogger(CallBackDemo.class);

    public static void main(String[] args) throws IOException {
        // 1.读取resource中指定环境商户信息
        Properties properties = new Properties();
        // 获取定版/预演/沙箱/生产环境测试商户信息 默认为定版环境
        //FileInputStream fis = new FileInputStream("resource/sit_config.properties");
        FileInputStream fis = new FileInputStream("resource/t1_config.properties");
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
        String respJson = "{\"accessToken\":\"\",\"encryptKey\":\"04E8937DAE5E11AC31EC89C6FBA801661A36B5B72CC315F0DE91AE7690DD799A23788C1208586851EC199C47158ACCFB5E8BB3A1B5DEAAF338A004C75D99C2F38ADE698F55460AB8293941CA765F907EF02729D6A6D123F7113A957AB5EEA0861D3C23CA61771E1E0E711B751D357707C1\",\"request\":\"xteGMenTcToroYMLCEhlLB8+MEhXY7NjrcaArJoa2DCcBt7eXqxjzyZtD3l9oFJftl60aApxNiQC\\nytQb0cFNj3JJWMOtGyg54yeLDqFsXjUP5MqON5Yr3/gzABvB8j7nwKTpjzmCHtJ3PbxLLALGL1Nb\\nVJbfe+2PYYLulf00qNZMX+w2MAjropPCeq1Ng//LhD3WQd4ZvEtyRGX2SdOp0yWUKdS87OmlrGyY\\nctvmXmDS3o7KdK9f6FIzbw22K7bWuQZVfLsxpDNUo+CMVBaBnhMl+SPNsICeVzqGLYmLozWwRtm8\\n2YKurppn7y8aEGiP72H1aSb4iY/tMUzMqHVHEzmufeAIe0HIJ/26WzWlBboBcqCfBM4S0DonleSv\\nMgr9zOTe2d8mxPmOZbni7tB3IQ==\",\"signature\":\"5bd01ba68c0f9cfff071519e88b527a91f0893f8ef5bfef5962a8201840394bd#4b1959dc8d3447f3d631f4a5b49d26fdf5e462d94ff2136c04fe0af600fbfa09\"}"; // 解析接到的请求
//        String respJson = "";
        //解密请求
        String reqMsg = requestDecrypt(merchantId, privateKey, sopPublicKey, respJson);
        JSONObject responseJson = new JSONObject();
        //解析报文失败时
        if (StringUtils.isEmpty(reqMsg)) {
            responseJson.put("respCode", "900010");
            responseJson.put("respMsg", "报文解析异常");
            //加密响应报文
            responseEncrypt(merchantId, privateKey, publicKey, sopPublicKey, responseJson);
            return;
        }
        //构造响应报文
        JSONObject requestJsonObj = JSONObject.parseObject(reqMsg);
        String transSeq = (String) requestJsonObj.get("transSeq");
        responseJson.put("respCode", "000000");
        responseJson.put("respMsg", "交易成功");
        responseJson.put("transSeq", transSeq);
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
