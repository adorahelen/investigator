package hycu.investigator.msa.domain;

public class PrivacyPattern { // 개인정보 패턴 정의
    private String name;
    private String regex;

    public PrivacyPattern(String name, String regex) {
        this.name = name;
        this.regex = regex;
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }
}