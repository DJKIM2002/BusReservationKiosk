package BusPakege;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import static BusPakege.BusFunc.*;

public class ViewCancel extends JFrame {
    private ArrayList<Reservation> rl; // 승객 ID별 예매 정보 리스트
    private int currentIdx = 0; // 예매 정보 리스트의 현재 인덱스

    // 예약 정보를 표시할 라벨들
    private final JLabel bTypeL = new JLabel(); // 버스 종류 라벨
    private final JLabel departureL = new JLabel(); // 출발지 라벨
    private final JLabel arrivalL = new JLabel(); // 도착지 라벨
    private final JLabel dTimeL = new JLabel(); // 출발 시간 라벨
    private final JLabel aTimeL = new JLabel(); // 도착 시간 라벨
    private final JLabel bDateL = new JLabel(); // 운행 날짜 라벨
    private final JLabel sNumL = new JLabel(); // 좌석 번호 라벨
    private final JLabel pTypeL = new JLabel(); // 승객 유형 라벨
    private final JLabel priceL = new JLabel(); // 총 결제 금액 라벨

    // 예매 취소 화면
    public ViewCancel() {
        setTitle("버스 예매 - 예매 취소");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768); // 창 크기 설정
        setLocationRelativeTo(null); // 가운데 창 띄우기
        setLayout(new BorderLayout());

        // 타이틀 라벨
        JLabel titleL = new JLabel("예매 취소", SwingConstants.CENTER);
        titleL.setFont(boldFont(30));
        titleL.setForeground(Color.WHITE);
        titleL.setBackground(new Color(70, 130, 180));
        titleL.setOpaque(true);
        add(titleL, BorderLayout.NORTH);

        // 데이터 출력 패널
        JPanel dataP = new JPanel(new GridLayout(10, 2, 10, 10)); // 10 X 2 그리드
        dataP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 모든 방향에 20의 패딩 설정

        // 승객 ID 검색 패널(검색창, 검색 버튼)
        JPanel searchP = new JPanel(new BorderLayout(10, 0));
        JTextField pLogIDF = new JTextField();
        searchP.add(pLogIDF, BorderLayout.CENTER);

        // 승객 ID 검색 버튼
        JButton searchBtn = createBtn("검색");
        searchBtn.addActionListener(e -> {
            String pLogID = pLogIDF.getText().trim(); // 입력된 문자열에서 공백 제거
            if (!pLogID.isEmpty()) { // 승객 ID 입력 후 검색 버튼을 누르면
                rl = BusFunc.loadReservation(pLogID);
                currentIdx = 0;

                if (rl.isEmpty()) { // 해당 승객 ID로 검색되는 예매 내역이 없으면
                    JOptionPane.showMessageDialog(this, "예매 내역이 존재하지 않습니다.", "내역 불러오기 오류", JOptionPane.ERROR_MESSAGE);
                    clearLabels();
                } else  // 예매 내역이 있으면
                    showReservation(currentIdx); // 현재 인덱스로 지정
            } else  // 승객 ID 입력하지 않고 검색 버튼을 누르면
                JOptionPane.showMessageDialog(this, "사용자 ID를 입력해주세요.", "ID 입력 오류", JOptionPane.ERROR_MESSAGE);
        });
        searchP.add(searchBtn, BorderLayout.EAST); // 검색 버튼을 텍스트 필드 오른쪽에 배치

        // 승객 ID 검색 패널 추가
        dataP.add(new JLabel("사용자 ID:"));
        dataP.add(searchP);

        // 예매 내역 패널 추가
        dataP.add(new JLabel("버스 종류:"));
        dataP.add(bTypeL);
        dataP.add(new JLabel("출발지:"));
        dataP.add(departureL);
        dataP.add(new JLabel("도착지:"));
        dataP.add(arrivalL);
        dataP.add(new JLabel("출발 시간:"));
        dataP.add(dTimeL);
        dataP.add(new JLabel("도착 시간:"));
        dataP.add(aTimeL);
        dataP.add(new JLabel("운행 날짜:"));
        dataP.add(bDateL);
        dataP.add(new JLabel("좌석 번호:"));
        dataP.add(sNumL);
        dataP.add(new JLabel("승객 유형:"));
        dataP.add(pTypeL);
        dataP.add(new JLabel("결제 금액:"));
        dataP.add(priceL);

        add(dataP, BorderLayout.CENTER);

        // 하단 버튼 패널(예매 취소, 이전, 다음, 메인)
        JPanel btnP = new JPanel();

        JButton deleteBtn = createBtn("예매 취소");
        deleteBtn.setBackground(new Color(250, 136, 62)); // 예매 취소 버튼은 주황색으로 설정
        JButton preBtn = createBtn("이전"); // 이전 예매 내역 조회
        JButton nextBtn = createBtn("다음"); // 다음 예매 내역 조회
        JButton homeBtn = createBtn("메인"); // 메인 화면으로 돌아가는 버튼

