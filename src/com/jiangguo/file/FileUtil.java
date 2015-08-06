package com.jiangguo.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * @author lisao ѹ��ͼƬ���洢
 */
public class FileUtil {

	public static Bitmap compressBySize(String pathName, int targetWidth,
			int targetHeight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;// ��ȥ��Ľ���ͼƬ��ֻ�ǻ�ȡͼƬ��ͷ����Ϣ��������ߵȣ�
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);//
		// �õ�ͼƬ�Ŀ�ȡ��߶ȣ�
		float imgWidth = opts.outWidth;
		float imgHeight = opts.outHeight;
		float tmp;
		// ���Ǻ���ͼƬ��ת��͸�
		if (imgWidth > imgHeight) {
			tmp = imgHeight;
			imgWidth = imgHeight;
			imgHeight = tmp;
		}
		// �ֱ����ͼƬ��ȡ��߶���Ŀ���ȡ��߶ȵı�����ȡ���ڵ��ڸñ�������С������
		int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
		int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
		opts.inSampleSize = 1;
		if (widthRatio > 1 || widthRatio > 1) {
			if (widthRatio > heightRatio) {
				opts.inSampleSize = widthRatio;
			} else {
				opts.inSampleSize = heightRatio;
			}
		}
		// ���ú����ű����󣬼���ͼƬ�����ݣ�
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(pathName, opts);
		saveFile(bitmap, pathName);
		return bitmap;
	}

	public static void saveFile(Bitmap bm, String fileName) {
		File dirFile = new File(fileName);
		// ���ͼƬ�Ƿ����
		if (dirFile.exists()) {
			dirFile.delete(); // ɾ��ԭͼƬ
		}
		File myCaptureFile = new File(fileName);
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			// 100��ʾ������ѹ����70��ʾѹ����Ϊ30%
			bm.compress(Bitmap.CompressFormat.JPEG, 50, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
