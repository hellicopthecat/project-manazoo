package app.common.ui;

/**
 * 텍스트 아트 생성 및 출력을 담당하는 유틸리티 클래스입니다. Windows CMD 환경에서 ASCII 아트를 활용한 시각적 요소를
 * 제공합니다.
 */
public final class TextArtUtil {
    /**
     * private 생성자로 인스턴스 생성을 방지합니다.
     */
    private TextArtUtil() {
    }

    /**
     * 동물원 메인 로고를 출력합니다. 애플리케이션 시작 시 표시되는 대형 로고입니다.
     */
    private static void printZooLogo() {
        System.out.println("███╗   ███╗ █████╗ ███╗   ██╗ █████╗  ██████╗ ███████╗      ███████╗ ██████╗  ██████╗ ");
        System.out.println("████╗ ████║██╔══██╗████╗  ██║██╔══██╗██╔════╝ ██╔════╝      ╚══███╔╝██╔═══██╗██╔═══██╗");
        System.out.println("██╔████╔██║███████║██╔██╗ ██║███████║██║  ███╗█████╗          ███╔╝ ██║   ██║██║   ██║");
        System.out.println("██║╚██╔╝██║██╔══██║██║╚██╗██║██╔══██║██║   ██║██╔══╝         ███╔╝  ██║   ██║██║   ██║");
        System.out.println("██║ ╚═╝ ██║██║  ██║██║ ╚████║██║  ██║╚██████╔╝███████╗      ███████╗╚██████╔╝╚██████╔╝");
        System.out.println("╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝      ╚══════╝ ╚═════╝  ╚═════╝ ");
        System.out.println();
        System.out.println("  Welcome to ManageZoo");
        System.out.println("  Professional Zoo Management System");
    }

