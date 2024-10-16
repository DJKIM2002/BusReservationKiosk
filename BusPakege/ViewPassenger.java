package BusPakege;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import static BusPakege.BusFunc.*;

public class ViewPassenger extends JFrame {
    static int pID; // 승객 ID(기본 키)
    static String pType; // 승객 타입
    static String pLogID; // 승객 로그인 ID

    private final JTextField loginIDF; // 로그인 ID 입력 필드
    private final JRadioButton adultBtn; // 성인 라디오 버튼
    private final JRadioButton youthBtn; // 청소년 라디오 버튼
    private final JRadioButton childBtn; // 어린이 라디오 버튼
    private final JRadioButton seniorBtn; // 노인 라디오 버튼
    private final JRadioButton disabledBtn; // 장애인 라디오 버튼

    // 승객 정보 입력 화면
    public ViewPassenger() {
        setTitle("버스 예매 - 승객 정보 입력");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768); // 창 크기 설정
        setLocationRelativeTo(null); // 가운데 창 띄우기
        setLayout(new BorderLayout());

        // 상단 타이틀
        JLabel titleL = new JLabel("승객 정보 입력", SwingConstants.CENTER);
        titleL.setFont(boldFont(30));
        titleL.setForeground(Color.WHITE);
        titleL.setBackground(new Color(70, 130, 180));
        titleL.setOpaque(true);
        add(titleL, BorderLayout.NORTH);

        // 승객 정보 입력 패널
        JPanel infoP = new JPanel();
        infoP.setLayout(new GridLayout(5, 2, 10, 10)); // 5 X 2 그리드
        infoP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 모든 방향에 20의 패딩 설정

        // 사용자 아이디 입력 패널
        JLabel loginIDL = new JLabel("사용자 ID 입력:");
        loginIDL.setFont(boldFont(20));
        infoP.add(loginIDL);

        loginIDF = new JTextField();
        loginIDF.setFont(boldFont(20));
        infoP.add(loginIDF);

        // 승객 유형 라벨
        JLabel pTypeL = new JLabel("승객 유형:");
        pTypeL.setFont(boldFont(20));
        infoP.add(pTypeL);

        // 승객 유형 패널, 버튼
        JPanel typeP = new JPanel(new FlowLayout());
        adultBtn = new JRadioButton("성인", true);
        adultBtn.setFont(boldFont(20));
        youthBtn = new JRadioButton("청소년");
        youthBtn.setFont(boldFont(20));
        childBtn = new JRadioButton("어린이");
        childBtn.setFont(boldFont(20));
        seniorBtn = new JRadioButton("노인");
        seniorBtn.setFont(boldFont(20));
        disabledBtn = new JRadioButton("장애인");
        disabledBtn.setFont(boldFont(20));

        // 승객 유형에 따른 할인 정보를 알려주는 라벨
        JLabel disconutL = new JLabel("<html>일반 : 16000원 / 우등 : 19000원 <br/>" +
                "청소년 : 30% 할인, 어린이 : 20% 할인, 노인 : 무료, 장애인 : 50% 할인</html>");
        disconutL.setFont(boldFont(20));

        // 승객 유형 라디오 버튼 그룹
        ButtonGroup pTypeG = new ButtonGroup();
        pTypeG.add(adultBtn);
        pTypeG.add(youthBtn);
        pTypeG.add(childBtn);
        pTypeG.add(seniorBtn);
        pTypeG.add(disabledBtn);

        typeP.add(adultBtn);
        typeP.add(youthBtn);
        typeP.add(childBtn);
        typeP.add(seniorBtn);
        typeP.add(disabledBtn);
        typeP.add(disconutL);

        infoP.add(typeP);
        add(infoP, BorderLayout.CENTER);

        // 하단 버튼 패널(이전, 다음)
        JPanel btnP = new JPanel();
        JButton backBtn = createBtn("이전");
        backBtn.addActionListener(e -> {
            new ViewSeatSelection();
            dispose();
        });
        btnP.add(backBtn);

        JButton nextBtn = createBtn("다음");
        nextBtn.addActionListener(e -> handleNextBtn());
        btnP.add(nextBtn);

        add(btnP, BorderLayout.SOUTH);

        // 창을 보이게 함
        setVisible(true);
    }

    private void handleNextBtn() {
        pLogID = loginIDF.getText().trim();

        // 사용자 아이디 길이 검증 (10자리 이하)
        if (pLogID.length() > 10) {
            JOptionPane.showMessageDialog(this, "사용자 아이디는 10자리를 넘을 수 없습니다.", "ID 입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pLogID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "사용자 아이디를 입력해주세요.", "ID 입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 선택한 승객 유형
        pType = "성인";  // 기본값
        if (youthBtn.isSelected()) {
            pType = "청소년";
        } else if (childBtn.isSelected()) {
            pType = "어린이";
        } else if (seniorBtn.isSelected()) {
            pType = "노인";
        } else if (disabledBtn.isSelected()) {
            pType = "장애인";
        }

        insertPassenger(pLogID, pType);
        new ViewPayment(); // 결제 화면 이동
        dispose();
    }

    // 승객 테이블에 데이터 삽입하는 메서드
    private void insertPassenger(String pLogID, String pType) {
        String query = "INSERT INTO Passenger (PassengerLogID, PassengerType) VALUES (?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) { // 키 반환 요청
            ps.setString(1, pLogID);
            ps.setString(2, pType);
            ps.executeUpdate();
            
            ResultSet gKeys = ps.getGeneratedKeys();
            if (gKeys.next()) {
                pID = gKeys.getInt(1); // static 변수에 저장
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
