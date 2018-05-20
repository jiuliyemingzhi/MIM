package net.jiuli.factoylib.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import net.jiuli.common.utils.HashUtil;
import net.jiuli.factoylib.Factory;

import java.io.File;
import java.util.Date;


/**
 * Created by jiuli on 17-9-4.
 */

public class UploadHelper {
    private static final String BUCKET_NAME = "bucketlw";
    private static final String ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";
    private static final String TAG = UploadHelper.class.getSimpleName();

    public static OSS getClient() {
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考访问控制章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAIRM3zYCbszOWz",
                "dYSRr9WAJxMqrHovvPGKZK9MtfOjAh");
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    private static String upLoad(String objKey, String path) {

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, objKey, path);

        try {
            OSS client = getClient();
            PutObjectResult result = client.putObject(put);
            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            Log.d(TAG, String.format("presignPublicObjectURL:%s", url));
            return url;
        } catch (ClientException e) {
            System.out.println("error");
        } catch (ServiceException e) {
            System.out.println("error");
        }
        return null;
    }


    public static String uploadImage(String path) {
        String key = getImageObjKey(path);
        return upLoad(key, path);
    }

    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }




    private static String getImageObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        return String.format("image/%s/%s.jpg", getDateString(), fileMd5);
    }

    public static String getPortraitObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        return String.format("portrait/%s/%s.jpg", getDateString(), fileMd5);
    }

    public static  String uploadPortrait(String path){
        return upLoad(getPortraitObjKey(path),path);
    }
    public static String uplaodAudioObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        return String.format("audio/%s/%s.mp3", getDateString(), fileMd5);
    }
}


