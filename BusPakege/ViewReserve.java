package BusPakege;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Calendar;
import static BusPakege.BusFunc.*;

public class ViewReserve extends JFrame {
    static int bID; // 버스 ID(기본 키)
    static String bType; // 버스 종류
    static String departure; // 출발지
    static String arrival;  // 도착지
    static String bDate; // 운행 날짜
    static String dTime; // 출발 시간
    static String aTime; // 도착 시간

    private JButton selectedBTypeBtn; // 선택된 버스 종류 버튼
    private JButton selectedDBtn; // 선택된 출발지 버튼
    private JButton selectedABtn; // 선택된 도착지 버튼
    private final String[] dTimes =
            {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "18:00", "20:00"}; // 출발 시간 배열

    private final JComboBox<String> dTimeCombo; // 출발 시간 드롭 다운 박스
    private final JLabel aTimeL; // 도착 시간 라벨
    private final JDatePickerImpl datePicker; // 날짜 선택기
    private java.sql.Date today = new java.sql.Date(System.currentTimeMillis()); // 오늘 날짜

    // 예매 정보 입력 화면
    public ViewReserve() {
        setTitle("버스 예매 - 출발지 및 도착지 선택");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768); // 창 크기 설정
        setLocationRelativeTo(null); // 가운데 창 띄우기
        setLayout(new BorderLayout());

