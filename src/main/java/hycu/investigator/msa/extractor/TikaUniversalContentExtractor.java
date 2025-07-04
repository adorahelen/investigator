package hycu.investigator.msa.extractor;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.net.URLConnection; // MIME 타입 추론을 위해 추가
import java.util.Arrays;
import java.util.List;

import java.net.FileNameMap;
import java.net.URLConnection;


// TikaUniversalContentExtractor 하나만으로 PDF, DOCX, XLSX, PPTX, DOC, XLS, PPT 등 수많은 문서 형식의 텍스트를 추출할 수 있습니다.
public class TikaUniversalContentExtractor implements ContentExtractor {

    private final AutoDetectParser parser = new AutoDetectParser();
    private final ParseContext context = new ParseContext();
    private final FileNameMap fileNameMap = URLConnection.getFileNameMap(); // 파일명으로 MIME 타입 추론



    @Override
    public String extract(InputStream is, String originalFilename) throws IOException {
//        System.out.println("Tika를 사용하여 파일 텍스트 추출 중: " + filePath);
        System.out.println("Tika를 사용하여 파일 텍스트 추출 중: " + originalFilename);

//        try (InputStream input = Files.newInputStream(filePath)) {
//            // Tika 2.x에서는 BodyContentHandler 생성자에 -1을 주어 메모리 제한 없이 전체 내용을 추출할 수 있음
//            BodyContentHandler handler = new BodyContentHandler(-1);
//            Metadata metadata = new Metadata();
//
//            // 파서의 parse 메서드는 inputstream, contenthandler, metadata, parsecontext를 인자로 받음
//            parser.parse(input, handler, metadata, context);
//            String extractedText = handler.toString();
//            System.out.println("Tika 텍스트 추출 완료. (추출된 텍스트 크기: " + extractedText.length() + " 바이트)");
//            return extractedText;
//        } catch (TikaException | SAXException e) {
//            throw new IOException("파일 텍스트 추출 중 오류 발생 (Tika)", e);
//        }
//    }

        try {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            // Tika가 파일 타입을 더 잘 감지하도록 힌트 제공
            metadata.set(TikaCoreProperties.ORIGINAL_RESOURCE_NAME, originalFilename);
//            metadata.set(Metadata.RESOURCE_NAME_KEY, originalFilename);
            String mimeTypeHint = fileNameMap.getContentTypeFor(originalFilename);
            if (mimeTypeHint != null) {
                metadata.set(Metadata.CONTENT_TYPE, mimeTypeHint);
            }



        parser.parse(is, handler, metadata, context);
        String extractedText = handler.toString();
        System.out.println("Tika 텍스트 추출 완료. (추출된 텍스트 크기: " + extractedText.length() + " 바이트)");
        return extractedText;
    } catch (TikaException | SAXException e) {
        throw new IOException("파일 텍스트 추출 중 오류 발생 (Tika)", e);
    }
}

//    @Override
//    public boolean supports(Path filePath) {
//        try {
//            // Tika의 AutoDetectParser는 내부적으로 다양한 MIME 타입을 처리합니다.
//            // 여기서는 Files.probeContentType()로 얻은 MIME 타입을 기반으로 Tika가 처리 가능한지 대략적으로 판단합니다.
//            // 하지만 Tika의 실제 지원 범위는 훨씬 넓으므로, 여기서 너무 엄격하게 필터링하지 않는 것이 좋습니다.
//            String mimeType = Files.probeContentType(filePath);
//
//            // 중요: Files.probeContentType()는 모든 MIME 타입을 정확히 반환하지 않을 수 있으며,
//            // Tika는 probeContentType이 알지 못하는 일부 파일도 처리할 수 있습니다.
//            // 따라서, 이 `supports` 메서드는 대략적인 필터링으로 사용하고,
//            // 실제 파싱은 Tika의 AutoDetectParser에 맡기는 것이 좋습니다.
//            // Tika가 처리 가능한 파일은 매우 다양하므로, 여기서는 `.txt`처럼 일반적인 텍스트 파일과
//            // `application/`으로 시작하는 문서 파일들을 포괄적으로 지원한다고 판단할 수 있습니다.
//            // Office 파일, PDF 등 문서 파일은 대부분 `application/` MIME 타입을 가집니다.
//
//            if (mimeType != null) {
//                // 특정 MIME 타입만 지원하고 싶다면 여기에 추가
//                // 예: return mimeType.equals("application/pdf") || mimeType.contains("word") || mimeType.contains("excel") || mimeType.contains("powerpoint");
//                // 하지만 AutoDetectParser를 사용한다면 대부분 자동으로 처리되므로,
//                // Files.probeContentType()가 알려진 문서 포맷임을 나타내면 True를 반환하는 것이 합리적입니다.
//
//                // 임의의 파일이더라도 Tika가 시도해볼 수 있도록 광범위하게 true를 반환하거나
//                // 주요 문서 MIME 타입에 대해서만 true를 반환합니다.
//                return mimeType.startsWith("application/") || mimeType.startsWith("text/");
//            }
//            // MIME 타입을 알 수 없지만 Tika가 처리할 수도 있는 경우 (예: 확장자 없는 파일)
//            // 필요에 따라 true를 반환하여 Tika가 시도하게 하거나, false를 반환하여 제외할 수 있습니다.
//            // 여기서는 일단 알려진 MIME 타입만 처리하는 것으로 제한합니다.
//            return false;
//
//        } catch (IOException e) {
//            // 파일이 존재하지 않거나 권한 문제 등으로 MIME 타입을 확인할 수 없는 경우
//            return false;
//        }
//    }


    @Override
    public boolean supports(String originalFilename) {
        // Tika는 대부분의 문서 타입을 지원하므로,
        // 여기서는 파일 확장자를 기반으로 Tika가 처리할 만한 '문서' 유형인지 대략적으로 판단합니다.
        // Tika는 MIME 타입을 알 수 없어도 시도하므로, 이 메서드가 false여도 extract가 성공할 수 있습니다.
        // 하지만 효율적인 필터링을 위해 구현합니다.

        // 파일 확장자 추출
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == originalFilename.length() - 1) {
            // 확장자가 없거나 점으로 끝나면 일반 텍스트나 알 수 없는 파일로 간주 (Tika도 처리 가능성 있음)
            return true; // Tika가 시도하도록 허용
        }
        String extension = originalFilename.substring(dotIndex + 1).toLowerCase();

        // Tika가 잘 처리하는 문서/데이터 파일 확장자 목록
        List<String> supportedExtensions = Arrays.asList(
                "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt", "ods", "odp",
                "txt", "csv", "html", "xml", "json", "eml", "msg", "zip", "tar", "gz"
                // 이미지/오디오/비디오는 메타데이터 위주이므로, 필요에 따라 추가
        );
        return supportedExtensions.contains(extension);
    }
}

