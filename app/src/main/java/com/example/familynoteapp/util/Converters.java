package com.example.familynoteapp.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.List;

public class Converters {

    @TypeConverter
    public static String fromList(List<String> list) {
        return list == null ? null : new Gson().toJson(list);
    }

    @TypeConverter
    public static List<String> toList(String json) {
        return json == null ? null : new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
    }

    /**
     * Lưu ảnh từ Uri vào bộ nhớ trong của app theo fileName.
     * Dùng openInputStream() để tránh SecurityException với URI của Google Photos.
     * Trả về đường dẫn file local hoặc null nếu lỗi.
     */
    public static String saveImageToInternalStorage(Context context, Uri uri, String fileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // Thử mở InputStream từ URI
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e("vinhnc", "InputStream null cho uri: " + uri);
                return null;
            }

            File file = new File(context.getFilesDir(), fileName);
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[8192]; // buffer lớn hơn một chút
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            Log.d("vinhnc", "Đã lưu file: " + file.getAbsolutePath());
            return file.getAbsolutePath();

        } catch (SecurityException se) {
            Log.e("vinhnc", "SecurityException khi đọc uri: " + uri, se);

            // Kiểm tra nếu URI đến từ Google Photos content provider
            if (uri != null && "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority())) {
                Log.e("vinhnc", "Không có quyền truy cập URI Google Photos. Nên yêu cầu người dùng chọn ảnh mới.");
            }

            return null;

        } catch (IOException e) {
            Log.e("vinhnc", "IOException khi lưu ảnh từ uri: " + uri, e);
            return null;
        } catch (Exception e) {
            Log.e("vinhnc", "Lỗi không xác định khi lưu ảnh", e);
            return null;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException ignored) {}
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException ignored) {}
        }
    }

}
