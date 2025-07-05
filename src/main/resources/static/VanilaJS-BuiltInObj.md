
-----

## JavaScript 내장 객체(Built-in Object) 및 문법 정리

제공해주신 JavaScript 코드에 사용된 내장 객체(Built-in Object)와 관련 문법을 각 객체의 목적과 함께 정리했습니다.

-----

### ✅ 1. **FormData**

* **역할**: 파일 업로드 시 폼 데이터를 구성하는 데 사용됩니다.

  ```javascript
  const formData = new FormData();
  formData.append('file', file);
  ```

* **설명**:

    * `FormData`는 `<form>`의 필드들을 자동으로 수집하거나 JavaScript로 직접 추가할 수 있습니다.
    * 파일뿐만 아니라 텍스트, 키-값 쌍도 지원합니다.

-----

### ✅ 2. **fetch()**

* **역할**: 비동기 HTTP 요청을 전송하는 데 사용됩니다 (Ajax 대체).

  ```javascript
  const response = await fetch('/api/detect/file', {
      method: 'POST',
      body: formData
  });
  ```

* **설명**:

    * 내장 함수이며 **Promise**를 반환합니다.
    * 응답 객체는 **Response** 타입이며, `.ok`, `.json()`, `.status` 등을 통해 정보에 접근할 수 있습니다.

-----

### ✅ 3. **Promise / async / await**

* **역할**: 비동기 작업을 직관적인 동기 코드처럼 작성할 수 있도록 돕습니다.

  ```javascript
  async function uploadFiles() { ... }
  await fetch(...);
  ```

* **설명**:

    * `async` 함수는 항상 **Promise**를 반환합니다.
    * `await`는 **Promise**가 해결(resolve)될 때까지 대기합니다.
    * 예외 처리를 위해 `try/catch`와 함께 자주 사용됩니다.

-----

### ✅ 4. **setInterval() / clearInterval()**

* **역할**: 일정 시간마다 작업을 반복 실행하거나 이를 중지하는 데 사용됩니다.

  ```javascript
  activeTasks[taskId].pollingInterval = setInterval(() => checkTaskStatus(taskId), pollingIntervalTime);
  clearInterval(task.pollingInterval);
  ```

* **설명**:

    * `setInterval(fn, ms)`: `fn` 함수를 `ms` 밀리초마다 반복 실행합니다.
    * `clearInterval(id)`: `setInterval`로 시작된 반복을 종료합니다.

-----

### ✅ 5. **DOM API (document.getElementById, createElement, querySelector 등)**

* **역할**: HTML 요소를 선택하고 조작하는 데 사용됩니다.

  ```javascript
  const fileInput = document.getElementById('fileInput');
  const tasksListDiv = document.getElementById('tasksList');
  taskItem = document.createElement('div');
  tasksListDiv.prepend(taskItem);
  ```

* **메서드 설명**:

    * `getElementById()`: `ID`로 요소를 선택합니다.
    * `querySelector()`: `CSS` 셀렉터로 요소를 선택합니다.
    * `createElement()`: 새로운 요소를 생성합니다 (`<div>`, `<ul>` 등).
    * `prepend()`: 자식 요소를 부모 요소의 맨 앞에 삽입합니다.
    * `innerHTML`: 요소의 HTML 내용을 읽거나 씁니다.
    * `classList.add() / remove()`: 요소의 클래스를 추가하거나 제거합니다.

-----

### ✅ 6. **Object.keys()**

* **역할**: 객체의 키(속성 이름) 목록을 배열로 반환합니다.

  ```javascript
  const categories = Object.keys(data);
  ```

* **설명**:

    * `data`가 `{ "전화번호": [...], "이메일": [...] }`와 같다면, `["전화번호", "이메일"]`을 반환합니다.

-----

### ✅ 7. **배열 관련 메서드 (forEach, push 등)**

* **역할**: 탐지된 항목을 반복하고 출력하는 데 사용됩니다.

  ```javascript
  items.forEach(item => {
      const li = document.createElement('li');
      li.textContent = item;
      ul.appendChild(li);
  });
  ```

* **설명**:

    * `forEach`: 배열의 각 요소를 순회하며 콜백 함수를 실행합니다.
    * `push`는 위 코드에 직접 사용되지 않았지만, `appendChild`로 유사한 `DOM` 조작을 수행합니다.

-----

### ✅ 8. **템플릿 리터럴 (\`${변수}\`)**

* **역할**: 문자열 내에 변수나 표현식을 쉽게 삽입할 수 있도록 합니다.

  ```javascript
  `<h4>✅ ${category} 탐지됨:</h4>`
  ```

* **설명**:

    * 백틱(`` ` ``)을 사용하여 문자열을 정의하며, 문자열 내에서 `${}` 문법으로 변수를 삽입할 수 있습니다.
    * 여러 줄 문자열도 지원합니다.

-----

### ✅ 9. **조건 연산자 (?:)**

* **역할**: 조건에 따라 두 값 중 하나를 선택하여 반환합니다.

  ```javascript
  status === 'PROCESSING' ? '<div class="spinner"></div>' : ''
  ```

* **설명**:

    * 조건이 참(`true`)이면 `:` 앞의 값을 반환하고, 거짓(`false`)이면 `:` 뒤의 값을 반환합니다.

-----

### ✅ 10. **선택적 체이닝 (?.)**

* **역할**: 객체의 속성에 접근할 때, 해당 속성이 `undefined` 또는 `null`인 경우에도 에러 없이 안전하게 접근할 수 있도록 돕습니다.

  ```javascript
  activeTasks[taskId]?.pollingInterval
  ```

* **설명**:

    * 객체가 `undefined` 또는 `null`이더라도 에러 없이 안전하게 접근할 수 있습니다.
    * `ES2020`에 도입된 문법입니다.

-----

### 🧾 정리: 사용된 주요 내장 객체 & 메서드

| 내장 객체/메서드              | 설명                       |
| :-------------------------- | :------------------------- |
| `FormData`                  | 파일 및 폼 데이터 전송 객체  |
| `fetch()`                   | HTTP 요청 보내기           |
| `Response.json()`           | 응답을 `JSON`으로 파싱       |
| `setInterval`, `clearInterval` | 주기적 실행/취소             |
| `document.getElementById`   | `DOM` 요소 선택            |
| `createElement`, `prepend`, `innerHTML`, `classList` | `DOM` 조작                 |
| `Object.keys()`             | 객체 키 추출               |
| `Array.forEach()`           | 배열 순회                  |
| 템플릿 리터럴                 | 백틱(`` ` ``)을 이용한 문자열 + 변수 |
| 선택적 체이닝 (`?.`)        | 안전한 객체 속성 접근        |

-----

이 내장 객체들을 잘 익히면 **DOM 조작**, **비동기 처리**, **서버 통신**까지 대부분의 프론트엔드 로직을 구성할 수 있습니다.

더 심화된 내용(예: `AbortController`, `FileReader`, `Blob`, `URL.createObjectURL`)도 원하시면 알려주세요\!