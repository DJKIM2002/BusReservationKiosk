package BusPakege;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import static BusPakege.BusFunc.*;

public class ViewPayment extends JFrame {
    double price = 0; // 총 결제 금액

    // 결제 화면
    public ViewPayment() {
        setTitle("버스 예매 - 최종 결제");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768); // 창 크기 설정
        setLocationRelativeTo(null); // 가운데 창 띄우기
        setLayout(new BorderLayout());

        // 상단 타이틀
        JLabel titleL = new JLabel("예매 확인 및 결제", SwingConstants.CENTER);
        titleL.setFont(boldFont(30)); // 폰트 크기 증가
        titleL.setForeground(Color.WHITE);
        titleL.setBackground(new Color(70, 130, 180));
        titleL.setOpaque(true);
        add(titleL, BorderLayout.NORTH);

        // 결제 전 예매 내역 확인
        JPanel summaryP = new JPanel(new GridLayout(9, 2, 10, 10)); // 9 X 2 그리드
        summaryP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 모든 방향에 20의 패딩 설정

        summaryP.add(new JLabel("버스 종류:"));
        summaryP.add(new JLabel(ViewReserve.bType));
        summaryP.add(new JLabel("출발지:"));
        summaryP.add(new JLabel(ViewReserve.departure));
        summaryP.add(new JLabel("도착지:"));
        summaryP.add(new JLabel(ViewReserve.arrival));
        summaryP.add(new JLabel("예매 날짜:"));
        summaryP.add(new JLabel(ViewReserve.bDate));
        summaryP.add(new JLabel("출발 시간:"));
        summaryP.add(new JLabel(ViewReserve.dTime));
        summaryP.add(new JLabel("도착 시간:"));
        summaryP.add(new JLabel(ViewReserve.aTime));
        summaryP.add(new JLabel("승객 유형:"));
        summaryP.add(new JLabel(ViewPassenger.pType));
        summaryP.add(new JLabel("선택한 좌석 번호:"));
        summaryP.add(new JLabel(ViewSeatSelection.sNum + "번"));

        // 총 결제 금액 계산(할인 고려)
        switch (ViewReserve.bType) {
            case "일반":
                price = 16000.0;
                break;
            case "우등":
                price = 18000.0;
        }

        switch (ViewPassenger.pType) {
            
            case "청소년":
                price -= price * 0.3;
                break;
            case "어린이":
                price -= price * 0.2;
                break;
            case "노인":
                price = 0.0;
                break;
            case "장애인":
                price -= price * 0.5;
        }

        summaryP.add(new JLabel("총 결제 금액(할인 포함):"));
        summaryP.add(new JLabel((int) price + "원"));

        add(summaryP, BorderLayout.CENTER);

        // 하단 버튼 패널(이전, 결제)
        JPanel btnP = new JPanel();
        btnP.setLayout(new FlowLayout()); // 버튼을 가로로 정렬

        // 이전 버튼
        JButton backBtn = createBtn("이전");
        backBtn.addActionListener(e -> {
            new ViewPassenger(); // 이전 화면으로 이동
            dispose(); // 현재 창 닫기
        });

        // 결제 버튼
        JButton confirmBtn = createBtn("결제");
        confirmBtn.addActionListener(e -> {
            Connection con = null;
            try {
                con = getConnection(); // DB 연결
                con.setAutoCommit(false); // 트랜잭션 시작

                // 1. 노선 테이블
                String insertRtQ = "INSERT INTO Route (DepartureCity, ArrivalCity) VALUES (?, ?)";
                PreparedStatement psRt = con.prepareStatement(insertRtQ, Statement.RETURN_GENERATED_KEYS);
                psRt.setString(1, ViewReserve.departure);
                psRt.setString(2, ViewReserve.arrival);
                psRt.executeUpdate();
                ResultSet rtKeys = psRt.getGeneratedKeys();
                int rtID = -1;
                if (rtKeys.next()) {
                    rtID = rtKeys.getInt(1);
                }

                // 2. 운행 일정 테이블
                String insertScQ = "INSERT INTO Schedule (BusID, RouteID, DepartureTime, ArrivalTime, BusDate) " +
                        "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psSc = con.prepareStatement(insertScQ, Statement.RETURN_GENERATED_KEYS);
                psSc.setInt(1, ViewReserve.bID);
                psSc.setInt(2, rtID);
                psSc.setString(3, ViewReserve.dTime);
                psSc.setString(4, ViewReserve.aTime);
                psSc.setString(5, ViewReserve.bDate);
                psSc.executeUpdate();
                ResultSet scKeys = psSc.getGeneratedKeys();
                int scID = -1;
                if (scKeys.next()) {
                    scID = scKeys.getInt(1);
                }

                // 3. 예매 테이블
                String insertRQ = "INSERT INTO Reservation (PassengerID, ScheduleID, SeatID, ReservationDate) " +
                        "VALUES (?, ?, ?, ?)";
                PreparedStatement psR = con.prepareStatement(insertRQ, Statement.RETURN_GENERATED_KEYS);
                psR.setInt(1, ViewPassenger.pID);
                psR.setInt(2, scID);
                psR.setInt(3, ViewSeatSelection.sID);
                psR.setString(4, ViewReserve.bDate);
                psR.executeUpdate();

                ResultSet rKeys = psR.getGeneratedKeys();
                int rID = -1;
                if (rKeys.next()) {
                    rID = rKeys.getInt(1);
                }

                // 4. 결제 테이블
                String insertPayQ = "INSERT INTO Payment (ReservationID, PaymentDate, TotalPrice) VALUES (?, ?, ?)";
                PreparedStatement psPay = con.prepareStatement(insertPayQ);
                psPay.setInt(1, rID);
                psPay.setString(2, java.time.LocalDate.now().toString()); // 현재 날짜
                psPay.setDouble(3, price);
                psPay.executeUpdate();

                // 트랜잭션 커밋
                con.commit();

                JOptionPane.showMessageDialog(this, "예매가 완료되었습니다.");
                new ViewMain(); // 메인 화면 이동
                dispose(); // 예약 완료 후 창 닫기

                // DB 오류 방지(예외 처리)
            } catch (SQLException ex) {
                ex.printStackTrace();
                if (con != null) {
                    try {
                        con.rollback(); // 트랜잭션 롤백
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                }
            } finally {
                if (con != null) {
                    try {
                        con.close(); // 연결 종료
                    } catch (SQLException closeEx) {
                        closeEx.printStackTrace();
                    }
                }
            }
        });

        btnP.add(backBtn);
        btnP.add(confirmBtn);

        add(btnP, BorderLayout.SOUTH);

        // 창을 보이게 함
        setVisible(true);
    }

}