        // 상단 타이틀
        JLabel titleLabel = new JLabel("출발지 및 도착지 선택", SwingConstants.CENTER);
        titleLabel.setFont(boldFont(24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBackground(new Color(70, 130, 180)); // 파란색 배경
        titleLabel.setOpaque(true);
        add(titleLabel, BorderLayout.NORTH);

        // 중앙 패널 (왼쪽: 출발지 및 도착지 선택, 오른쪽: 날짜 및 시간 선택)
        JPanel mainP = new JPanel(new GridLayout(1, 2)); // 1 X 2 그리드
        mainP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 왼쪽 패널 (버스 종류 및 출발지/도착지 선택)
        JPanel leftP = new JPanel();
        leftP.setLayout(new GridLayout(4, 1, 10, 10)); // 4 X 1 그리드

        // 버스 종류 선택 패널
        JPanel bTypeP = new JPanel(new GridLayout(1, 2, 20, 20)); // 1 X 2 그리드
        JLabel bTypeL = new JLabel("버스 종류 선택:");
        bTypeL.setHorizontalAlignment(SwingConstants.CENTER); // 라벨 중앙 정렬
        bTypeL.setFont(boldFont(20));

        // 버스 종류 선택 버튼
        JButton btnSt = createBTypeBtn("일반", new Color(114, 210, 255)); // 일반 버튼
        JButton btnPr = createBTypeBtn("우등", new Color(255, 167, 138));  // 우등 버튼

        bTypeP.add(btnSt);
        bTypeP.add(btnPr);

        leftP.add(bTypeL);
        leftP.add(bTypeP);

        // 출발지 선택 버튼 패널
        JPanel departureP = createCSP("출발지 선택", e -> {
            if (selectedDBtn != null) {
                selectedDBtn.setBackground(new Color(114, 210, 255)); // 이전 선택된 버튼 색상 원래대로
            }
            selectedDBtn = (JButton) e.getSource();
            selectedDBtn.setBackground(new Color(30, 144, 255)); // 선택된 버튼 색상 변경
            departure = selectedDBtn.getText();
        });

        // 도착지 선택 버튼 패널
        JPanel arrivalP = createCSP("도착지 선택", e -> {
            if (selectedABtn != null) {
                selectedABtn.setBackground(new Color(114, 210, 255)); // 이전 선택된 버튼 색상 원래대로
            }
            selectedABtn = (JButton) e.getSource();
            selectedABtn.setBackground(new Color(30, 144, 255)); // 선택된 버튼 색상 변경
            arrival = selectedABtn.getText();
        });

        leftP.add(departureP); // 출발지 패널 추가
        leftP.add(arrivalP); // 도착지 패널 추가

        mainP.add(leftP); // 왼쪽 패널 추가

        // 오른쪽 패널 (날짜 및 시간 선택)
        JPanel rightP = new JPanel();
        rightP.setLayout(new GridLayout(6, 1, 10, 10)); // 6행 1열로 변경

        // 날짜 선택 패널 (JDatePicker 사용)
        JLabel dateL = new JLabel("날짜 선택:");
        dateL.setHorizontalAlignment(SwingConstants.CENTER);
        dateL.setFont(boldFont(20));

        // 날짜 선택기 설정
        SqlDateModel model = new SqlDateModel();
        JDatePanelImpl dateP = new JDatePanelImpl(model);

        // 달력 형태의 날짜 선택기(외부 라이브러리 사용)
        datePicker = new JDatePickerImpl(dateP);
        datePicker.addActionListener(e-> updateBDate());
        rightP.add(dateL);
        rightP.add(datePicker);

        // JDatePicker의 내부 날짜 표시 JLabel에 폰트 설정
        JFormattedTextField editor = datePicker.getJFormattedTextField();
        if (editor != null)
            editor.setFont(boldFont(20));

        // 출발 시간 선택 패널
        JLabel timeL = new JLabel("출발 시간 선택:");
        timeL.setHorizontalAlignment(SwingConstants.CENTER);
        timeL.setFont(boldFont(20));

        dTimeCombo = new JComboBox<>(dTimes);
        dTimeCombo.setFont(boldFont(20));
        dTimeCombo.addActionListener(this::updateATime);

        dTimeCombo.removeAllItems(); // 출발 시간 드롭 박스 리스트 초기화

        rightP.add(timeL);
        rightP.add(dTimeCombo);

        // "도착 시간" 표시 라벨
        JLabel aTimeTL = new JLabel("도착 시간:");
        aTimeTL.setHorizontalAlignment(SwingConstants.CENTER);
        aTimeTL.setFont(boldFont(20));

        this.aTimeL = new JLabel("");
        this.aTimeL.setHorizontalAlignment(SwingConstants.CENTER);
        this.aTimeL.setFont(boldFont(20));

        rightP.add(aTimeTL);
        rightP.add(this.aTimeL);

        mainP.add(rightP);

        add(mainP, BorderLayout.CENTER);

        // 하단 버튼 (이전/다음 버튼)
        JPanel btnP = new JPanel();

        // 이전 버튼
        JButton backBtn = createBtn("이전");
        backBtn.addActionListener(e -> {
            new ViewMain(); // 이전 화면으로 이동
            dispose(); // 현재 창 닫기
        });

        // 다음 버튼
        JButton nextBtn = createBtn("다음");
        nextBtn.addActionListener(e -> {
            // 모든 항목이 선택되었는지 확인
            if (bType == null || departure == null || arrival == null ||
                    dTimeCombo.getSelectedItem() == null || dTimeCombo.getSelectedItem().toString().isEmpty()) {
                JOptionPane.showMessageDialog(this, "선택하지 않은 항목이 있습니다. 모든 항목을 선택해주세요.", "항목 선택 오류" , JOptionPane.ERROR_MESSAGE);
            } else if (departure.equals(arrival)) {
                JOptionPane.showMessageDialog(this, "출발지와 도착지가 동일할 수 없습니다.", "노선 선택 오류" , JOptionPane.ERROR_MESSAGE);
            } else {
                // 버스 테이블
                try (Connection con = getConnection()) {
                    String insertBQ = "INSERT INTO Bus (BusType) VALUES (?)";
                    PreparedStatement psB = con.prepareStatement(insertBQ, Statement.RETURN_GENERATED_KEYS);
                    psB.setString(1, bType);

                    psB.executeUpdate();

                    try (ResultSet bKeys = psB.getGeneratedKeys()) {
                        bID = -1;
                        if (bKeys.next()) {
                            bID = bKeys.getInt(1);
                        }

                        new ViewSeatSelection(); // 좌석 선택 화면으로 이동
                        dispose(); // 현재 창 닫기
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "버스 정보를 저장하는 중 오류가 발생했습니다.", "DB 오류" , JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnP.add(backBtn);
        btnP.add(nextBtn);

        add(btnP, BorderLayout.SOUTH);

        updateATime(null); // 도착 시간 초기화

        setVisible(true);
    }

    // 버스 종류 버튼 생성 메서드
    private JButton createBTypeBtn(String type, Color color) {
        JButton bTypeBtn = new JButton(type);
        bTypeBtn.setFont(boldFont(20));
        bTypeBtn.setBackground(color);
        bTypeBtn.setForeground(Color.WHITE);
        bTypeBtn.setFocusPainted(false);
        bTypeBtn.addActionListener(e -> {
            if (selectedBTypeBtn != null) {
                selectedBTypeBtn.setBackground(selectedBTypeBtn.getText().equals("일반")
                        ? new Color(114, 210, 255)
                        : new Color(255, 167, 138)); // 버튼 색 원래대로
            }
            selectedBTypeBtn = bTypeBtn;
            selectedBTypeBtn.setBackground(type.equals("일반") ? new Color(30, 144, 255)
                    : new Color(250, 136, 62)); // 선택된 버튼 색상 변경
            bType = type;
        });
        return bTypeBtn;
    }

    // 출발지/도착지 선택 버튼 패널 생성 메서드
    private JPanel createCSP(String labelText, java.awt.event.ActionListener e) {
        JPanel CSP = new JPanel(new BorderLayout());

        JLabel CSL = new JLabel(labelText, SwingConstants.CENTER);
        CSL.setFont(boldFont(20));
        CSP.add(CSL, BorderLayout.NORTH);

        JPanel btnGrid = new JPanel(new GridLayout(2, 4, 10, 10)); // 2x4 그리드로 배치
        String[] cities = {"서울", "부산", "대구", "광주", "인천", "대전", "울산"};

        for (String city : cities) {
            JButton cityBtn = new JButton(city);
            cityBtn.setFont(new Font("맑은 고딕", Font.BOLD, 20));
            cityBtn.setBackground(new Color(114, 210, 255)); // 버튼 색상 설정
            cityBtn.setForeground(Color.WHITE);
            cityBtn.setFocusPainted(false);
            cityBtn.addActionListener(e);
            btnGrid.add(cityBtn);
        }

        CSP.add(btnGrid, BorderLayout.CENTER);
        return CSP;
    }

    // 운행 날짜 업데이트 메서드
    private void updateBDate() {
        java.sql.Date selectedDate = (java.sql.Date) datePicker.getModel().getValue();
        today = new java.sql.Date(System.currentTimeMillis());

        if (selectedDate != null) {
            // 시간 정보 없이 날짜만 비교하기 위해 Calendar 객체 사용
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);
            Calendar todayCal = Calendar.getInstance();
            todayCal.setTime(today);

            // 년, 월, 일만 비교 (시간 정보는 무시)
            boolean isPastDate = selectedCal.get(Calendar.YEAR) < todayCal.get(Calendar.YEAR) ||
                    (selectedCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                            (selectedCal.get(Calendar.MONTH) < todayCal.get(Calendar.MONTH) ||
                                    (selectedCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                                            selectedCal.get(Calendar.DAY_OF_MONTH) < todayCal.get(Calendar.DAY_OF_MONTH))));

            if (isPastDate) { // 지난 날짜 선택 시
                JOptionPane.showMessageDialog(this, "지난 날짜로 예매는 불가능합니다.", "날짜 선택 오류" , JOptionPane.ERROR_MESSAGE);
                dTimeCombo.removeAllItems(); // 출발 시간 드롭박스 초기화
                aTimeL.setText(""); // 도착 시간 초기화
                datePicker.getModel().setValue(null); // 날짜 선택기 초기화
            } else {
                bDate = selectedDate.toString();
                aTimeL.setText(""); // 도착 시간 초기화
                // 출발 시간 드롭다운 업데이트
                updateDTime(); // 드롭다운 업데이트
            }
        } else {
            // 날짜가 선택되지 않은 경우 출발 시간 및 도착 시간을 초기화
            dTimeCombo.removeAllItems(); // 출발 시간 드롭박스 초기화
            aTimeL.setText(""); // 도착 시간 초기화
        }
    }

    // 출발 시간 업데이트 메서드
    private void updateDTime() {
        dTimeCombo.removeAllItems(); // 출발 시간 드롭박스 리스트 초기화
        today = new java.sql.Date(System.currentTimeMillis());
        if (bDate != null && bDate.equals(today.toString())) {
            // 오늘 날짜인 경우
            java.util.Calendar now = java.util.Calendar.getInstance();
            int currentH = now.get(java.util.Calendar.HOUR_OF_DAY);
            int currentM = now.get(java.util.Calendar.MINUTE);

            boolean isAvailable = false; // 선택 가능한 시간이 있는지 확인

            for (String time : dTimes) {
                String[] timeParts = time.split(":");
                int selectedHour = Integer.parseInt(timeParts[0]);
                int selectedMinute = Integer.parseInt(timeParts[1]);

                // 이미 지난 시간은 리스트에 추가하지 않음
                if (selectedHour > currentH || (selectedHour == currentH && selectedMinute >= currentM)) {
                    dTimeCombo.addItem(time); // 현재 시간 이후의 시간만 추가
                    isAvailable = true; // 선택 가능한 시간이 있음을 표시
                }
            }

            // 선택 가능한 시간이 없는 경우
            if (!isAvailable) {
                JOptionPane.showMessageDialog(this, "선택 가능한 시간이 없습니다.", "시간 선택 오류" , JOptionPane.ERROR_MESSAGE);
                // 날짜 선택기 및 도착 시간 초기화
                datePicker.getModel().setValue(null); // 날짜 선택기 초기화
                aTimeL.setText(""); // 도착 시간 초기화
            }
        } else {
            // 다른 날짜인 경우 모든 요소를 다시 추가
            for (String time : dTimes) {
                dTimeCombo.addItem(time);
            }
        }
    }
    
    // 도착 시간 업데이트 메서드
    private void updateATime(ActionEvent e) {
        String selectedDTime = (String) dTimeCombo.getSelectedItem();
        dTime = selectedDTime; // 선택한 출발 시간을 저장
        if (selectedDTime != null) {
            // 출발 시간에 따라 도착 시간을 계산 (2시간 후 고정)
            String[] timeParts = selectedDTime.split(":");
            int hours = Integer.parseInt(timeParts[0]) + 2; // 예를 들어, 2시간 후 도착
            if (hours >= 24) {
                hours -= 24; // 하루를 넘기면 다시 0으로 돌아감
            }
            String arrivalTime = String.format("%02d:%s", hours, timeParts[1]);
            aTimeL.setText(arrivalTime);
            aTime = arrivalTime; // 계산된 도착 시간을 저장
        }
    }
}
