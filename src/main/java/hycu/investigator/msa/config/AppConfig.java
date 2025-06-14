package hycu.investigator.msa.config;

import hycu.investigator.msa.extractor.ContentExtractor;
import hycu.investigator.msa.extractor.PlainTextFileContentExtractor;
import hycu.investigator.msa.extractor.TikaUniversalContentExtractor;
import hycu.investigator.msa.pattern.PrivacyPatternProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig { //  현재 코드는 최소 4개의 스레드(패턴 탐지용) @Async 붙어있는 건 UI
    // @Async는 파일 업로드 요청 자체를 빠르게 처리하고 실제 파일 분석은 별도의 스레드에서 비동기적으로 진행하므로, UI가 멈추지 않도록

    private static final int THREAD_POOL_SIZE = 4;

    @Bean(destroyMethod = "shutdown") // 애플리케이션 종료 시 스레드 풀 종료
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Bean
    public PrivacyPatternProvider privacyPatternProvider() {
        return new PrivacyPatternProvider();
    }

    @Bean
    public TikaUniversalContentExtractor tikaUniversalContentExtractor() {
        return new TikaUniversalContentExtractor();
    }

    @Bean
    public PlainTextFileContentExtractor plainTextFileContentExtractor() {
        return new PlainTextFileContentExtractor();
    }

    // 모든 ContentExtractor 구현체를 리스트로 묶어서 주입 가능하도록
    @Bean
    public List<ContentExtractor> contentExtractors(
            TikaUniversalContentExtractor tikaExtractor,
            PlainTextFileContentExtractor plainTextExtractor) {
        // Tika가 대부분을 처리하므로, Tika를 먼저 시도하도록 순서 지정
        return Arrays.asList(tikaExtractor, plainTextExtractor);
    }
}