package io.common;

/**
 * クォーテーション有無Enum
 */
public enum QuoteMode {

    ALL(true),     // 全項目をダブルクォートで囲う
    NONE(false);   // 囲わない

    private final boolean quote;

    QuoteMode(boolean quote) {
        this.quote = quote;
    }

    public boolean isQuote() {
        return quote;
    }

    /**
     * 画面から渡された値を enum に変換
     */
    public static QuoteMode from(String value) {
        return QuoteMode.valueOf(value.toUpperCase());
    }
}
