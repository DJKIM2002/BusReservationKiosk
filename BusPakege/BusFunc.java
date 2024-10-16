package BusPakege;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class BusFunc {
    // 예매 정보 로딩 메서드
    static ArrayList<Reservation> loadReservation(String pLogID) {
        String query = "SELECT r.ReservationID, r.SeatID, b.BusType, rc.DepartureCity, rc.ArrivalCity, " +
                "sc.DepartureTime, sc.ArrivalTime, sc.BusDate, s.SeatNumber, p.PassengerType, " +
                "pay.TotalPrice " +
                "FROM Reservation r " +
                "JOIN Passenger p ON r.PassengerID = p.PassengerID " +
                "JOIN Seat s ON r.SeatID = s.SeatID " +
                "JOIN Schedule sc ON r.ScheduleID = sc.ScheduleID " +
                "JOIN Route rc ON sc.RouteID = rc.RouteID " +
                "JOIN Bus b ON sc.BusID = b.BusID " +
                "JOIN Payment pay ON r.ReservationID = pay.ReservationID " +
                "WHERE p.PassengerLogID = ?";

        ArrayList<Reservation> rl = new ArrayList<>(); // 승객 ID별 예매 정보 리스트를 저장할 ArrayList

        try (Connection con = getConnection(); // DB 연결
             PreparedStatement ps = con.prepareStatement(query)) { // ps 사용(SQL 인젝션 방지 및 성능 향상)

            ps.setString(1, pLogID); // 승객 ID 대입
            ResultSet rs = ps.executeQuery(); // SQL 쿼리 실행

            while (rs.next()) { // Select문을 통해 가져온 데이터 저장
                rl.add(new Reservation(
                        rs.getInt("ReservationID"), // 예매 ID
                        rs.getInt("SeatID"), // 좌석 ID
                        rs.getString("BusType"), // 버스 종류
                        rs.getString("DepartureCity"), // 출발지
                        rs.getString("ArrivalCity"), // 도착지
                        rs.getTime("DepartureTime"), // 출발 시간
                        rs.getTime("ArrivalTime"), // 도착 시간
                        rs.getDate("BusDate"), // 운행 날짜
                        rs.getInt("SeatNumber"), // 좌석 번호
                        rs.getString("PassengerType"), // 승객 유형
                        rs.getInt("TotalPrice") // 총 결제 금액
                ));
            }
        } catch (SQLException ex) { // SQL 예외 처리
            ex.printStackTrace();
        }
        return rl; // 예매 정보 리스트 반환
    }

    // DB 연결 메서드
    static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/bus";
        String uName = "******";
        String pwd = "******";
        return DriverManager.getConnection(url, uName, pwd);
    }

    // 예매 정보 클래스
    static class Reservation {
        int rID; // 예매ID
        int sID; // 좌석ID
        String bType; // 버스 종류
        String departure; // 출발지
        String arrival; // 도착지
        Time dTime; // 출발 시간
        Time aTime; // 도착 시간
        Date bDate; // 운행 날짜
        int sNum; // 좌석 번호
        String pType; // 승객 유형
        int price; // 총 결제 금액

        // 생성자 메서드
        Reservation(int rID, int sID, String bType, String departure, String arrival,
                               Time dTime, Time aTime, Date bDate, int sNum,
                               String pType, int price) {
            this.rID = rID;
            this.sID = sID;
            this.bType = bType;
            this.departure = departure;
            this.arrival = arrival;
            this.dTime = dTime;
            this.aTime = aTime;
            this.bDate = bDate;
            this.sNum = sNum;
            this.pType = pType;
            this.price = price;
        }
    }

    // 버튼 생성 메서드
    static JButton createBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BusFunc.boldFont(20));
        btn.setFocusPainted(false); // 테두리 제거
        btn.setBackground(new Color(100, 149, 237)); // 버튼 색상 설정
        btn.setForeground(Color.WHITE); // 버튼 글씨 색상 설정
        return btn;
    }

    // 볼드체 폰트 조절 함수
    static Font boldFont(int size) {
        return new Font("맑은 고딕", Font.BOLD, size);
    }
}
