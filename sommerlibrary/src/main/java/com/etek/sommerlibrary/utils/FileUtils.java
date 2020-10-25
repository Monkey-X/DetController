package com.etek.sommerlibrary.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class FileUtils {

	// public final static String ExternalStorageDirectory =
	// "/storage/sd_external";

	public final static String ExternalStorageDirectory = Environment
			.getExternalStorageDirectory().getPath();

	private static final String TAG = "FileUtils";

	// public final static String ExternalStorageDirectory =
	// "/storage/extSdCard";

	static Context mContext;
	private static String TEMP_DIR_PATH="temp";

	public static void init(Context context) {
		mContext = context;
	}

	static FileOutputStream fos;

	public static void renameFile(String oldPath, String newPath) {
		if(TextUtils.isEmpty(oldPath)) {
			return;
		}

		if(TextUtils.isEmpty(newPath)) {
			return;
		}

		File file = new File(oldPath);
		file.renameTo(new File(newPath));
	}

	/*得到传入文件的大小*/
	public static long getFileSizes(File f) throws Exception {

		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
			fis.close();
		} else {
			f.createNewFile();
			System.out.println("文件夹不存在");
		}

		return s;
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public static String getPath(Context context, Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;//sdk版本是否大于4.4

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}


	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {column};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}




	/**
	 * 转换文件大小成KB  M等
	 */
	public static String FormentFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}


	public static void openStream(String dirPath, String fileName) {
		String filepath = ExternalStorageDirectory;
		// String filepath ="/mnt/extSdCard";
		filepath += File.separator + dirPath;
		File file;
		File dir = new File(filepath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		file = new File(filepath, fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 输出文件流
	}

	public static void writeStream(String content) {
		try {
			fos.write(content.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void closeStream() {
		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveFileToSDcard(String dirPath, String fileName,
                                        String content) {

		String filepath = ExternalStorageDirectory;
		// String filepath ="/mnt/extSdCard";
		filepath += File.separator + dirPath;
		File file;
		File dir = new File(filepath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		file = new File(filepath, fileName);
		if (file.exists()) {
			file.delete();
		}
		Log.v(TAG,"file:"+file.toString());
		// Log.d("FileUtils", filepath);
		// TODO Auto-generated method stub
		try {

			FileOutputStream os = new FileOutputStream(file);// 输出文件流
			os.write(content.getBytes());
			os.flush();
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveFileToSDcard(String fileName,
			String content) {

	
		File file;
		
		file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}

		// Log.d("FileUtils", filepath);
		// TODO Auto-generated method stub
		try {

			FileOutputStream os = new FileOutputStream(file);// 输出文件流
			os.write(content.getBytes());
			os.flush();
			os.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}










	public static String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return null;
		}
	}


	public static File[] getFileDir(String filePath) {

		File f = new File(filePath);
		File[] files = f.listFiles();

		return files;

	}

	public static String readFile(File fileName) throws IOException {
		byte[] buffer = null;
		String res = null;
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			buffer = new byte[length];
			fin.read(buffer);
			res = new String(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;

	}


	public static String readFileFromSD(String dirPath,String fileName) throws IOException {
		String filepath = ExternalStorageDirectory;
		// String filepath ="/mnt/extSdCard";
		filepath += File.separator + dirPath;
		File file;
		File dir = new File(filepath);
		if (!dir.exists()) {
			return  null;
		}
		file = new File(filepath, fileName);
		if (!file.exists()) {
			return  null;
		}
	return 	readFile(file);

	}


	
	

	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} 
		} else {
			Log.e(TAG,"文件不存在！" + "\n");
		}
	}


	public static File getFileFromUri(Uri uri, Context context) {
		if (uri == null) {
			return null;
		}
		switch (uri.getScheme()) {
			case "content":
				return getFileFromContentUri(uri, context);
			case "file":
				return new File(uri.getPath());
			default:
				return null;
		}
	}
	private static File getFileFromContentUri(Uri contentUri, Context context) {
		if (contentUri == null) {
			return null;
		}
		File file = null;
		String filePath;
		String fileName;
		String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(contentUri, filePathColumn, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
			fileName = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
			cursor.close();
			if (!TextUtils.isEmpty(filePath)) {
				file = new File(filePath);
			}
			if (!file.exists() || file.length() <= 0 || TextUtils.isEmpty(filePath)) {
				filePath = getPathFromInputStreamUri(context, contentUri, fileName);
			}
			if (!TextUtils.isEmpty(filePath)) {
				file = new File(filePath);
			}
		}
		return file;
	}
	/**
	 * 用流拷贝文件一份到自己APP目录下
	 *
	 * @param context
	 * @param uri
	 * @param fileName
	 * @return
	 */
	public static String getPathFromInputStreamUri(Context context, Uri uri, String fileName) {
		InputStream inputStream = null;
		String filePath = null;

		if (uri.getAuthority() != null) {
			try {
				inputStream = context.getContentResolver().openInputStream(uri);
				File file = createTemporalFileFrom(context, inputStream, fileName);
				filePath = file.getPath();

			} catch (Exception e) {
//				SommerLog.e(e.getMessage());
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Exception e) {
//					SommerLog.e(e.getMessage());
				}
			}
		}

		return filePath;
	}

	private static File createTemporalFileFrom(Context context, InputStream inputStream, String fileName)
			throws IOException {
		File targetFile = null;

		if (inputStream != null) {
			int read;
			byte[] buffer = new byte[8 * 1024];
			//自己定义拷贝文件路径
			targetFile = new File(context.getCacheDir(), fileName);
			if (targetFile.exists()) {
				targetFile.delete();
			}
			OutputStream outputStream = new FileOutputStream(targetFile);

			while ((read = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, read);
			}
			outputStream.flush();

			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return targetFile;
	}


	/**
	 * 复制文件，可以选择是否删除源文件
	 */
	public static boolean copyFile(File srcFile, File destFile,
								   boolean deleteSrc) {
		if (!srcFile.exists() || !srcFile.isFile()) {
			return false;
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = in.read(buffer)) > 0) {
				out.write(buffer, 0, i);
				out.flush();
			}
			if (deleteSrc) {
				srcFile.delete();
			}
		} catch (Exception e) {

			return false;
		} finally {
			IOUtils.close(out);
			IOUtils.close(in);
		}
		return true;
	}



	public static void copyDataBase2SD(Context context,String dbName) throws IOException {


		File sdcard = Environment.getExternalStorageDirectory();
		File filepath = new File(sdcard, "detonator/db/");
		if (!filepath.exists()) {
			filepath.mkdirs();
			// do not allow media scan
			new File(filepath, ".nomedia").createNewFile();
		}
		File file;

		file = new File(filepath, dbName);
		if (file.exists()) {
			file.delete();
		}
		String inFileName = context.getDatabasePath(dbName).toString();
		// Open your local db as the input stream
		InputStream myInput = new FileInputStream(inFileName);
		// Path to the just created empty db

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(file);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public static final long KB = 1024;
	public static final long MB = KB * 1024;
	public static final long GB = MB * 1024;

	/**
	 * 文件字节大小显示成M,G和K
	 * @param size
	 * @return
	 */
	public static String displayFileSize(long size) {
		if (size >= GB) {
			return String.format("%.1f GB", (float) size / GB);
		} else if (size >= MB) {
			float value = (float) size / MB;
			return String.format(value > 100 ? "%.0f MB" : "%.1f MB", value);
		} else if (size >= KB) {
			float value = (float) size / KB;
			return String.format(value > 100 ? "%.0f KB" : "%.1f KB", value);
		} else {
			return String.format("%d B", size);
		}
	}


}
