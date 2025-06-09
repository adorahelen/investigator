//package hycu.investigator.msa.extractor;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import org.apache.tika.exception.TikaException;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.parser.AutoDetectParser;
//import org.apache.tika.parser.ParseContext;
//import org.apache.tika.sax.BodyContentHandler;
//import org.xml.sax.SAXException;
//
//public class PdfContentExtractor implements ContentExtractor {
//
//    @Override
//    public String extract(Path filePath) throws IOException {
//        System.out.println("PDF 텍스트 추출 중 (Tika): " + filePath);
//        try (InputStream input = Files.newInputStream(filePath)) {
//            BodyContentHandler handler = new BodyContentHandler(-1); // -1은 메모리 제한 없음
//            Metadata metadata = new Metadata();
//            ParseContext context = new ParseContext();
//
//            AutoDetectParser parser = new AutoDetectParser();
//            parser.parse(input, handler, metadata, context);
//            String extractedText = handler.toString();
//            System.out.println("PDF 텍스트 추출 완료. (추출된 텍스트 크기: " + extractedText.length() + " 바이트)");
//            return extractedText;
//        } catch (TikaException | SAXException e) {
//            throw new IOException("PDF 텍스트 추출 중 오류 발생", e);
//        }
//    }
//
//    @Override
//    public boolean supports(Path filePath) {
//        try {
//            String mimeType = Files.probeContentType(filePath);
//            return "application/pdf".equals(mimeType);
//        } catch (IOException e) {
//            return false;
//        }
//    }
//}