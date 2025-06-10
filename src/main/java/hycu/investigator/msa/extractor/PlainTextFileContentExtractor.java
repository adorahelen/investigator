package hycu.investigator.msa.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class PlainTextFileContentExtractor implements ContentExtractor {

    @Override
    public String extract(InputStream is, String originalFilename) throws IOException {
        System.out.println("일반 텍스트 파일 읽기 중: " + originalFilename);
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        System.out.println("일반 텍스트 파일 읽기 완료. (읽은 텍스트 크기: " + content.length() + " 바이트)");
        return content.toString();
    }

    @Override
    public boolean supports(String originalFilename) {
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == originalFilename.length() - 1) {
            // 확장자가 없으면 일반 텍스트 파일일 가능성 있으므로 지원
            return true;
        }
        String extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        List<String> textExtensions = Arrays.asList(
                "txt", "log", "csv", "json", "xml", // Tika도 처리하지만, 간단한 텍스트는 여기서 처리 가능
                "java", "py", "js", "html", "css", "md" // 코드 파일
        );
        return textExtensions.contains(extension);
    }


//    @Override
//    public String extract(Path filePath) throws IOException {
//        System.out.println("일반 텍스트 파일 읽기 중: " + filePath);
//        return new String(Files.readAllBytes(filePath));
//    }
//
//    @Override
//    public boolean supports(Path filePath) {
//        try {
//            String mimeType = Files.probeContentType(filePath);
//            return mimeType == null || mimeType.startsWith("text/");
//        } catch (IOException e) {
//            return false; // MIME 타입 확인 불가 시 지원하지 않는 것으로 간주
//        }
//    }
}