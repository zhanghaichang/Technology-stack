package com.jiaoyiping.passwordmanager.pki;
 /*
  * Created with Intellij IDEA
  * USER: 焦一平
  * Mail: jiaoyiping@gmail.com
  * Date: 2016/10/2
  * Time: 12:05
  * To change this template use File | Settings | Editor | File and Code Templates
 */

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * 签名和验证签名
 */
public class TestSign {
    //证书密码
    private static final String PASSWORD = "123456";
    //证书别名
    private static final String ALIAS = "test";

    public static void main(String[] args) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream("D:\\test.p12"), PASSWORD.toCharArray());
        Certificate x509Certificate = keyStore.getCertificate(ALIAS);

        encrypt(x509Certificate.getPublicKey(), (PrivateKey) keyStore.getKey(ALIAS, PASSWORD.toCharArray()));
        System.out.println("==============================================================================");
        sign(keyStore);
    }

    /**
     * 签名和验证签名
     *
     * @throws Exception
     */
    public static void sign(KeyStore keyStore) throws Exception {

        X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(ALIAS);
        //需要签名的信息的内容
        String message = "中国移动通信研究院";
        //获取CA证书私钥
        PrivateKey priKey = (PrivateKey) keyStore.getKey(ALIAS, PASSWORD.toCharArray());
        System.out.println("私钥:" + Hex.encodeHexString(priKey.getEncoded()));

        //用私钥签名
        Signature signature = Signature.getInstance("NONEwithRSA");
        signature.initSign(priKey);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeUTF(message);
        signature.update(byteArrayOutputStream.toByteArray());
        String result = Hex.encodeHexString(signature.sign());
        System.out.println("签名之后的内容:" + result);


        //用公钥来验证签名
        Signature signature1 = Signature.getInstance("NONEwithRSA");
        signature1.initVerify(x509Certificate.getPublicKey());
        System.out.println("公钥:" + Hex.encodeHexString(x509Certificate.getPublicKey().getEncoded()));
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream1 = new DataOutputStream(byteArrayOutputStream1);
        dataOutputStream1.writeUTF(message);
        signature1.update(byteArrayOutputStream1.toByteArray());

        System.out.println("验证结果:   " + signature1.verify(Hex.decodeHex(result.toCharArray())));
    }

    /**
     * 加密和解密
     *
     * @param publicKey
     * @param privateKey
     * @throws Exception
     */
    public static void encrypt(PublicKey publicKey, PrivateKey privateKey) throws Exception {

        String input = "慧与(中国)有限公司";
        Cipher cipher = Cipher.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) publicKey;
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        //加密后的内容
        System.out.println("加密之后的内容:" + Hex.encodeHexString(cipherText));


        //解密
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] plainText = cipher.doFinal(cipherText);
        System.out.println("解密之后的内容 : " + new String(plainText));

    }


}
