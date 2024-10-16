package BusPakege;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import static BusPakege.BusFunc.boldFont;
import static BusPakege.BusFunc.getConnection;
import static java.lang.Integer.parseInt;

public class ViewSeatSelection extends JFrame {
    static int sID; // 좌석 ID(기본 키)
    static int sNum; // 좌석 번호
    private final JPanel seatP; // 좌석 버튼 패널

    public ViewSeatSelection() {
        setTitle("버스 예매 - 좌석 선택");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768); // 창 크기 설정
        setLocationRelativeTo(null); // 가운데 창 띄우기
        setLayout(new BorderLayout());

        // 상단 타이틀
        JLabel titleL = new JLabel("좌석 선택", SwingConstants.CENTER);
        titleL.setFont(boldFont(30));
        titleL.setForeground(Color.WHITE);
        titleL.setBackground(new Color(70, 130, 180));
        titleL.setOpaque(true);
        add(titleL, BorderLayout.NORTH);

        // 좌석 버튼 패널
        seatP = new JPanel();
        seatP.setLayout(new GridBagLayout());
        seatP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        Dimension btnSize = new Dimension(80, 60); // 좌석 번호에 상관 없이 버튼 크기 고정

        // 좌석 버튼 배치
        int startSNum = 1; // 좌석 번호는 1부터 시작
        for (int row = 0; row < 8; row++) {
            boolean isFOLR = (row == 0 || row == 7); // 처음, 마지막 행에만 라벨 표시

            gbc.gridy = row;

            // 왼쪽 창가 또는 빈칸
            gbc.gridx = 0;
            if (isFOLR) {
                JLabel windowLL = new JLabel("(창가)", SwingConstants.CENTER);
                windowLL.setFont(boldFont(20));
                seatP.add(windowLL, gbc);
            } else {
                seatP.add(new JLabel(" "), gbc);
            }

            // 좌석 2개 (왼쪽)
            for (int i = 0; i < 2; i++) {
                gbc.gridx = i + 1;
                JButton seatBtn = createSeatBtn(startSNum, btnSize);
                seatP.add(seatBtn, gbc);
                startSNum++;
            }

            // 통로 표시 또는 빈칸
            gbc.gridx = 3;
            if (isFOLR) {
                JLabel aisleL = new JLabel("(통로)", SwingConstants.CENTER);
                aisleL.setFont(boldFont(20));
                seatP.add(aisleL, gbc);
            } else {
                seatP.add(new JLabel(" "), gbc);
            }

            // 좌석 2개 (오른쪽)
            for (int i = 0; i < 2; i++) {
                gbc.gridx = i + 4;
                JButton seatBtn = createSeatBtn(startSNum, btnSize);
                seatP.add(seatBtn, gbc);
                startSNum++;
            }

            // 오른쪽 창가 또는 빈칸
            gbc.gridx = 6;
            if (isFOLR) {
                JLabel windowLR = new JLabel("(창가)", SwingConstants.CENTER);
                windowLR.setFont(boldFont(20));
                seatP.add(windowLR, gbc);
            } else {
                seatP.add(new JLabel(" "), gbc);
            }
        }

        add(seatP, BorderLayout.CENTER);

        // 하단 패널(이전)
        JPanel btnP = new JPanel();
        JButton backButton = BusFunc.createBtn("이전");
        backButton.addActionListener(e -> {
            new ViewReserve(); // 예매 화면으로 돌아가기
            dispose();
        });
        btnP.add(backButton);
        add(btnP, BorderLayout.SOUTH);

        // 좌석 상태 불러오기
        loadSeatAvailability();

        setVisible(true);
    }

    // 좌석 버튼을 생성하는 메서드
    private JButton createSeatBtn(int seatNumber, Dimension buttonSize) {
        JButton seatBtn = new JButton(String.valueOf(seatNumber));
        seatBtn.setPreferredSize(buttonSize);
        seatBtn.setForeground(Color.WHITE);
        seatBtn.setFont(boldFont(20));

        // 좌석 선택 가능 (초기 상태)
        seatBtn.setBackground(new Color(114, 210, 255));
        seatBtn.addActionListener(e -> {
            sNum = parseInt(seatBtn.getText());
            selectSeat(); // 선택한 좌석 번호로 업데이트
        });

        return seatBtn;
    }

    // 주어진 좌석 번호에 해당하는 버튼을 찾는 메서드
    private JButton findSeatButton(int sNum) {
        for (Component comp : seatP.getComponents()) {
            if (comp instanceof JButton button) { // 컴포넌트가 JButton인지 확인
                if (!button.getText().equals("X") && Integer.parseInt(button.getText()) == sNum) {
                    return button;
                }
            }
        }
        return null;
    }

    // 좌석 상태 불러오기 메서드
    private void loadSeatAvailability() {
        // DB에서 예약된 좌석 번호를 가져와서 버튼 비활성화
        String query = "SELECT s.SeatNumber FROM Seat s " +
                "JOIN Schedule sc ON s.BusID = sc.BusID " +
                "JOIN Bus b ON sc.BusID = b.BusID " +
                "WHERE sc.BusDate = ? AND sc.DepartureTime = ? " +
                "AND b.BusType = ? AND s.IsAvailable = false";

        try (Connection con = getConnection();
             PreparedStatement psS = con.prepareStatement(query)) {
            psS.setString(1, ViewReserve.bDate);
            psS.setString(2, ViewReserve.dTime);
            psS.setString(3, ViewReserve.bType);
            ResultSet rs = psS.executeQuery();

            while (rs.next()) {
                int reservedSNum = rs.getInt("SeatNumber");
                // 해당 좌석 번호의 버튼 비활성화
                JButton seatBtn = findSeatButton(reservedSNum);
                if (seatBtn != null) {
                    seatBtn.setBackground(Color.DARK_GRAY);
                    seatBtn.setText("X");
                    seatBtn.setEnabled(false);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // 좌석 테이블에 데이터 삽입하는 메서드
    private void selectSeat() {
        String insertSQ = "INSERT INTO Seat (BusID, SeatNumber, IsAvailable) VALUES (?, ?, false)";

        try (Connection con = getConnection();
             PreparedStatement psS = con.prepareStatement(insertSQ, Statement.RETURN_GENERATED_KEYS)) {
            psS.setInt(1, ViewReserve.bID); // BusID
            psS.setInt(2, sNum); // SeatNumber
            psS.executeUpdate();

            ResultSet seatKeys = psS.getGeneratedKeys();
            if (seatKeys.next()) {
                sID = seatKeys.getInt(1); 
            }

            new ViewPassenger(); // 승객 정보 입력 화면으로 이동
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