    /**
     * 관리자 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printAdminMenuTitle() {
        System.out.println(" █████╗ ██████╗ ███╗   ███╗██╗███╗   ██╗");
        System.out.println("██╔══██╗██╔══██╗████╗ ████║██║████╗  ██║");
        System.out.println("███████║██║  ██║██╔████╔██║██║██╔██╗ ██║");
        System.out.println("██╔══██║██║  ██║██║╚██╔╝██║██║██║╚██╗██║");
        System.out.println("██║  ██║██████╔╝██║ ╚═╝ ██║██║██║ ╚████║");
        System.out.println("╚═╝  ╚═╝╚═════╝ ╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝");
    }

    /**
     * 관람객 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printVisitorMenuTitle() {
        System.out.println("██╗   ██╗██╗███████╗██╗████████╗ ██████╗ ██████╗ ");
        System.out.println("██║   ██║██║██╔════╝██║╚══██╔══╝██╔═══██╗██╔══██╗");
        System.out.println("██║   ██║██║███████╗██║   ██║   ██║   ██║██████╔╝");
        System.out.println("╚██╗ ██╔╝██║╚════██║██║   ██║   ██║   ██║██╔══██╗");
        System.out.println(" ╚████╔╝ ██║███████║██║   ██║   ╚██████╔╝██║  ██║");
        System.out.println("  ╚═══╝  ╚═╝╚══════╝╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝");
    }

    /**
     * 사육장 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printEnclosureMenuTitle() {
        System.out.println("███████╗███╗   ██╗ ██████╗██╗      ██████╗ ███████╗██╗   ██╗██████╗ ███████╗");
        System.out.println("██╔════╝████╗  ██║██╔════╝██║     ██╔═══██╗██╔════╝██║   ██║██╔══██╗██╔════╝");
        System.out.println("█████╗  ██╔██╗ ██║██║     ██║     ██║   ██║███████╗██║   ██║██████╔╝█████╗  ");
        System.out.println("██╔══╝  ██║╚██╗██║██║     ██║     ██║   ██║╚════██║██║   ██║██╔══██╗██╔══╝  ");
        System.out.println("███████╗██║ ╚████║╚██████╗███████╗╚██████╔╝███████║╚██████╔╝██║  ██║███████╗");
        System.out.println("╚══════╝╚═╝  ╚═══╝ ╚═════╝╚══════╝ ╚═════╝ ╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝");
    }

    /**
     * 동물 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printAnimalMenuTitle() {
        System.out.println(" █████╗ ███╗   ██╗██╗███╗   ███╗ █████╗ ██╗     ");
        System.out.println("██╔══██╗████╗  ██║██║████╗ ████║██╔══██╗██║     ");
        System.out.println("███████║██╔██╗ ██║██║██╔████╔██║███████║██║     ");
        System.out.println("██╔══██║██║╚██╗██║██║██║╚██╔╝██║██╔══██║██║     ");
        System.out.println("██║  ██║██║ ╚████║██║██║ ╚═╝ ██║██║  ██║███████╗");
        System.out.println("╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝");
    }

    /**
     * 사육사 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printZookeeperMenuTitle() {
        System.out.println("███████╗ ██████╗  ██████╗ ██╗  ██╗███████╗███████╗██████╗ ███████╗██████╗ ");
        System.out.println("╚══███╔╝██╔═══██╗██╔═══██╗██║ ██╔╝██╔════╝██╔════╝██╔══██╗██╔════╝██╔══██╗");
        System.out.println("  ███╔╝ ██║   ██║██║   ██║█████╔╝ █████╗  █████╗  ██████╔╝█████╗  ██████╔╝");
        System.out.println(" ███╔╝  ██║   ██║██║   ██║██╔═██╗ ██╔══╝  ██╔══╝  ██╔═══╝ ██╔══╝  ██╔══██╗");
        System.out.println("███████╗╚██████╔╝╚██████╔╝██║  ██╗███████╗███████╗██║     ███████╗██║  ██║");
        System.out.println("╚══════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚══════╝╚═╝  ╚═╝");
    }

    /**
     * 재무 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printFinanceMenuTitle() {
        System.out.println("███████╗██╗███╗   ██╗ █████╗ ███╗   ██╗ ██████╗███████╗");
        System.out.println("██╔════╝██║████╗  ██║██╔══██╗████╗  ██║██╔════╝██╔════╝");
        System.out.println("█████╗  ██║██╔██╗ ██║███████║██╔██╗ ██║██║     █████╗  ");
        System.out.println("██╔══╝  ██║██║╚██╗██║██╔══██║██║╚██╗██║██║     ██╔══╝  ");
        System.out.println("██║     ██║██║ ╚████║██║  ██║██║ ╚████║╚██████╗███████╗");
        System.out.println("╚═╝     ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝╚══════╝");
    }

    /**
     * 등록 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printRegisterMenuTitle() {
        System.out.println("██████╗ ███████╗ ██████╗ ██╗███████╗████████╗███████╗██████╗ ");
        System.out.println("██╔══██╗██╔════╝██╔════╝ ██║██╔════╝╚══██╔══╝██╔════╝██╔══██╗");
        System.out.println("██████╔╝█████╗  ██║  ███╗██║███████╗   ██║   █████╗  ██████╔╝");
        System.out.println("██╔══██╗██╔══╝  ██║   ██║██║╚════██║   ██║   ██╔══╝  ██╔══██╗");
        System.out.println("██║  ██║███████╗╚██████╔╝██║███████║   ██║   ███████╗██║  ██║");
        System.out.println("╚═╝  ╚═╝╚══════╝ ╚═════╝ ╚═╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝");
    }

    /**
     * 조회 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printViewMenuTitle() {
        System.out.println("██╗   ██╗██╗███████╗██╗    ██╗");
        System.out.println("██║   ██║██║██╔════╝██║    ██║");
        System.out.println("██║   ██║██║█████╗  ██║ █╗ ██║");
        System.out.println("╚██╗ ██╔╝██║██╔══╝  ██║███╗██║");
        System.out.println(" ╚████╔╝ ██║███████╗╚███╔███╔╝");
        System.out.println("  ╚═══╝  ╚═╝╚══════╝ ╚══╝╚══╝ ");
    }

    /**
     * 수정 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printEditMenuTitle() {
        System.out.println("███████╗██████╗ ██╗████████╗");
        System.out.println("██╔════╝██╔══██╗██║╚══██╔══╝");
        System.out.println("█████╗  ██║  ██║██║   ██║   ");
        System.out.println("██╔══╝  ██║  ██║██║   ██║   ");
        System.out.println("███████╗██████╔╝██║   ██║   ");
        System.out.println("╚══════╝╚═════╝ ╚═╝   ╚═╝   ");
    }

    /**
     * 삭제 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printRemoveMenuTitle() {
        System.out.println("██████╗ ███████╗███╗   ███╗ ██████╗ ██╗   ██╗███████╗");
        System.out.println("██╔══██╗██╔════╝████╗ ████║██╔═══██╗██║   ██║██╔════╝");
        System.out.println("██████╔╝█████╗  ██╔████╔██║██║   ██║██║   ██║█████╗  ");
        System.out.println("██╔══██╗██╔══╝  ██║╚██╔╝██║██║   ██║╚██╗ ██╔╝██╔══╝  ");
        System.out.println("██║  ██║███████╗██║ ╚═╝ ██║╚██████╔╝ ╚████╔╝ ███████╗");
        System.out.println("╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝ ╚═════╝   ╚═══╝  ╚══════╝");
    }

    /**
     * 환영 메시지를 텍스트 아트로 출력합니다. ManageZoo 로고 스타일과 일관성 있는 Welcome!! 메시지를 출력합니다.
     */
    public static void printWelcomeMessage() {
        System.out.println("██╗    ██╗███████╗██╗      ██████╗ ██████╗ ███╗   ███╗███████╗██╗██╗");
        System.out.println("██║    ██║██╔════╝██║     ██╔════╝██╔═══██╗████╗ ████║██╔════╝██║██║");
        System.out.println("██║ █╗ ██║█████╗  ██║     ██║     ██║   ██║██╔████╔██║█████╗  ██║██║");
        System.out.println("██║███╗██║██╔══╝  ██║     ██║     ██║   ██║██║╚██╔╝██║██╔══╝  ╚═╝╚═╝");
        System.out.println("╚███╔███╔╝███████╗███████╗╚██████╗╚██████╔╝██║ ╚═╝ ██║███████╗██╗██╗");
        System.out.println(" ╚══╝╚══╝ ╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚══════╝╚═╝╚═╝");
    }

