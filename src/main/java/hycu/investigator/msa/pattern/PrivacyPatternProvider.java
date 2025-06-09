package hycu.investigator.msa.pattern;

import hycu.investigator.msa.domain.PrivacyPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrivacyPatternProvider {

    private final List<PrivacyPattern> patterns;

    public PrivacyPatternProvider() {
        // 개인정보 패턴 정의
        List<PrivacyPattern> tempPatterns = new ArrayList<>();
        tempPatterns.add(new PrivacyPattern("Phone Number", "\\b01[016789][-.\\s]?\\d{3,4}[-.\\s]?\\d{4}\\b|\\b02[-.\\s]?\\d{3,4}[-.\\s]?\\d{4}\\b"));
        tempPatterns.add(new PrivacyPattern("Email Address", "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\\b"));
        tempPatterns.add(new PrivacyPattern("Korean SSN (Dummy)", "\\b\\d{6}-[1-4]\\d{6}\\b"));
        tempPatterns.add(new PrivacyPattern("Credit Card (Dummy)", "\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9]{2})[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})\\b"));
        this.patterns = Collections.unmodifiableList(tempPatterns); // 외부에서 수정 불가하도록
    }

    public List<PrivacyPattern> getAllPatterns() {
        return patterns;
    }
}