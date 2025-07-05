    const activeTasks = {}; // 현재 진행 중인 작업을 관리할 객체 { taskId: { file: File, pollingInterval: intervalId } }
    const pollingIntervalTime = 2000; // 2초마다 폴링

    async function uploadFiles() {
    const fileInput = document.getElementById('fileInput');
    const files = fileInput.files;

    if (files.length === 0) {
    alert('파일을 선택해주세요.');
    return;
}

    for (let i = 0; i < files.length; i++) {
    const file = files[i];
    const taskId = await startFileUpload(file);
    if (taskId) {
    activeTasks[taskId] = { file: file, pollingInterval: null };
    addOrUpdateTaskUI(taskId, file.name, 'PENDING');
    activeTasks[taskId].pollingInterval = setInterval(() => checkTaskStatus(taskId), pollingIntervalTime);
}
}
    fileInput.value = ''; // 파일 선택 필드 초기화
}

    async function startFileUpload(file) {
    const formData = new FormData();
    formData.append('file', file);

    try {
    const response = await fetch('/api/detect/file', {
    method: 'POST',
    body: formData
});

    if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || response.statusText);
}

    const { taskId } = await response.json();
    console.log(`File '${file.name}' uploaded. Task ID:`, taskId);
    return taskId;
} catch (error) {
    console.error(`File upload failed for '${file.name}':`, error);
    alert(`파일 업로드 실패: ${file.name} - ${error.message}`);
    return null;
}
}

    async function checkTaskStatus(taskId) {
    try {
    const response = await fetch(`/api/detect/status/${taskId}`);
    if (!response.ok) {
    throw new Error(`작업 상태 조회 실패: ${response.statusText}`);
}
    const data = await response.json();
    const task = activeTasks[taskId];

    if (!task) { // 이미 UI에서 제거된 작업일 수 있음
    clearInterval(activeTasks[taskId]?.pollingInterval);
    return;
}

    addOrUpdateTaskUI(taskId, task.file.name, data.status, data.result, data.errorMessage);

    if (data.status === 'COMPLETED' || data.status === 'FAILED') {
    clearInterval(task.pollingInterval); // 작업 완료/실패 시 폴링 중지
    delete activeTasks[taskId]; // 활성 작업 목록에서 제거
}
} catch (error) {
    console.error(`Polling error for task ${taskId}:`, error);
    clearInterval(activeTasks[taskId]?.pollingInterval); // 오류 발생 시 폴링 중지
    addOrUpdateTaskUI(taskId, activeTasks[taskId]?.file.name || '알 수 없는 파일', 'FAILED', null, error.message);
    delete activeTasks[taskId];
}
}

    function addOrUpdateTaskUI(taskId, fileName, status, result = null, errorMessage = null) {
    const tasksListDiv = document.getElementById('tasksList');
    let taskItem = document.getElementById(`task-${taskId}`);

    if (!taskItem) {
    // 새로운 작업 항목 생성
    taskItem = document.createElement('div');
    taskItem.id = `task-${taskId}`;
    taskItem.classList.add('task-item');
    taskItem.innerHTML = `
                    <div class="task-header">
                        <span class="task-name">${fileName}</span>
                        <span class="task-status">${getStatusText(status)} ${status === 'PROCESSING' ? '<div class="spinner"></div>' : ''}</span>
                    </div>
                    <div class="task-results"></div>
                `;
    tasksListDiv.prepend(taskItem); // 최신 작업이 위에 오도록 추가
    const noResultsPara = tasksListDiv.querySelector('.no-results');
    if (noResultsPara) {
    noResultsPara.remove(); // "업로드된 파일이 없습니다." 메시지 제거
}
} else {
    // 기존 작업 항목 업데이트
    taskItem.querySelector('.task-status').innerHTML = `${getStatusText(status)} ${status === 'PROCESSING' ? '<div class="spinner"></div>' : ''}`;
}

    // 상태에 따라 클래스 추가 (색상 변경 등)
    taskItem.classList.remove('pending', 'processing', 'completed', 'failed');
    taskItem.classList.add(status.toLowerCase());

    if (status === 'COMPLETED') {
    displayTaskResults(taskItem, result);
} else if (status === 'FAILED') {
    taskItem.querySelector('.task-results').innerHTML = `<p class="error-message">탐지 실패: ${errorMessage || '알 수 없는 오류'}</p>`;
}
}

    function getStatusText(status) {
    switch (status) {
    case 'PENDING': return '대기 중';
    case 'PROCESSING': return '처리 중...';
    case 'COMPLETED': return '완료';
    case 'FAILED': return '실패';
    default: return '알 수 없음';
}
}

    function displayTaskResults(taskItem, data) {
    const resultsDiv = taskItem.querySelector('.task-results');
    resultsDiv.innerHTML = '';

    const categories = Object.keys(data);
    if (categories.length === 0) {
    resultsDiv.innerHTML = '<p class="no-results">어떤 개인정보도 탐지되지 않았습니다.</p>';
    return;
}

    let foundAny = false;
    for (const category of categories) {
    const items = data[category];
    if (items && items.length > 0) {
    foundAny = true;
    const categoryDiv = document.createElement('div');
    categoryDiv.classList.add('result-category');
    categoryDiv.innerHTML = `<h4>✅ ${category} 탐지됨:</h4>`;
    const ul = document.createElement('ul');
    items.forEach(item => {
    const li = document.createElement('li');
    li.textContent = item;
    ul.appendChild(li);
});
    categoryDiv.appendChild(ul);
    resultsDiv.appendChild(categoryDiv);
}
}

    if (!foundAny) {
    resultsDiv.innerHTML = '<p class="no-results">어떤 개인정보도 탐지되지 않았습니다.</p>';
}
}
