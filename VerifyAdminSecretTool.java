import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * 此工具为验证IM服务管理密钥是否正确的工具。
 * 通过手机号查询 Wildfire IM 用户信息，如果能够返回正确信息或者未找到表示密钥正确。
 *
 * 编译：放到IM服务运行服务器上，执行 javac VerifyAdminSecretTool.java  编译得到 VerifyAdminSecretTool.class，
 * 用法：执行 java VerifyAdminSecretTool 按提示输入 IM 服务地址、管理员密钥、电话号码。
 * 程序会调用 /admin/user/get_info 接口并打印返回的 JSON。
 */
public class VerifyAdminSecretTool {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入 IM 服务地址（如 http://192.168.1.100:18080）: ");
        String serverUrl = scanner.nextLine().trim();

        System.out.print("请输入管理员密钥（admin secret）: ");
        String secret = scanner.nextLine().trim();

        System.out.print("请输入电话号码: ");
        String mobile = scanner.nextLine().trim();

        scanner.close();

        if (serverUrl.isEmpty() || secret.isEmpty() || mobile.isEmpty()) {
            System.err.println("错误：服务地址、密钥和电话号码均不能为空");
            System.exit(1);
        }

        // 构建请求 URL（去掉末尾可能的 /）
        if (serverUrl.endsWith("/")) {
            serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
        }
        String urlStr = serverUrl + "/admin/user/get_info";

        // 构建请求体
        String jsonBody = "{\"mobile\":\"" + mobile + "\",\"includeDeleted\":false}";

        // 生成认证签名
        int nonce = RANDOM.nextInt(1000000) + 1;
        long timestamp = System.currentTimeMillis();
        String signStr = nonce + "|" + secret + "|" + timestamp;
        String sign = sha1Hex(signStr);

        // 发送 HTTP POST 请求
        String response = doPost(urlStr, jsonBody, nonce, timestamp, sign);

        // 输出结果
        System.out.println("\n===== 返回结果 =====");
        System.out.println(response);
    }

    private static String doPost(String urlStr, String body, int nonce, long timestamp, String sign) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("nonce", String.valueOf(nonce));
            conn.setRequestProperty("timestamp", String.valueOf(timestamp));
            conn.setRequestProperty("sign", sign);
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            // 写入请求体
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            // 读取响应
            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    private static String sha1Hex(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
