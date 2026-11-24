package clients;

/** Только проверка полей: ИНН, имя (в т.ч. вариант 5: <= 30), email. Возвращает первую найденную причину. */
public class ClientValidator {
    public ValidationResult validate(ClientDraft d) {
        String inn = d.getInn();
        if (inn.isEmpty()) return ValidationResult.fail("ИНН не задан");
        for (int i = 0; i < inn.length(); i++) {
            char c = inn.charAt(i);
            if (c < '0' || c > '9') return ValidationResult.fail("ИНН должен состоять только из цифр: '" + inn + "'");
        }
        if (inn.length() != ImportRules.INN_LEGAL_LENGTH && inn.length() != ImportRules.INN_PERSON_LENGTH) {
            return ValidationResult.fail("ИНН должен содержать 10 или 12 цифр, получено " + inn.length());
        }
        if (d.getName().isEmpty()) return ValidationResult.fail("имя не задано");
        if (d.getName().length() > ImportRules.MAX_NAME_LENGTH) {
            return ValidationResult.fail("имя длиннее " + ImportRules.MAX_NAME_LENGTH + " символов (" + d.getName().length() + ")");
        }
        String reason = checkEmail(d.getEmail());
        return reason == null ? ValidationResult.ok() : ValidationResult.fail(reason);
    }

    private String checkEmail(String email) {
        int at = email.indexOf('@');
        if (at < 0) return "email не содержит @: '" + email + "'";
        if (email.indexOf('@', at + 1) >= 0) return "email содержит более одного @: '" + email + "'";
        if (at == 0) return "email: пустая часть слева от @";
        if (at == email.length() - 1) return "email: пустая часть справа от @";
        return null;
    }
}
