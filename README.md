# fileIo

## 📌 プロジェクト概要

**fileIo** は、Java（Spring Boot）を用いて  
**CSV / Excel / PDF などのファイル入出力処理や、DBテーブル情報の取得処理を検証・学習するためのサンプルプロジェクト**です。

業務で頻出する以下のような処理を、実装例としてまとめています。

- CSV の取込・出力
- Excel の取込・出力
- PDF の出力
- 請求書レイアウトの PDF 出力
- DB のテーブル情報を取得する VIEW 的処理

## 🧱 技術スタック

- Java
- Spring Boot
- Thymeleaf
- Gradle
- RDB（DB VIEW 用）

## 📂 ディレクトリ構成（抜粋）
```text
src/
├── main/
│   ├── java/
│   │   └── 各種ファイル入出力処理
│   └── resources/
│       └── templates/
│           ├── csv/        # CSV 出力・取込サンプル
│           ├── excel/     # Excel 出力・取込サンプル
│           ├── pdf/       # PDF 出力サンプル
│           ├── invoice/   # 請求書 PDF 出力サンプル
│           └── dbview/    # DB テーブル情報取得用画面
```

## ✨ 主な機能

### CSV 関連
- CSV ファイルの出力
- CSV ファイルの取込

### Excel 関連
- Excel ファイルの出力
- Excel ファイルの取込

### PDF 関連
- PDF ファイルの出力
- 請求書フォーマットの PDF 出力

### DB VIEW 機能
- DB のテーブル情報を取得
- テーブル定義確認用の VIEW 的サンプル

## 🎯 このプロジェクトの目的

本プロジェクトは、実務で利用頻度の高いファイル入出力処理を題材に、  
Java / Spring Boot における実装方法を整理・検証することを目的としています。

- Java におけるファイル IO（CSV / Excel / PDF）の基本的な実装理解
- 業務システムでよく利用される帳票・請求書出力処理の検証
- DB のメタ情報（テーブル定義など）を取得する処理の実装例整理
- Spring Boot + Thymeleaf を用いた Web アプリ構成の学習

## 📘 想定利用シーン

- Java / Spring Boot の学習用サンプルコードとして
- 業務システム開発前の技術検証・実装参考用
- ファイル入出力処理（CSV / Excel / PDF）の実装例集としての参照
