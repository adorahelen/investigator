

# **📂 현재 코드에서 처리 가능한 파일 포맷 목록 (`TikaUniversalContentExtractor`, `PlainTextFileContentExtractor`)**

## **1️⃣ `TikaUniversalContentExtractor` (Apache Tika 기반)**
Apache Tika의 `AutoDetectParser`를 활용하여 **자동으로 파일 타입을 감지**하고 해당 파서를 사용하여 **텍스트를 추출**합니다.  
따라서 **대부분의 일반적인 문서 및 텍스트 파일**이 처리 가능합니다.

### **📝 주요 지원 파일 목록**
#### **📑 Microsoft Office 문서**
- Word 문서: `.doc`, `.docx`
- Excel 스프레드시트: `.xls`, `.xlsx`
- PowerPoint 프레젠테이션: `.ppt`, `.pptx`
- Outlook 이메일: `.msg` (메타데이터 및 본문 포함)

#### **📜 PDF 문서**
- `.pdf` (텍스트 추출 가능)

#### **🖥️ OpenDocument 형식**
- 텍스트 문서: `.odt`
- 스프레드시트: `.ods`
- 프레젠테이션: `.odp`

#### **🌐 웹 관련 파일**
- HTML 문서: `.html`, `.htm`
- XML 문서: `.xml`

#### **📄 일반 텍스트 및 코드 파일**
- 일반 텍스트 파일: `.txt`, `.csv` (쉼표로 구분된 값)
- 프로그래밍 코드 파일: `.java`, `.py`, `.c`, `.cpp`, `.js` 등

#### **✉️ 이메일 형식**
- `.eml` (이메일 메시지 분석 가능)

#### **📦 압축 파일 (내부 파일의 텍스트 추출)**
- `.zip`, `.tar`, `.gz`  
  *(압축 파일 자체의 텍스트가 아닌, 압축 해제 후 내부 지원 파일의 텍스트를 추출)*

#### **🖼️ 다양한 이미지 파일**
- `.jpg`, `.png`, `.gif`, `.tiff` 등  
  *(메타데이터 추출 가능, 이미지 내 텍스트는 OCR 통합 필요)*

#### **🎵 오디오/비디오 파일**
- `.mp3`, `.mp4`, `.avi` 등  
  *(주로 메타데이터 추출 가능)*

### **🔹 요약**
- `TikaUniversalContentExtractor`는 **Apache Tika가 지원하는 거의 모든 문서 기반 파일 형식의 텍스트를 추출**할 수 있습니다.

---

## **2️⃣ `PlainTextFileContentExtractor` (기본 텍스트 처리)**
- **일반 텍스트 파일 (`.txt`)**.
- MIME 타입이 **`text/`**로 시작하는 파일 (`text/plain`, `text/html` 등).  
  *(단, Tika가 더 정교하게 처리 가능하므로 우선 처리될 가능성이 높음)*
- MIME 타입을 알 수 없는 파일 중, **파일 내용을 그대로 읽었을 때 텍스트인 파일**.

---

## **📌 코드 동작 방식**
- `PrivacyDetectionService`의 `contentExtractors` 리스트에서 **`TikaUniversalContentExtractor`가 우선적으로 처리**.
- `PlainTextFileContentExtractor`는 **MIME 타입 감지가 안 되거나, Tika가 처리하지 않는 단순한 텍스트 파일을 처리하는 "폴백(fallback)" 역할** 수행.
- Tika의 오버헤드 없이 순수 텍스트 파일을 빠르게 처리하고 싶을 때 사용 가능.

---

## **✅ 결론**
현재 코드에서 **다양한 종류의 문서, 텍스트, 그리고 기타 여러 미디어 파일의 메타데이터 및 텍스트 콘텐츠를 광범위하게 분석**할 수 있습니다.


###  주의사항:

- 성능: Tika의 AutoDetectParser는 매우 강력하지만, 파일 타입을 감지하고 파싱하는 과정에 약간의 오버헤드가 있을 수 있습니다. 아주 대량의 파일이나 매우 고성능이 요구되는 특정 시나리오에서는 각 파일 타입에 최적화된 개별 파서를 직접 사용하는 것이 유리할 수도 있습니다.
- 메모리: Tika는 파일을 메모리에 로드하여 파싱할 수 있으므로, 매우 큰 파일(수백 MB 이상)을 처리할 때는 메모리 부족(OOM) 문제가 발생할 수 있습니다. BodyContentHandler(-1)은 메모리 제한을 두지 않으므로, 이 부분을 주의해야 합니다. 필요에 따라 BodyContentHandler(int writeLimit)으로 제한을 두거나, Tika의 StreamingContentHandler를 사용하여 스트리밍 방식으로 처리하는 것을 고려할 수 있습니다.
