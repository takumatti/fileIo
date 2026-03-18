package io.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * S3ファイル管理クラス
 */
@Component
@RequiredArgsConstructor
public class S3FileManager {
	/**
	 * S3バケット名
	 */
	@Value("${aws.s3.bucketName}")
	private String bucketName;
	/**
	 * S3プレフィックスキー
	 */
	@Value("${aws.s3.rootKey}")
	private String rootKey;
	/**
	 * S3クライアント
	 */
	private final S3Client s3;
	/**
	 * CSVキー
	 */
	private static final String KEY_CSV = "csv/";

	/**
	 * CSVアップロード
	 * 
	 * @param fileName     ファイル名
	 * @param contentBytes コンテンツバイト配列
	 */
	public void uploadCsv(String fileName, byte[] contentBytes) {
		s3.putObject(PutObjectRequest.builder().bucket(bucketName).key(rootKey + KEY_CSV + fileName)
				.contentType("text/csv").build(), RequestBody.fromBytes(contentBytes));
	}
}