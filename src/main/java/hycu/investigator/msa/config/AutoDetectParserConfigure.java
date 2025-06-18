package hycu.investigator.msa.config;

import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AutoDetectParserConfigure {


    @Bean
    public ExecutorService tikaExecutorService() {
        return new ThreadPoolExecutor(
                5,                      // core pool size
                10,                     // max pool size
                60L,                    // keep alive time
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20) // task queue
        );
    }

    // [OCR 파서라서, 테서렉트 설치 한 이후에 패스 넣어야 에러 안남]

//    @Bean
//    public AutoDetectParser autoDetectParser() throws TikaConfigException {
//        AutoDetectParser parser = new AutoDetectParser();
//
//        configOCRParser(parser);
//
//        return parser;
//    }

//    private void configOCRParser(AutoDetectParser detectParser) throws TikaConfigException {
//        TesseractOCRParser tesseractParser = configTesseractOCRParser(detectParser);
//        // tesseractParser.setSkipOCR(true); // 필요시 사용
//    }

//    private TesseractOCRParser configTesseractOCRParser(AutoDetectParser detectParser) throws TikaConfigException {
//        TesseractOCRParser tesseractParser = new TesseractOCRParser();
//
//        tesseractParser.setImageMagickPath("C:\\data\\ImageMagick-7.1.1");
//        tesseractParser.setEnableImagePreprocessing(true);
//        tesseractParser.setTesseractPath("C:\\data\\Tesseract-OCR");
//        tesseractParser.setLanguage("kor_lstm_best+eng_lstm_best");
//        tesseractParser.setPageSegMode("4");
//
//        setMediaType(detectParser, tesseractParser);
//        tesseractParser.initialize(null);
//
//        return tesseractParser;
//    }

//    private void setMediaType(AutoDetectParser detectParser, Parser ocrParser) {
//        Map<MediaType, Parser> parsers = detectParser.getParsers();
//
//        parsers.put(MediaType.image("png"), ocrParser);
//        parsers.put(MediaType.image("bmp"), ocrParser);
//        parsers.put(MediaType.image("gif"), ocrParser);
//        parsers.put(MediaType.image("jpeg"), ocrParser);
//        parsers.put(MediaType.image("jpg"), ocrParser);
//        parsers.put(MediaType.image("tif"), ocrParser);
//        parsers.put(MediaType.image("tiff"), ocrParser);
//
//        detectParser.setParsers(parsers);
//    }
}
