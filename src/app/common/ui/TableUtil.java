package app.common.ui;

/**
 * 120자 콘솔 너비에 최적화된 표 형식 출력 유틸리티 클래스입니다.
 * Windows CMD 환경에서 안정적으로 동작하도록 설계되었습니다.
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 단일 행 표 출력
 * String[] headers = {"ID", "Name", "Age"};
 * String[] values = {"001", "John", "25"};
 * TableUtil.printSingleRowTable("User Information", headers, values);
 * 
 * // 다중 행 표 출력
 * String[] headers = {"Product", "Price", "Stock"};
 * String[][] data = {
 *     {"Apple", "$1.50", "100"},
 *     {"Banana", "$0.80", "150"},
 *     {"Orange", "$2.00", "80"}
 * };
 * TableUtil.printTable("Product List", headers, data);
 * 
 * // 리스트를 이용한 다중 행 표 출력
 * List&lt;String[]&gt; dataList = Arrays.asList(
 *     new String[]{"Item1", "Value1"},
 *     new String[]{"Item2", "Value2"}
 * );
 * TableUtil.printMultiRowTable("Items", new String[]{"Item", "Value"}, dataList);
 * </pre>
 * 
 * <h3>특징:</h3>
 * <ul>
 * <li>모든 컬럼이 15자 고정 폭으로 정렬됩니다.</li>
 * <li>한글과 영문 혼용 시 올바른 정렬을 제공합니다.</li>
 * <li>Windows CMD 환경에서 최적화되어 있습니다.</li>
 * <li>최대 8개 컬럼까지 지원합니다.</li>
 * </ul>
 */
public final class TableUtil {
    
    /** 최대 지원 컬럼 수 */
    private static final int MAX_COLUMNS = 8;
    
    /** 고정 컬럼 너비 (15자) */
    private static final int FIXED_COLUMN_WIDTH = 15;
    
    /**
     * private 생성자로 인스턴스 생성을 방지합니다.
     */
    private TableUtil() {
    }
    
    /**
     * 단일 행 표를 출력합니다.
     * 
     * @param title 표 제목
     * @param headers 헤더 배열
     * @param values 값 배열
     */
    public static void printSingleRowTable(String title, String[] headers, String[] values) {
        printTable(title, headers, new String[][]{values});
    }
    
    /**
     * 범용 표 출력 메서드입니다.
     * 
     * @param title 표 제목
     * @param headers 헤더 배열 (최대 8개)
     * @param data 데이터 2차원 배열
     */
    public static void printTable(String title, String[] headers, String[][] data) {
        if (headers.length > MAX_COLUMNS) {
            System.out.println("  오류: 최대 " + MAX_COLUMNS + "개 컬럼까지 지원됩니다.");
            return;
        }
        
        // 고정 너비 배열 생성
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = FIXED_COLUMN_WIDTH;
        }

        printTitle(title);
        printTopBorder(columnWidths);
        printHeaderRow(headers, columnWidths);
        printMiddleBorder(columnWidths);
        
        // 데이터 행 출력
        for (String[] row : data) {
            printDataRow(row, columnWidths);
        }
        
