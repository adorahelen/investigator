package hycu.investigator.msa.extractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlainTextFileContentExtractor implements ContentExtractor {

    @Override
    public String extract(Path filePath) throws IOException {
        System.out.println("일반 텍스트 파일 읽기 중: " + filePath);
        return new String(Files.readAllBytes(filePath));
    }

    @Override
    public boolean supports(Path filePath) {
        try {
            String mimeType = Files.probeContentType(filePath);
            return mimeType == null || mimeType.startsWith("text/");
        } catch (IOException e) {
            return false; // MIME 타입 확인 불가 시 지원하지 않는 것으로 간주
        }
    }
}
