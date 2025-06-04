
### Tika와 Tesseract를 이용한 OCR 과정

1.  **Tesseract OCR 엔진 설치:**
    가장 먼저 시스템에 Tesseract OCR 엔진을 설치해야 합니다. Tika는 Tesseract의 실행 파일을 호출하여 OCR을 수행하므로, Tesseract가 시스템 PATH에 등록되어 있거나 Tika 설정에서 Tesseract의 경로를 명시해 주어야 합니다.
    * **macOS:** `brew install tesseract`
    * **Windows:** [Tesseract GitHub 저장소](https://tesseract-ocr.github.io/tessdoc/Downloads.html)에서 설치 파일을 다운로드하여 설치.
    * **Linux:** `sudo apt-get install tesseract-ocr` (Debian/Ubuntu)
    * 또한, 인식하고자 하는 언어 팩(예: `tessdata/eng.traineddata`, `tessdata/kor.traineddata`)도 설치해야 합니다.

2.  **아파치 티카 의존성 추가:**
    `tika-parser-scientific-package` 또는 `tika-parser-text-package` (Tika 2.x) 또는 특정 `tika-parser-image-package` (Tika 1.x)에 OCR 관련 파서가 포함될 수 있으며, 가장 간단하게는 `tika-parsers-standard-package`에 포함된 기능을 활용합니다. 그러나 Tesseract를 명시적으로 사용하려면 추가적인 설정이 필요할 수 있습니다.

    **Gradle (`build.gradle`) 예시:**
    ```gradle
    dependencies {
        // 기존 Tika 의존성 유지
        implementation 'org.apache.tika:tika-core:2.9.2'
        implementation 'org.apache.tika:tika-parsers-standard-package:2.9.2'

        // OCR을 위한 Tika-Tesseract 모듈 (필요시 명시적 추가)
        // Tika 2.x부터는 tika-parsers-standard-package에 포함되어 있을 가능성이 높지만,
        // 명시적으로 제어하거나 문제가 발생할 경우 확인
        // Tika 1.x에서는 tika-parsers 모듈에 포함되어 있었음. 2.x에서는 대부분 표준 파서 패키지에 통합됨.
        // 특정 이미지 포맷 파싱을 위해 필요할 수 있는 플러그인 (예: ImageIO)
        // implementation 'com.twelvemonkeys.imageio:imageio-jpeg:3.9.4'
        // implementation 'com.twelvemonkeys.imageio:imageio-tiff:3.9.4'
        // ... (다른 필요한 이미지 포맷)
    }
    ```

3.  **Tika 설정 파일 (tika-config.xml) 구성:**
    Tika가 Tesseract를 사용하도록 지시하려면, `tika-config.xml` 파일을 사용하여 `TesseractOCRConfig`를 구성해야 합니다. 이 파일은 클래스패스 상에 있어야 합니다.

    **`tika-config.xml` 예시:**
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <properties>
        <parsers>
            <parser class="org.apache.tika.parser.DefaultParser">
                <mime-exclude>image/x-emf</mime-exclude>
            </parser>
            <parser class="org.apache.tika.parser.image.TesseractOCRParser">
                <mime-include>image/jpeg</mime-include>
                <mime-include>image/png</mime-include>
                <mime-include>image/tiff</mime-include>
                <mime-include>application/pdf</mime-include> </parser>
        </parsers>

        <service-loader loadUnsecure="true" />

        <parser-resources>
            <parser-resource class="org.apache.tika.parser.image.TesseractOCRParser">
                <param name="language" type="string" value="kor+eng"/> <param name="tesseractPath" type="string" value="/usr/local/bin/"/> <param name="tessdataPath" type="string" value="/usr/local/share/tessdata"/> <param name="timeout" type="int" value="120"/> <param name="density" type="int" value="300"/> <param name="pageSegMode" type="string" value="3"/> </parser-resource>
        </parser-resources>
    </properties>
    ```
    이 `tika-config.xml` 파일을 프로젝트의 `src/main/resources` 폴더에 넣으면 Tika가 자동으로 로드합니다.

4.  **자바 코드에서 Tika 사용:**
    자바 코드에서는 기존처럼 `AutoDetectParser`를 사용하여 파일을 파싱합니다. Tika는 `tika-config.xml` 설정에 따라 자동으로 TesseractOCRParser를 사용하여 이미지나 PDF 내 이미지에서 텍스트를 추출하려고 시도합니다.

    ```java
    // ... (기존 Tika 관련 import)
    import org.apache.tika.config.TikaConfig; // TikaConfig를 로드하기 위한 import

    // ... (main 메서드 내 파일 파싱 부분)
    String fileContent = "";
    try (InputStream input = Files.newInputStream(filePath)) {
        // TikaConfig를 로드하여 OCR 설정을 적용
        // tika-config.xml 파일이 src/main/resources에 있다면 자동으로 로드됨
        // 또는 특정 경로에서 로드: TikaConfig config = new TikaConfig(new File("path/to/tika-config.xml"));
        TikaConfig config = new TikaConfig();
        AutoDetectParser parser = new AutoDetectParser(config); // 설정된 파서를 사용

        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        // TesseractOCRConfig를 ParseContext에 추가하여 파싱 중 Tesseract 설정 사용
        TesseractOCRConfig tesseractConfig = new TesseractOCRConfig();
        tesseractConfig.setLanguage("kor+eng"); // 코드에서 언어 설정
        // tesseractConfig.setTesseractPath("/usr/local/bin/"); // 코드에서 Tesseract 경로 설정 (tika-config.xml과 중복될 수 있음)
        context.set(tesseractConfig);

        parser.parse(input, handler, metadata, context);
        fileContent = handler.toString();
        System.out.println("Tika + Tesseract로 텍스트 추출 완료. (추출된 텍스트 크기: " + fileContent.length() + " 바이트)");

        // PDF 메타데이터 (Tika가 OCR 결과 외에 PDF 자체의 메타데이터도 추출)
        // System.out.println("Extracted Metadata: " + metadata);

    } catch (IOException | TikaException | SAXException e) {
        System.err.println("오류: Tika + Tesseract 텍스트 추출 중 문제 발생 - " + e.getMessage());
        // ... 오류 처리
    }
    // ... (이후 개인정보 탐지 로직)
    ```

### 주요 고려사항:

* **Tesseract 설치 및 PATH:** 가장 중요한 것은 Tesseract가 시스템에 올바르게 설치되고 Tika가 실행 파일을 찾을 수 있도록 PATH가 설정되거나 `tika-config.xml`에 명시되어야 한다는 점입니다.
* **언어 팩:** 인식할 언어에 해당하는 `tessdata` 파일이 Tesseract 설치 경로의 `tessdata` 폴더에 있어야 합니다. `kor.traineddata`, `eng.traineddata` 등.
* **성능:** OCR은 CPU 집약적인 작업이며, 이미지 품질에 따라 시간이 오래 걸릴 수 있습니다. 대량의 파일이나 고해상도 이미지 처리 시 성능 문제를 고려해야 합니다.
* **정확도:** OCR의 정확도는 이미지의 품질, 폰트, 배경 등에 크게 영향을 받습니다. 추출된 텍스트의 정확도가 항상 100%는 아니므로, 개인정보 탐지 결과도 그에 따라 달라질 수 있습니다.
* **`tika-config.xml` vs 코드:** Tesseract 설정은 `tika-config.xml`에서 하는 것이 일반적이지만, 필요에 따라 `TesseractOCRConfig` 객체를 직접 생성하여 `ParseContext`에 넣어줄 수도 있습니다. 두 방법 중 하나만 사용하거나, `tika-config.xml`로 기본 설정을 하고 코드에서 동적으로 오버라이드할 수도 있습니다.

이러한 단계를 거치면 Tika를 통해 PDF 내부의 이미지화된 텍스트까지 OCR로 처리하여 개인정보 탐지의 범위를 넓힐 수 있습니다.