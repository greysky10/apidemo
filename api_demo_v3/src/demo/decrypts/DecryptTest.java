package demo.decrypts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pfpj.sm.SM2Utils;
import com.psbc.sop.pub.tools.security.sm.impl.SMTool_1;
import demo.common.util.SMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 请求密文解密
 */
public class DecryptTest {
    private static final Logger logger = LoggerFactory.getLogger(DecryptTest.class);

    public static void main(String[] args) throws IOException {
        String sopPublicKey = "040485CEFE14C7AF854C66D5279239E88F2E8B881C3EB1B393003D2B9F09E7064447C1A3615875B05A9164F7F637151F115B89E70DFCCD0C25CF83268E21576921";
        String sopPrivateKey = "2FF4C4A8E0682D481C6F495D75C0D5EB9D7454E49F4489ADF771ABE9732E1051";
        String sm4Key = SMUtil.getSM4Key();
        SM2Utils sm2Utils = new SM2Utils();
        String encryptKey = sm2Utils.encrypt(sopPublicKey, sm4Key);
        System.out.println("加密：" + encryptKey);
        String respSm4Key = sm2Utils.decrypt(sopPrivateKey, encryptKey);
        System.out.println("解密：" + respSm4Key);

    }
}
