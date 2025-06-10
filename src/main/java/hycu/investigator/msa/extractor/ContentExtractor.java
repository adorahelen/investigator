package hycu.investigator.msa.extractor;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

//public interface ContentExtractor {
//    String extract(Path filePath) throws IOException;
//    boolean supports(Path filePath); // 특정 파일 경로를 지원하는지 여부
//}

// 모듈 to Spring, 경로가 아닌 스트림을 받도록 수정
public interface ContentExtractor {
    // 파일 내용 추출 메서드: InputStream과 원본 파일명을 받음
    String extract(InputStream is, String originalFilename) throws IOException;
    // 해당 추출기가 주어진 파일명을 지원하는지 여부 (확장자 기반 판단)
    boolean supports(String originalFilename);
}