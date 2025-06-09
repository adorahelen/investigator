package hycu.investigator.msa.detector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// (기존 Callable): 단일 개인정보 패턴 탐지 로직.
public class PrivacyDetector implements Callable<List<String>> {
    private String text;
    private String patternName;
    private Pattern pattern; // 컴파일된 Pattern 객체

    public PrivacyDetector(String text, String patternName, String regex) {
        this.text = text;
        this.patternName = patternName;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public List<String> call() throws Exception {
        List<String> foundItems = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            foundItems.add(matcher.group());
        }

        Thread.sleep((long) (Math.random() * 50) + 10);
        return foundItems;
    }
}