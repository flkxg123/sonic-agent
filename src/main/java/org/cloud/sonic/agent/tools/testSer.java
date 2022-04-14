package org.cloud.sonic.agent.tools;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.cloud.sonic.agent.tools.BytesTool.subByteArray;

public class testSer {
    public static void main(String[] args) throws IOException {
        Socket test = new Socket("localhost", 5002);
        InputStream inputStream = test.getInputStream();
        OutputStream outputStream = test.getOutputStream();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jsonrpc", "2.0");
        jsonObject.put("method", "Dump");
        jsonObject.put("params", Arrays.asList(true));
        jsonObject.put("id", 0);
        int len = jsonObject.toJSONString().length();
        ByteBuffer header = ByteBuffer.allocate(4);
        header.put(intToByteArray(len), 0, 4);
        header.flip();
        ByteBuffer body = ByteBuffer.allocate(len);
        body.put(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8), 0, len);
        body.flip();
        ByteBuffer total = ByteBuffer.allocate(len + 4);
        total.put(header.array());
        total.put(body.array());
        total.flip();
        outputStream.write(total.array());
        while (test.isConnected()) {
            byte[] head = new byte[4];
            inputStream.read(head);
            byte[] buffer = new byte[toInt(head)];
            int realLen;
            realLen = inputStream.read(buffer);
            if (realLen >= 0) {
                System.out.println(new String(buffer));
            }
        }
    }

    public static int toInt(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << (i * 8);
        }
        return res;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i & 0xff);
        result[1] = (byte) (i >> 8 & 0xff);
        result[2] = (byte) (i >> 16 & 0xff);
        result[3] = (byte) (i >> 24 & 0xff);
        return result;
    }
}
