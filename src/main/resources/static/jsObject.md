
### 1\. Document 객체 (DOM - Document Object Model)

`document` 객체는 \*\*DOM(Document Object Model)\*\*의 핵심입니다.

* **역할**: 웹 페이지의 **HTML 콘텐츠 자체**를 나타냅니다. 웹 페이지의 모든 요소(태그, 속성, 텍스트)는 `document` 객체의 자식으로 구성되며, JavaScript를 통해 이 요소를 생성, 조작, 삭제할 수 있습니다.
* **BOM과의 차이**: `Window` 객체가 브라우저 '창' 자체와 그 환경(URL, 히스토리, 화면 크기 등)을 제어하는 반면, `document` 객체는 `Window` 객체 아래에 속하면서 **웹 페이지의 내용(HTML, XML)에 대한 구조화된 표현**을 담당합니다. 웹 페이지에 표시되는 모든 내용이 `document` 객체를 통해 접근되고 변경됩니다.
* **예시**:
  ```javascript
  console.log(document.title); // 현재 문서의 제목
  const myDiv = document.getElementById('myId'); // ID로 요소 선택
  document.createElement('p'); // 새로운 단락 요소 생성
  ```

### 2\. Console 객체 (Web API)

`console` 객체는 주로 **디버깅 목적**으로 사용되는 객체입니다.

* **역할**: 웹 브라우저의 개발자 도구에 있는 \*\*콘솔(Console)\*\*에 메시지를 출력하거나, 코드 실행 시간 측정, 객체 내용 확인 등 디버깅 및 개발 과정에서 유용한 기능을 제공합니다.
* **BOM과의 차이**: `console` 객체는 브라우저 자체의 기능이라기보다는 **웹 개발자가 코드를 테스트하고 문제를 해결하기 위해 제공되는 도구**에 가깝습니다. 역시 `window` 객체의 속성으로 접근 가능하지만, 브라우저 창이나 페이지 콘텐츠 조작과는 직접적인 관련이 적습니다. 웹 표준에서는 Web API의 한 부분으로 간주됩니다.
* **주요 메서드**:
    * `console.log()`: 일반 메시지 출력
    * `console.warn()`: 경고 메시지 출력
    * `console.error()`: 에러 메시지 출력
    * `console.info()`: 정보성 메시지 출력
    * `console.dir()`: 객체의 속성을 트리 형태로 출력
    * `console.time()`, `console.timeEnd()`: 코드 실행 시간 측정
* **예시**:
  ```javascript
  console.log('변수 값:', someVariable);
  console.error('오류 발생:', errorObject);
  console.time('루프 실행 시간');
  for (let i = 0; i < 1000; i++) { /* do something */ }
  console.timeEnd('루프 실행 시간');
  ```

-----

### 📌 최종 요약 및 분류

정리하자면, 자바스크립트가 브라우저에서 사용할 수 있는 환경 객체들은 다음과 같이 크게 세 가지로 분류될 수 있습니다:

1.  **브라우저 객체 모델 (BOM - Browser Object Model)**:

    * **`Window`**: 브라우저 창 전체를 제어하는 최상위 객체.
    * **`Navigator`**: 브라우저 자체 정보 (종류, 버전 등).
    * **`Screen`**: 사용자 화면 정보.
    * **`History`**: 브라우저 방문 기록.
    * **`Location`**: 현재 URL 정보.
    * 이들은 브라우저 '창'과 그 주변 환경에 대한 접근을 제공합니다.

2.  **문서 객체 모델 (DOM - Document Object Model)**:

    * **`Document`**: HTML/XML 문서의 내용을 제어하고 조작하는 객체. 웹 페이지에 표시되는 모든 콘텐츠에 대한 프로그래밍 인터페이스를 제공합니다.

3.  **기타 Web API (Web Application Programming Interface)**:

    * **`Console`**: 개발자 도구 콘솔에 정보를 출력하기 위한 디버깅용 객체.
    * **`XMLHttpRequest` / `fetch`**: 서버와 비동기 통신 (Ajax)을 위한 API.
    * **`localStorage` / `sessionStorage`**: 브라우저에 데이터 저장.
    * **`Geolocation API`**: 사용자의 지리적 위치 정보.
    * 이들은 브라우저가 웹 애플리케이션에 제공하는 특정 기능을 수행하는 인터페이스입니다.

