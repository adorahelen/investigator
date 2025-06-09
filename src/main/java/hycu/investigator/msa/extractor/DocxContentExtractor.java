//package hycu.investigator.msa.extractor;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import hycu.investigator.msa.extractor.ContentExtractor;
//import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//
//public class DocxContentExtractor implements ContentExtractor {
//
//    @Override
//    public String extract(Path filePath) throws IOException {
//        System.out.println("DOCX 텍스트 추출 중 (Apache POI): " + filePath);
//        try (InputStream is = Files.newInputStream(filePath);
//             XWPFDocument document = new XWPFDocument(is);
//             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
//            String extractedText = extractor.getText();
//            System.out.println("DOCX 텍스트 추출 완료. (추출된 텍스트 크기: " + extractedText.length() + " 바이트)");
//            return extractedText;
//        }
//    }
//
//    @Override
//    public boolean supports(Path filePath) {
//        String fileName = filePath.getFileName().toString().toLowerCase();
//        return fileName.endsWith(".docx");
//        // 또는 Files.probeContentType(filePath)를 사용하여 "application/vnd.openxmlformats-officedocument.wordprocessingml.document" 확인
//    }
//}