        // 예매 취소 버튼 클릭 리스너
        deleteBtn.addActionListener(e -> cancelReservation());

        // 이전 버튼 클릭 리스너
        preBtn.addActionListener(e -> showPreReservation());

        // 다음 버튼 클릭 리스너
        nextBtn.addActionListener(e -> showNextReservation());

        // 메인 버튼 클릭 리스너
        homeBtn.addActionListener(e -> {
            new ViewMain();
            dispose();
        });

        // 하단 버튼 패널에 추가
        btnP.add(deleteBtn);
        btnP.add(preBtn);
        btnP.add(nextBtn);
        btnP.add(homeBtn);

        add(btnP, BorderLayout.SOUTH);

        // 창을 보이게 함
        setVisible(true);
    }

    // 해당 인덱스의 예매 내역을 보여주는 메서드
    private void showReservation(int idx) {
        if (idx >= 0 && idx < rl.size()) {
            Reservation data = rl.get(idx);
            bTypeL.setText(data.bType);
            departureL.setText(data.departure);
            arrivalL.setText(data.arrival);
            dTimeL.setText(data.dTime.toString().substring(0, 5)); // 시, 분만 표시
            aTimeL.setText(data.aTime.toString().substring(0, 5)); // 시, 분만 표시
            bDateL.setText(data.bDate.toString());
            sNumL.setText(data.sNum + "번");
            pTypeL.setText(data.pType);
            priceL.setText(data.price + "원");
        }
    }

    // 예매 취소 메서드
    private void cancelReservation() {
        if (rl != null && !rl.isEmpty()) { // 예매 내역이 존재하는 경우
            int rID = rl.get(currentIdx).rID; // 현재 인덱스의 예약 ID 가져오기
            int sID = rl.get(currentIdx).sID; // 해당 예약의 SeatID 가져오기

            // 좌석 상태 업데이트 쿼리
            String updateSeatQ = "UPDATE Seat SET IsAvailable = true WHERE SeatID = ?";

            // 예매 취소 쿼리
            String deleteQ = "DELETE FROM Reservation WHERE ReservationID = ?";

            try (Connection con = getConnection()) { // DB 연결
                // 좌석 상태 업데이트
                try (PreparedStatement ps = con.prepareStatement(updateSeatQ)) {
                    ps.setInt(1, sID);
                    ps.executeUpdate();
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // 예약 취소
                try (PreparedStatement ps = con.prepareStatement(deleteQ)) {
                    ps.setInt(1, rID);
                    int isCancel = ps.executeUpdate(); // 예매 취소 쿼리 실행 여부 

                    if (isCancel > 0) { // 예매 취소 성공
                        JOptionPane.showMessageDialog(this, "예매 취소되었습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
                        clearLabels(); // 
                        rl.remove(currentIdx); // 목록에서 현재 예매 내역 삭제
                        new ViewMain(); // 메인 화면으로 이동
                        dispose();
                    } else // DB 오류로 인한 예매 취소 실패
                        JOptionPane.showMessageDialog(this, "예매 취소에 실패했습니다.", "DB 오류", JOptionPane.ERROR_MESSAGE);
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } catch (SQLException ex) { 
                ex.printStackTrace();
            }
        } else { // 예매 내역이 존재하지 않는 경우
            JOptionPane.showMessageDialog(this, "취소할 예매 내역이 없습니다.", "내역 불러오기 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 이전 예매 내역 보여주기 메서드
    private void showPreReservation() { 
        if (rl == null || rl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "사용자 ID 입력 후, 검색을 먼저 해주세요.", "ID 입력 오류", JOptionPane.ERROR_MESSAGE);
            return; // ID가 입력되지 않은 경우, 오류 출력 후 메서드 종료
        }
        if (currentIdx > 0) {
            currentIdx--;
            showReservation(currentIdx);
        } else {
            JOptionPane.showMessageDialog(this, "이전 예매 내역이 없습니다.", "내역 불러오기 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 다음 예매 내역 보여주기 메서드
    private void showNextReservation() {
        if (rl == null || rl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "사용자 ID 입력 후, 검색을 먼저 해주세요.", "ID 입력 오류", JOptionPane.ERROR_MESSAGE);
            return; // ID가 입력되지 않은 경우, 오류 출력 후 메서드 종료
        }
        if (currentIdx < rl.size() - 1) {
            currentIdx++;
            showReservation(currentIdx);
        } else {
            JOptionPane.showMessageDialog(this, "다음 예매 내역이 없습니다.", "내역 불러오기 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 예매 내역 초기화 메서드
    private void clearLabels() {
        bTypeL.setText("");
        departureL.setText("");
        arrivalL.setText("");
        dTimeL.setText("");
        aTimeL.setText("");
        bDateL.setText("");
        sNumL.setText("");
        pTypeL.setText("");
        priceL.setText("");
    }
}
