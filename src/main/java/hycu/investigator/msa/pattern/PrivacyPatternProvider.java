package hycu.investigator.msa.pattern;

import hycu.investigator.msa.domain.PrivacyPattern;

import java.util.ArrayList;
import java.util.List;

public class PrivacyPatternProvider {

    public List<PrivacyPattern> getAllPatterns() {
        List<PrivacyPattern> patterns = new ArrayList<>();

        // ----------------------------------------------------
        // 1. 대한민국 개인정보 패턴 (추가/수정)
        // ----------------------------------------------------

        // 주민등록번호 (Resident Registration Number): YYMMDD-SLNNNNN
        // 000000-0000000 (간단한 예시), 실제로는 더 복잡한 유효성 검사 필요하나 정규식으로는 형식만
        // '-' 하이픈은 포함될 수도 있고 없을 수도 있음
        patterns.add(new PrivacyPattern("Korean Resident Registration Number",
                "\\b(?:\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1-2]\\d|3[0-1]))\\-?[1-4]\\d{6}\\b"));

        // 휴대폰 번호 (Mobile Phone Number): 01X-YYYY-ZZZZ (하이픈 유무, 띄어쓰기 유무)
        patterns.add(new PrivacyPattern("Korean Mobile Phone Number",
                "\\b(010|011|016|017|018|019)[\\s\\-]?(\\d{3,4})[\\s\\-]?(\\d{4})\\b"));

        // 일반 전화번호 (Landline Phone Number): 지역번호-국번-번호
        // 02-YYYY-ZZZZ, 03X-YYYY-ZZZZ, 05X-YYYY-ZZZZ 등 (대표적으로 02, 031, 051 등)
        patterns.add(new PrivacyPattern("Korean Landline Phone Number",
                "\\b(02|0[3-6][1-5]|070|080|064)[\\s\\-]?(\\d{3,4})[\\s\\-]?(\\d{4})\\b"));

        // 계좌번호 (Bank Account Number): 은행별로 자릿수가 다양함. 특정 은행 형식 또는 일반적인 숫자 나열
        // 은행별 특정 패턴 (예: Kookmin Bank 123456-12-123456 또는 1234567890123)
        // 여기서는 가장 일반적인 숫자 나열 형태 (최소 10자리 ~ 최대 16자리, 하이픈 포함/미포함)
        // 더 정밀한 탐지를 위해선 각 은행별 상세 패턴이 필요
        patterns.add(new PrivacyPattern("Korean Bank Account Number",
                "\\b\\d{3,6}\\-?\\d{2,6}\\-?\\d{4,8}\\b|\\b\\d{10,16}\\b")); // 10-16자리 숫자 또는 하이픈 포함 형식

        // 여권번호 (Passport Number): M으로 시작하는 8자리 숫자 (알파벳 M 또는 S + 숫자)
        // 대한민국 여권은 M 또는 S로 시작하고 8자리의 숫자가 뒤따릅니다. (예: M12345678)
        patterns.add(new PrivacyPattern("Korean Passport Number",
                "\\b[MS]\\d{8}\\b"));

        // 운전면허번호 (Driver's License Number): 12-34-123456-12 (지역코드-발급년도-일련번호-체크섬)
        // 형식: 숫자2자리-숫자2자리-숫자6자리-숫자2자리
        patterns.add(new PrivacyPattern("Korean Driver's License Number",
                "\\b\\d{2}\\-?\\d{2}\\-?\\d{6}\\-?\\d{2}\\b"));

        // 사업자등록번호 (Business Registration Number): 123-45-67890 (3-2-5 형식)
        patterns.add(new PrivacyPattern("Korean Business Registration Number",
                "\\b\\d{3}\\-?\\d{2}\\-?\\d{5}\\b"));

        // ----------------------------------------------------
        // 2. 기타 일반적인 개인정보 패턴 (기존 유지 또는 필요에 따라 추가)
        // ----------------------------------------------------

        // 이메일 주소 (Email Address)
        patterns.add(new PrivacyPattern("Email Address",
                "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b"));

        // IP 주소 (IP Address)
        patterns.add(new PrivacyPattern("IP Address",
                "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"));

        // 신용카드 번호 (Credit Card Number) - 일반적인 16자리 (대한민국에만 해당X)
        // 4000-1234-5678-9010 또는 4000123456789010
        patterns.add(new PrivacyPattern("Credit Card Number",
                "\\b(?:4\\d{3}|5[1-5]\\d{2}|6011|622\\d|64[4-9]\\d|65\\d{2})[\\s\\-]?(\\d{4})[\\s\\-]?(\\d{4})[\\s\\-]?(\\d{4})\\b"));
        // 비자(4), 마스터(51-55), 디스커버(6011, 644-649, 65), 중국 은련(622) 시작 번호 고려

        // 비밀번호 (Password) - "password:", "pass:", "pwd:" 키워드 근처
        // 이 패턴은 주변 텍스트에 따라 오탐이 많을 수 있으므로 주의해서 사용
        patterns.add(new PrivacyPattern("Potential Password",
                "\\b(?:password|pass|pwd|비밀번호)[:=\\s]*([\\w\\d\\!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]{4,})\\b"));


        return patterns;
    }
}