    /**
     * 조회 메뉴 타이틀을 텍스트 아트로 출력합니다. ManageZoo 로고와 일관성 있는 클래식 블록 스타일로 구현됩니다.
     */
    public static void printReservationMenuTitle() {
        System.out.println("██████╗ ███████╗███████╗███████╗██████╗ ██╗   ██╗ █████╗ ████████╗██╗ ██████╗ ███╗   ██╗");
        System.out.println("██╔══██╗██╔════╝██╔════╝██╔════╝██╔══██╗██║   ██║██╔══██╗╚══██╔══╝██║██╔═══██╗████╗  ██║");
        System.out.println("██████╔╝█████╗  ███████╗█████╗  ██████╔╝██║   ██║███████║   ██║   ██║██║   ██║██╔██╗ ██║");
        System.out.println("██╔══██╗██╔══╝  ╚════██║██╔══╝  ██╔══██╗╚██╗ ██╔╝██╔══██║   ██║   ██║██║   ██║██║╚██╗██║");
        System.out.println("██║  ██║███████╗███████║███████╗██║  ██║ ╚████╔╝ ██║  ██║   ██║   ██║╚██████╔╝██║ ╚████║");
        System.out.println("╚═╝  ╚═╝╚══════╝╚══════╝╚══════╝╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝");

    }

    /**
     * 미구현 기능에 대한 사과 메시지를 출력합니다.
     * 현재 개발 중인 기능에 대해 사용자에게 안내할 때 사용합니다.
     */
    public static void printSorryMessage() {
        System.out.println("███████╗ ██████╗ ██████╗ ██████╗ ██╗   ██╗");
        System.out.println("██╔════╝██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝");
        System.out.println("███████╗██║   ██║██████╔╝██████╔╝ ╚████╔╝ ");
        System.out.println("╚════██║██║   ██║██╔══██╗██╔══██╗  ╚██╔╝  ");
        System.out.println("███████║╚██████╔╝██║  ██║██║  ██║   ██║   ");
        System.out.println("╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ");
    }

    /**
     * 완전한 환영 화면을 출력합니다. Welcome 텍스트 아트와 접속 메뉴를 함께 출력하여 완성된 시작 화면을 구성합니다.
     */
    public static void printWelcomeScreen() {
        printWelcomeMessage();
        MenuUtil.printAccessMenu();
    }

    /**
     * 로딩 애니메이션을 출력합니다. 3초 동안 로딩 게이지가 진행되고 완료되면 메인 로고를 출력합니다.
     */
    public static void printLoadingAnimation() {
        final int LOADING_TIME_MS = 3000; // 3초
        final int PROGRESS_BAR_WIDTH = 100; // 진행바 너비
        final int UPDATE_INTERVAL_MS = LOADING_TIME_MS / PROGRESS_BAR_WIDTH; // 업데이트 간격

        System.out.println();
        System.out.println("  Starting ManageZoo System...");
        System.out.println();

        // 진행바를 한 줄에서 동적으로 업데이트
        System.out.print("  Loading: [");

        for (int i = 0; i <= PROGRESS_BAR_WIDTH; i++) {
            // 백스페이스로 이전 진행바를 지우고 새로 그리기
            if (i > 0) {
                // 이전 진행바 길이만큼 백스페이스
                for (int j = 0; j < PROGRESS_BAR_WIDTH + 10; j++) { // ] + 퍼센트 + 여백
                    System.out.print("\b");
                }
            }

            // 새로운 진행바 그리기
            String progress = "█".repeat(i);
            String remaining = " ".repeat(PROGRESS_BAR_WIDTH - i);
            int percentage = (i * 100) / PROGRESS_BAR_WIDTH;

            System.out.print(progress + remaining + " " + String.format("%3d%%", percentage));
            System.out.flush(); // 즉시 출력

            // 마지막이 아니면 대기
            if (i < PROGRESS_BAR_WIDTH) {
                try {
                    Thread.sleep(UPDATE_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        System.out.println();
        System.out.println("  Loading Complete!");

        // 잠시 대기 후 화면 클리어
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        UIUtil.clearScreen();
        printZooLogo();
    }
}
