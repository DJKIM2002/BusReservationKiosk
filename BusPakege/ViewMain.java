package BusPakege;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import static BusPakege.BusFunc.*;


public class ViewMain extends JFrame {
    private final JLabel timeL; // 현재 시각을 표시할 라벨

    // 메인 화면
    public ViewMain() {
        startClock(); // 시계 시작

        setTitle("버스 예매 키오스크 - 메인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768); // 창 크기 설정
        setLocationRelativeTo(null); // 가운데 창 띄우기
        setLayout(new BorderLayout());

        // 상단 패널(타이틀 및 현재 시각 표시)
        JPanel topP = new JPanel(new BorderLayout());
        topP.setBackground(new Color(70, 130, 180));
        JLabel titleL = new JLabel("버스 예매 키오스크", SwingConstants.CENTER);
        titleL.setFont(boldFont(36));
        titleL.setForeground(Color.WHITE);
        topP.add(titleL, BorderLayout.CENTER);

        // 현재 시각 표시 (상단 우측 배치)
        timeL = new JLabel();
        timeL.setHorizontalAlignment(SwingConstants.CENTER); // 라벨 가운데 정렬
        timeL.setFont(boldFont(18));
        timeL.setForeground(Color.WHITE);
        timeL.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20)); // 패딩 설정
        topP.add(timeL, BorderLayout.EAST); // 현재 시각 라벨을 상단 패널의 오른쪽에 배치

        add(topP, BorderLayout.NORTH);

        // 이미지 패널(가운데 배치)
        JPanel centerP = new JPanel();
        centerP.setLayout(new BorderLayout());
        JLabel bImageL = new JLabel();
        bImageL.setIcon(new ImageIcon("image/bus_image.jpg")); // 버스 이미지
        bImageL.setHorizontalAlignment(SwingConstants.CENTER);
        centerP.add(bImageL, BorderLayout.CENTER);
        add(centerP, BorderLayout.CENTER);

        // 버튼 패널(하단 배치)
        JPanel btnP = new JPanel(new GridLayout(2, 2, 20, 20)); // 2 X 2 그리드
        btnP.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // 패딩 설정

        JButton reserveBtn = createBtn("예매하기");
        JButton checkBtn = createBtn("예매확인");
        JButton cancelBtn = createBtn("예매취소");
        JButton exitBtn = createBtn("종료");

        // 예매하기 버튼 리스너
        reserveBtn.addActionListener(e -> {
            new ViewReserve(); // 예매 화면 이동
            setVisible(false);
        });

        // 예매확인 버튼 리스너
        checkBtn.addActionListener(e -> {
            new ViewCheck();
            setVisible(false);
        });

        // 예매취소 버튼 리스너
        cancelBtn.addActionListener(e -> {
            new ViewCancel();
            setVisible(false);
        });

        // 종료 버튼 리스너
        exitBtn.addActionListener(e -> System.exit(0));

        // 하단 버튼 패널에 추가
        btnP.add(reserveBtn);
        btnP.add(checkBtn);
        btnP.add(cancelBtn);
        btnP.add(exitBtn);

        add(btnP, BorderLayout.SOUTH);

        // 창을 보이게 함
        setVisible(true);
    }

    // 현재 시간을 가져오는 메서드
    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(new Date());
    }

    // 시계 업데이트 메서드
    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            timeL.setText(getCurrentTime()); // 실시간으로 현재 시각 업데이트
        });
        timer.start(); // 타이머 시작
    }

    // main 메서드
    public static void main(String[] args) {
        new ViewMain();
    }
}
