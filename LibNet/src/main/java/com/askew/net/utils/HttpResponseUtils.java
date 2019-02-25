package com.askew.net.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.Response;
import okio.Okio;
import okio.Sink;

/**
 * Created by lihoudong204 on 2018/11/20
 */
public class HttpResponseUtils {
    /**
     * 解析Http Header: Content-Range
     * 格式：Content-Range: bytes 0-5555/23333
     * @param contentRange
     * @return
     */
    public static long[] parseContentRange(String contentRange) {
        if (TextUtils.isEmpty(contentRange)) {
            return null;
        }
        long[] ranges = new long[3];
        try {
            String[] str = contentRange.trim().split("\\s+");
            if (str.length > 1) {
                String rangeAndLength = str[1];
                String[] rangeAndLengths = rangeAndLength.split("/");
                if (rangeAndLengths.length > 1) {
                    String[] rangeStr = rangeAndLengths[0].split("-");
                    ranges[0] = Long.parseLong(rangeStr[0]);
                    ranges[1] = Long.parseLong(rangeStr[1]);
                }
                ranges[2] = Long.parseLong(rangeAndLengths[1]);
            }
        }catch (Exception e){
            return null;
        }

        return ranges;
    }

    /**
     * 拼接文件路径
     * @param response
     * @param fileDir
     * @param fileName
     * @return
     */
    public static String getFilePath(Response response, String fileDir, String fileName) {
        if (TextUtils.isEmpty(fileDir)) {
            return "";
        }
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if(TextUtils.isEmpty(fileName)){ //文件名为空，从http头中获取
            fileName = getFileName(response);
        }
        if (TextUtils.isEmpty(fileName)) { //文件名为空，使用时间戳作为文件名
            fileName = System.currentTimeMillis() + "";
        }
        if(TextUtils.isEmpty(getExtension(fileName))){ //文件名中不包含扩展名
            fileName = fileName + "." + getExtension(getFileName(response));
        }

        return dir.getAbsolutePath() + File.separator + fileName;
    }

    /**
     * 从http响应头的Content-Disposition字段中获取文件名
     * @param response
     * @return
     */
    private static String getFileName(Response response) {
        String contentDisposition = response.header("Content-Disposition");
        if (TextUtils.isEmpty(contentDisposition)) {
            return null;
        }
        int index = contentDisposition.indexOf("filename=");
        if (index == -1) {
            return null;
        }
        int endIndex = contentDisposition.indexOf(';', index);
        if (endIndex == -1) {
            endIndex = contentDisposition.length();
        }
        int startIndex = index + "filename=".length();
        return contentDisposition.substring(startIndex, endIndex);
    }

    /**
     * 获取扩展名
     * @param fileName
     * @return
     */
    private static String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1 || index == fileName.length() - 1) {//没有.或者最后一个字符是.
            return "";
        }
        if(fileName.charAt(index + 1) == '/'){ //  形如./test
            return "";
        }
        return fileName.substring(index + 1);
    }

    /**
     * 保存response到文件中
     * @param response
     * @param file
     * @throws Exception
     */
    public static void trySaveFile(Response response, File file) throws Exception {
        if (response.code() == 200) {
            //断点续传
            saveWholeFile(response, file);
        } else if (response.code() == 206) { //续传
            String range = response.header("Content-Range");
            long[] ranges = HttpResponseUtils.parseContentRange(range);
            if (ranges == null) {
                saveWholeFile(response, file);
            }else {
                savePartialFile(response, file, ranges[0]);
            }
        }
    }

    private static void savePartialFile(Response response, File file, long range) throws IOException {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(range);
            byte[] bytes = new byte[4096];
            int len;
            while ((len = response.body().byteStream().read(bytes)) > 0) {
                randomAccessFile.write(bytes, 0, len);
            }
        }finally {
            randomAccessFile.close();

        }
    }

    private static void saveWholeFile(Response response, File file) throws IOException {
        Sink sink = Okio.sink(file);
        try {
            response.body().source().readAll(sink);
        }finally {
            if (sink != null) {
                sink.close();
            }
        }
    }

}
