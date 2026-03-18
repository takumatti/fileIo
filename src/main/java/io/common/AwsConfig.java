package io.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS設定クラス
 */
@Configuration
public class AwsConfig {
	/**
	 * AWSリージョン
	 */
	@Value("${aws.region}")
	private String region;
	/**
	 * AWSアクセスキー
	 */
	@Value("${aws.accessKey}")
	private String accessKey;
	/**
	 * AWSセキュリティキー
	 */
	@Value("${aws.secretKey}")
	private String secretKey;

	/**
	 * EC2クライアントBean定義
	 * 
	 * @return EC2クライアント
	 */
	@Bean
	public Ec2Client ec2() {
		return Ec2Client.builder().region(Region.of(region)).credentialsProvider(getProvider()).build();
	}

	/**
	 * EC2非同期クライアントBean定義
	 * 
	 * @return EC2非同期クライアント
	 */
	@Bean
	public Ec2AsyncClient ec2Async() {
		return Ec2AsyncClient.builder().region(Region.of(region)).credentialsProvider(getProvider()).build();
	}

	/**
	 * S3クライアントBean定義
	 * 
	 * @return S3クライアント
	 */
	@Bean
	public S3Client s3() {
		return S3Client.builder().region(Region.of(region)).credentialsProvider(getProvider()).build();
	}

	/**
	 * S3非同期クライアントBean定義
	 * 
	 * @return S3非同期クライアント
	 */
	@Bean
	public S3AsyncClient s3Async() {
		return S3AsyncClient.builder().region(Region.of(region)).credentialsProvider(getProvider()).build();
	}

	/**
	 * AWS認証情報取得
	 * 
	 * @return AWS認証情報
	 */
	private AwsCredentials getCredentials() {
		return AwsBasicCredentials.create(accessKey, secretKey);
	}

	/**
	 * AWS認証情報プロバイダー取得
	 * 
	 * @return AWS認証情報プロバイダー
	 */
	private AwsCredentialsProvider getProvider() {
		return StaticCredentialsProvider.create(getCredentials());
	}
}