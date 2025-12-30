package SHAIF.controller;

import SHAIF.model.FormType;

public class ComboSystem {
    private FormType lastForm = FormType.CIRCLE;
    private int comboCount = 0;
    private long lastComboTime = 0;
    private static final long COMBO_TIMEOUT = 2_000_000_000L; // 2 giây

    public void recordShapeChange(FormType newForm) {
        long currentTime = System.nanoTime();
        
        // Reset combo nếu quá lâu
        if (currentTime - lastComboTime > COMBO_TIMEOUT) {
            comboCount = 0;
        }
        
        // Tăng combo nếu chuyển hình khác
        if (newForm != lastForm) {
            comboCount++;
            lastComboTime = currentTime;
        }
        
        lastForm = newForm;
    }

    public int getComboCount() {
        long currentTime = System.nanoTime();
        if (currentTime - lastComboTime > COMBO_TIMEOUT) {
            comboCount = 0;
        }
        return comboCount;
    }

    public void reset() {
        comboCount = 0;
        lastForm = FormType.CIRCLE;
        lastComboTime = 0;
    }
}

