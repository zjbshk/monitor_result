package cn.infomany;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask {
    public void run() {
        try {
            String urlPath = "https://yz.chsi.com.cn/user/center.jsp";
            String cookie = getCookie();
            String htmlStr = getHtml("GET", urlPath, cookie, null, null);
//            System.out.println(htmlStr);
            Document doc = Jsoup.parse(htmlStr);
            Element admissionTicketDownload = doc.getElementsContainingOwnText("准考证下载").first();
//            Element admissionTicketDownload = doc.getElementsContainingOwnText("专业目录").first();
            Element admissionTicketDownloadParent = admissionTicketDownload.parent();
            if (admissionTicketDownloadParent.tagName().equals("a")) {
                doc.setBaseUri("https://yz.chsi.com.cn");
                String href = admissionTicketDownloadParent.absUrl("href");
                System.out.println("absHref : " + href);
                htmlStr = getHtml("GET", href, cookie, null, null);
                outFile(htmlStr);
            } else {
                System.out.println("not ok:" + DateFormat.getInstance().format(new Date()));
            }
        } catch (IOException e) {
            System.out.println("出了点小问题");
        }

    }

    private String getCookie() {
        String filePath = System.getProperty("user.home") + File.separator + "cookie.txt";
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            String cookie = streamToString(fileInputStream).trim();
            System.out.println("cookie:" + cookie);
            return cookie;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void outFile(String htmlStr) {
        File file = new File(System.getProperty("user.home") + File.separator + "baby.html");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(htmlStr.getBytes("utf-8"));
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getHtml(String method, String urlPath, String cookie, String data, String encode) throws IOException {
        if (encode == null) {
            encode = "UTF-8";
        }
        URL url = new URL(urlPath);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        if ("POST".equalsIgnoreCase(method)) {
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoOutput(true);
            if (data != null && data.length() != 0) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(data.getBytes(encode));
                outputStream.flush();
                outputStream.close();
            }
        } else {
            // 该方法只对get和post类型有效
            httpURLConnection.setRequestMethod(method);
        }
        httpURLConnection.setRequestProperty("Cookie", cookie);
        httpURLConnection.setInstanceFollowRedirects(true);

        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpURLConnection.getInputStream();
            String responseBody = streamToString(inputStream);

            // close network resource
            inputStream.close();
            httpURLConnection.disconnect();

            return responseBody;
        } else {
            InputStream tmpIs = httpURLConnection.getErrorStream();
            if (tmpIs == null) tmpIs = httpURLConnection.getInputStream();
            String errMsg = streamToString(tmpIs);
            String msg = String.format("%d:%s", httpURLConnection.getResponseCode(), errMsg);
            throw new RuntimeException(msg);
        }
    }

    public String streamToString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }
}