        printBottomBorder(columnWidths);
        System.out.println();
    }
    
    /**
     * 표 제목을 출력합니다.
     * 
     * @param title 제목
     */
    private static void printTitle(String title) {
        System.out.println("  " + title);
    }
    
    /**
     * 상단 경계선을 출력합니다.
     * 
     * @param widths 컬럼 너비 배열
     */
    private static void printTopBorder(int[] widths) {
        System.out.print("  ╔");
        for (int i = 0; i < widths.length; i++) {
            System.out.print("═".repeat(widths[i]));
            if (i < widths.length - 1) {
                System.out.print("╦");
            }
        }
        System.out.println("╗");
    }
    
    /**
     * 중간 경계선을 출력합니다.
     * 
     * @param widths 컬럼 너비 배열
     */
    private static void printMiddleBorder(int[] widths) {
        System.out.print("  ╠");
        for (int i = 0; i < widths.length; i++) {
            System.out.print("═".repeat(widths[i]));
            if (i < widths.length - 1) {
                System.out.print("╬");
            }
        }
        System.out.println("╣");
    }
    
    /**
     * 하단 경계선을 출력합니다.
     * 
     * @param widths 컬럼 너비 배열
     */
    private static void printBottomBorder(int[] widths) {
        System.out.print("  ╚");
        for (int i = 0; i < widths.length; i++) {
            System.out.print("═".repeat(widths[i]));
            if (i < widths.length - 1) {
                System.out.print("╩");
            }
        }
        System.out.println("╝");
    }
    
    /**
     * 헤더 행을 출력합니다.
     * 
     * @param headers 헤더 배열
     * @param widths 컬럼 너비 배열
     */
    private static void printHeaderRow(String[] headers, int[] widths) {
        System.out.print("  ║");
        for (int i = 0; i < headers.length; i++) {
            String text = fixTextToWidth(headers[i], widths[i]);
            System.out.print(text);
            System.out.print("║");
        }
        System.out.println();
    }
    
    /**
     * 데이터 행을 출력합니다.
     * 
     * @param data 데이터 배열
     * @param widths 컬럼 너비 배열
     */
    private static void printDataRow(String[] data, int[] widths) {
        System.out.print("  ║");
        for (int i = 0; i < widths.length; i++) {
            String text = "";
            if (i < data.length && data[i] != null) {
                text = fixTextToWidth(data[i], widths[i]);
            } else {
                text = fixTextToWidth("", widths[i]); // 빈 데이터는 빈 문자열로 처리
            }
            System.out.print(text);
            System.out.print("║");
        }
        System.out.println();
    }
    
    /**
     * 문자열의 실제 콘솔 표시 폭을 계산합니다.
     * 한글, 중국어, 일본어 등은 2칸, 영문/숫자는 1칸으로 계산합니다.
     * 
     * @param text 측정할 텍스트
     * @return 실제 콘솔 표시 폭
     */
    private static int getDisplayWidth(String text) {
        int width = 0;
        for (char c : text.toCharArray()) {
            if (isFullWidthCharacter(c)) {
                width += 2; // 전각 문자 (한글, 중국어, 일본어 등)
            } else {
                width += 1; // 반각 문자 (영문, 숫자, 기호 등)
            }
        }
        return width;
    }
    
    /**
     * 전각 문자인지 판단합니다.
     * CMD 기본 환경 호환성을 고려하여 한글과 CJK 문자만 처리합니다.
     * 
     * @param c 판단할 문자
     * @return 전각 문자 여부
     */
    private static boolean isFullWidthCharacter(char c) {
        return (c >= 0x1100 && c <= 0x11FF) ||  // 한글 자모
               (c >= 0x3130 && c <= 0x318F) ||  // 한글 호환 자모
               (c >= 0xAC00 && c <= 0xD7AF) ||  // 한글 완성형
               (c >= 0x4E00 && c <= 0x9FFF) ||  // CJK 통합 한자
               (c >= 0x3400 && c <= 0x4DBF);    // CJK 확장 A
    }
    
    /**
     * 텍스트를 고정 너비(15자)에 맞춥니다.
     * 실제 콘솔 표시 폭을 고려하여 나머지는 우측에 공백으로 채웁니다.
     * 
     * @param text 원본 텍스트
     * @param width 목표 너비 (15자)
     * @return 너비에 맞춰진 텍스트
     */
    private static String fixTextToWidth(String text, int width) {
        if (text == null) {
            text = "";
        }
        
        int currentDisplayWidth = getDisplayWidth(text);
        
        // 텍스트가 너비보다 긴 경우 자르기
        if (currentDisplayWidth > width) {
            return truncateToWidth(text, width);
        }
        
        // 고정 너비에서 텍스트 표시 폭을 뺀 만큼 우측에 공백 추가
        int emptySpaces = width - currentDisplayWidth;
        return text + " ".repeat(emptySpaces);
    }
    
    /**
     * 텍스트를 지정된 표시 폭에 맞게 자릅니다.
     * 
     * @param text 자를 텍스트
     * @param targetWidth 목표 표시 폭
     * @return 잘린 텍스트
     */
    private static String truncateToWidth(String text, int targetWidth) {
        if (targetWidth <= 3) {
            // 매우 작은 폭인 경우 단순 자르기
            StringBuilder result = new StringBuilder();
            int currentWidth = 0;
            for (char c : text.toCharArray()) {
                int charWidth = isFullWidthCharacter(c) ? 2 : 1;
                if (currentWidth + charWidth > targetWidth) {
                    break;
                }
                result.append(c);
                currentWidth += charWidth;
            }
            return result.toString();
        } else {
            // "..." 추가할 공간 확보
            int availableWidth = targetWidth - 3;
            StringBuilder result = new StringBuilder();
            int currentWidth = 0;
            
            for (char c : text.toCharArray()) {
                int charWidth = isFullWidthCharacter(c) ? 2 : 1;
                if (currentWidth + charWidth > availableWidth) {
                    break;
                }
                result.append(c);
                currentWidth += charWidth;
            }
            
            // 남은 공간에 맞춰 "..." 추가
            result.append("...");
            int finalWidth = currentWidth + 3;
            if (finalWidth < targetWidth) {
                result.append(" ".repeat(targetWidth - finalWidth));
            }
            
            return result.toString();
        }
    }
    
    /**
     * 여러 행 표 출력을 위한 헬퍼 메서드
     * (향후 확장용)
     * 
     * @param title 제목
     * @param headers 헤더 배열
     * @param dataList 데이터 리스트
     */
    public static void printMultiRowTable(String title, String[] headers, java.util.List<String[]> dataList) {
        String[][] data = dataList.toArray(new String[0][]);
        printTable(title, headers, data);
    }
}
