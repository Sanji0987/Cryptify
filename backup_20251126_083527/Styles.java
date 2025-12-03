/**
 * Centralized CSS styling for the CryptoDrop application.
 * All colors and styles in one place for easy theme customization.
 */
public class Styles {

    // ========== COLORS ==========

    // Backgrounds
    public static final String COLOR_BG_MAIN = "#f5f5f5";
    public static final String COLOR_BG_WHITE = "white";
    public static final String COLOR_BG_HOVER = "#fafafa";
    public static final String COLOR_BG_BUTTON_HOVER = "#f8f8f8";
    public static final String COLOR_BG_BUTTON = "#e0e0e0";

    // Borders
    public static final String COLOR_BORDER_LIGHT = "#e0e0e0";
    public static final String COLOR_BORDER_MEDIUM = "#d0d0d0";
    public static final String COLOR_BORDER_GRAY = "#ccc";
    public static final String COLOR_BORDER_DARK = "#999";
    public static final String COLOR_BORDER_ACTIVE = "#666";
    public static final String COLOR_BORDER_BUTTON = "#bebebe";

    // Text
    public static final String COLOR_TEXT_DARK = "#333";
    public static final String COLOR_TEXT_BLACK = "#000";
    public static final String COLOR_TEXT_GRAY = "#666";
    public static final String COLOR_TEXT_MEDIUM_GRAY = "#999";
    public static final String COLOR_TEXT_LIGHT = "#aaa";
    public static final String COLOR_TEXT_GTK = "#2e3436";

    // Status Colors
    public static final String COLOR_ERROR = "#cc0000";
    public static final String COLOR_INFO = "#3584e4";

    // ========== MAIN WINDOW ==========

    public static final String MAIN_CONTAINER = "-fx-padding: 15; -fx-background-color: " + COLOR_BG_MAIN + ";";

    public static final String LEFT_PANEL = "-fx-background-color: " + COLOR_BG_WHITE + "; " +
            "-fx-padding: 10; " +
            "-fx-border-color: " + COLOR_BORDER_MEDIUM + "; " +
            "-fx-border-width: 1;";

    public static final String FILE_LIST_LABEL = "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + COLOR_TEXT_GRAY + ";";

    public static final String FILE_LIST_VIEW = "-fx-background-color: " + COLOR_BG_WHITE + "; " +
            "-fx-border-color: " + COLOR_BORDER_LIGHT + "; " +
            "-fx-border-width: 1; " +
            "-fx-font-size: 11px;";

    // ========== DROP ZONE ==========

    public static final String DROP_ZONE_NORMAL = "-fx-background-color: " + COLOR_BG_WHITE + "; " +
            "-fx-border-color: " + COLOR_BORDER_DARK + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-style: dashed; " +
            "-fx-border-radius: 2;";

    public static final String DROP_ZONE_ACTIVE = "-fx-background-color: " + COLOR_BG_HOVER + "; " +
            "-fx-border-color: " + COLOR_BORDER_ACTIVE + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-style: dashed; " +
            "-fx-border-radius: 2;";

    public static final String DROP_LABEL = "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + COLOR_TEXT_MEDIUM_GRAY + ";";

    public static final String DROP_HINT = "-fx-font-size: 10px; " +
            "-fx-text-fill: " + COLOR_TEXT_LIGHT + ";";

    // ========== BUTTONS ==========

    public static final String BUTTON_NORMAL = "-fx-background-color: " + COLOR_BG_WHITE + "; " +
            "-fx-text-fill: " + COLOR_TEXT_DARK + "; " +
            "-fx-border-color: " + COLOR_BORDER_GRAY + "; " +
            "-fx-border-width: 1; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: normal; " +
            "-fx-cursor: hand;";

    public static final String BUTTON_HOVER = "-fx-background-color: " + COLOR_BG_BUTTON_HOVER + "; " +
            "-fx-text-fill: " + COLOR_TEXT_BLACK + "; " +
            "-fx-border-color: " + COLOR_BORDER_DARK + "; " +
            "-fx-border-width: 1; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: normal; " +
            "-fx-cursor: hand;";

    public static final String BUTTON_CONTAINER = "-fx-padding: 10 0 0 0;";

    // ========== DIALOGS ==========

    public static final String DIALOG_CONTAINER = "-fx-background-color: " + COLOR_BG_MAIN + ";";

    public static final String DIALOG_PADDING = "-fx-padding: 20;";

    public static final String DIALOG_TITLE = "-fx-font-size: 14px; " +
            "-fx-font-weight: bold;";

    public static final String DIALOG_ICON_BASE = "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 40px; " +
            "-fx-alignment: center;";

    public static final String DIALOG_ICON_ERROR = DIALOG_ICON_BASE + "-fx-text-fill: " + COLOR_ERROR + ";";

    public static final String DIALOG_ICON_INFO = DIALOG_ICON_BASE + "-fx-text-fill: " + COLOR_INFO + ";";

    public static final String DIALOG_MESSAGE = "-fx-font-size: 13px; " +
            "-fx-fill: " + COLOR_TEXT_GTK + ";";

    public static final String DIALOG_BUTTON_NORMAL = "-fx-background-color: " + COLOR_BG_BUTTON + "; " +
            "-fx-text-fill: " + COLOR_TEXT_GTK + "; " +
            "-fx-border-color: " + COLOR_BORDER_BUTTON + "; " +
            "-fx-border-width: 1; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-cursor: hand;";

    public static final String DIALOG_BUTTON_HOVER = "-fx-background-color: #d0d0d0; " +
            "-fx-text-fill: " + COLOR_TEXT_GTK + "; " +
            "-fx-border-color: " + COLOR_BORDER_BUTTON + "; " +
            "-fx-border-width: 1; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-cursor: hand;";
}
