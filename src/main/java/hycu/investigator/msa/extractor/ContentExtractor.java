package hycu.investigator.msa.extractor;


import java.io.IOException;
import java.nio.file.Path;

public interface ContentExtractor {
    String extract(Path filePath) throws IOException;
    boolean supports(Path filePath); // 특정 파일 경로를 지원하는지 여부
}