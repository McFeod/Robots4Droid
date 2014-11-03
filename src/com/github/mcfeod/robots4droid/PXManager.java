package com.github.mcfeod.robots4droid;

/* Данный класс расширится при введении возможности зуммирования.
* Пока он служит только для уменьшения количества полей в DrawThread1
* */
public class PXManager {

    private static int WIDTH_PX_160_DPI      = 35;
    private static int HEIGHT_PX_160_DPI     = 35;
    private static int STEP_PX_160_DPI       = 10;
    private static int INTENT_PX_160_DPI     = 30;
    private static int LINE_WIDTH_PX_160_DPI = 10;

    public int cellWidth;
    public int cellHeight;
    public int step;
    public int indent;
    public int lineWidth;

    public int viewWidth;
    public int viewHeight;

    public void setValuesForDensity(float density){
        // Эти значения нужно будет привязать ещё и к размеру экрана, не только к разрешению
        cellWidth = (int)(WIDTH_PX_160_DPI      * density);
        cellHeight= (int)(HEIGHT_PX_160_DPI     * density);
        step      = (int)(STEP_PX_160_DPI       * density);
        indent    = (int)(INTENT_PX_160_DPI     * density);
        lineWidth = (int)(LINE_WIDTH_PX_160_DPI * density);
    }
}
