package userManagement;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class IntegratedConvenienceApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private boolean isAdmin = false; // 기본값은 일반 사용자(false)
    private JPanel favoritesGrid; // 즐겨찾기 목록 패널
    private JPanel favoritGridPanel; // 즐겨찾기 그리드 패널
    private ArrayList<String> favorites = new ArrayList<>(); // 즐겨찾기 목록
    private ArrayList<String> categorylist = new ArrayList<>();
    private ArrayList<String> productlist = new ArrayList<>();
    private String selectCategory;

    // 현재 mySQL이 구동되어지는 서버의 Host 주소와, 스케마의 이름을 입력
    private static final String DB_URL = "jdbc:mysql://localhost:3306/convenience";
    // 구동되고 있는 mySQL 서버의 user 이름 입력
    private static final String DB_USER = "root";
    // 구동되고 있는 mySQL 서버의 password 입력
    private static final String DB_PASSWORD = "1234";

    public IntegratedConvenienceApp() {
        setTitle("Integrated Convenience Store Manager");
        setSize(450, 250);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // mainPanel 초기화
        mainPanel.add(createLoginScreen(), "login");
        mainPanel.add(createCategoryScreen(), "Category");
        mainPanel.add(createProductListScreen(), "ProductList");
        mainPanel.add(createFavoritesScreen(), "Favorites");

        add(mainPanel);
        cardLayout.show(mainPanel, "Login"); // 초기 화면 설정
        setLocationRelativeTo(null); // 화면 중앙 배치
    }

    private void switchToScreen(String screenName) {
    	cardLayout.show(mainPanel, screenName); // 화면 전환

        // 화면 이름에 따라 크기 변경
        if (screenName.equals("Login")) {
            setSize(450, 250); // 로그인 화면 크기
        } else if (screenName.equals("Category")) {
            setSize(800, 600); // 카테고리 화면 크기
        } else if (screenName.equals("ProductList")) {
            setSize(800, 600); // 제품 목록 화면 크기
        } else if (screenName.equals("Favorites")) {
            setSize(800, 600); // 즐겨찾기 화면 크기 (예시)
        }

        // 화면 중앙에 배치
        setLocationRelativeTo(null);
    }

    private JPanel createLoginScreen() {
        JPanel panel = new JPanel(null); // Null Layout 사용

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(20, 20, 100, 25);
        panel.add(idLabel);

        JTextField idField = new JTextField();
        idField.setBounds(120, 20, 150, 25);
        panel.add(idField);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(20, 60, 100, 25);
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(120, 60, 150, 25);
        panel.add(passwordField);

        JCheckBox adminCheckBox = new JCheckBox("관리자 계정");
        adminCheckBox.setBounds(120, 100, 150, 25);
        panel.add(adminCheckBox);

        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(20, 140, 100, 30);
        panel.add(loginButton);

        JButton registerButton = new JButton("회원가입");
        registerButton.setBounds(130, 140, 100, 30);
        panel.add(registerButton);

        JButton resetPasswordButton = new JButton("비밀번호 재설정");
        resetPasswordButton.setBounds(240, 140, 150, 30);
        panel.add(resetPasswordButton);

    	// 이벤트 리스너 추가
        registerButton.addActionListener(e -> register(idField.getText(), new String(passwordField.getPassword())));
        resetPasswordButton.addActionListener(e -> resetPassword(idField.getText()));

        loginButton.addActionListener(e -> {
            String id = idField.getText();
            String password = new String(passwordField.getPassword());
            if (authenticateUser(id, password)) {
                isAdmin = adminCheckBox.isSelected(); // 관리자 여부 설정
                JOptionPane.showMessageDialog(this, isAdmin ? "관리자로 로그인 성공!" : "로그인 성공!");
                switchToScreen("Category"); // 카테고리 화면으로 전환
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 잘못되었습니다.");
            }
        });


        return panel; // 완성된 로그인 화면 반환
    }

    private boolean authenticateUser(String id, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE id = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // 결과가 있으면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 연결 중 오류가 발생했습니다.");
            return false; // 오류 시 false 반환
        }
    }
    
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
            IntegratedConvenienceApp app = new IntegratedConvenienceApp();
            app.setVisible(true);
        });
    }

    // 회원가입 메서드
    private static void register(String id, String password) {
        if (!id.matches("[a-zA-Z0-9]{5,15}") || !password.matches("[a-zA-Z0-9]{8,}")) {
            JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호 형식을 확인해주세요.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkQuery = "SELECT * FROM users WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, id);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "이미 사용 중인 아이디입니다.");
                    return;
                }
            }

            String insertQuery = "INSERT INTO users (id, password, is_admin) VALUES (?, ?, FALSE)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, id);
                insertStmt.setString(2, password);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // 비밀번호 재설정 메서드
    private static void resetPassword(String id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkQuery = "SELECT * FROM users WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, id);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "존재하지 않는 아이디입니다.");
                    return;
                }
            }

            String newPassword = JOptionPane.showInputDialog("새 비밀번호를 입력하세요:");
            if (newPassword == null || !newPassword.matches("[a-zA-Z0-9]{8,}")) {
                JOptionPane.showMessageDialog(null, "비밀번호 형식을 확인해주세요.");
                return;
            }

            String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, id);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "비밀번호가 변경되었습니다!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

 	// 카테고리 화면
    private JPanel createCategoryScreen() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String testQuery = "SELECT 제품카테고리 FROM list GROUP BY 제품카테고리;";
            try (PreparedStatement checkStmt = conn.prepareStatement(testQuery)) {
            	ResultSet rs = checkStmt.executeQuery();
            	while(rs.next()) {
	                String result = rs.getString("제품카테고리");
	                categorylist.add(result);
            	}
            }
        }
        catch (SQLException ex) {
                ex.printStackTrace();
        }
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("카테고리", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);
        
        JPanel categoryPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        for (int i = 0; i < categorylist.size(); i++) {
        	String categoryName = categorylist.get(i);
            JButton categoryButton = createCustomButton(categoryName);
            ImageIcon productImage = new ImageIcon(System.getProperty("user.dir") + "\\대표카테고리이미지\\" + categoryName + ".jpg");
            Image scaledImage = productImage.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            categoryButton.setIcon(new ImageIcon(scaledImage));
            categoryButton.setText(categoryName);
            categoryButton.addActionListener(e -> {
            	cardLayout.show(mainPanel, "ProductList");
            	selectCategory = categoryName;
            	System.out.println(selectCategory);
            });
            categoryPanel.add(categoryButton);
        }
        
        JButton favoritesButton = createCustomButton("즐겨찾기");
        favoritesButton.addActionListener(e -> cardLayout.show(mainPanel, "Favorites"));

        JButton backButton = createCustomButton("뒤로가기");
        backButton.addActionListener(e -> switchToScreen("Login"));

        panel.add(categoryPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.add(favoritesButton);
        bottomPanel.add(backButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // 제품 목록 화면
    private JPanel createProductListScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("제품 목록", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // 검색 바 패널
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchBar = new JTextField(20);
        JButton searchButton = createCustomButton("검색");

        searchBarPanel.add(new JLabel("검색: "));
        searchBarPanel.add(searchBar);
        searchBarPanel.add(searchButton);

        panel.add(searchBarPanel, BorderLayout.NORTH);
        
        // 제품 목록 패널
        JPanel productPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        // 제품 목록 저장 (검색 기능에 활용)
        ArrayList<JButton> productButtons = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String productName = "제품 " + i;
            String price = (1000 * i) + "원"; // 제품 가격
            String stock = (50 - i) + "개"; // 제품 재고
            boolean onePlusOne = i % 2 == 0; // 1+1 이벤트 여부
            boolean twoPlusOne = i % 3 == 0; // 2+1 이벤트 여부

            JButton productButton = createCustomButton(productName);
            productButton.addActionListener(e -> showProductDetail(productName, price, stock, onePlusOne, twoPlusOne));
            productPanel.add(productButton);
            productButtons.add(productButton); // 버튼 저장
        }

        panel.add(new JScrollPane(productPanel), BorderLayout.CENTER);

        // 검색 버튼 액션 리스너
        searchButton.addActionListener(e -> {
            String query = searchBar.getText().trim().toLowerCase();
            productPanel.removeAll(); // 기존 목록 초기화
            for (JButton button : productButtons) {
                if (button.getText().toLowerCase().contains(query)) {
                    productPanel.add(button); // 검색 결과에 맞는 버튼만 추가
                }
            }
            productPanel.revalidate();
            productPanel.repaint();
        });

        // 뒤로가기 버튼
        JButton backButton = createCustomButton("뒤로가기");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Category"));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    // 제품 상세 화면 생성 및 이동
    private void showProductDetail(String productName, String price, String stock, boolean onePlusOne, boolean twoPlusOne) {
        JPanel productDetailScreen = createProductDetailScreen(productName, price, stock, onePlusOne, twoPlusOne);
        mainPanel.add(productDetailScreen, "ProductDetail");
        cardLayout.show(mainPanel, "ProductDetail");
    }
    
    // 제품 상세 화면
    private JPanel createProductDetailScreen(String productName, String price, String stock, boolean onePlusOne, boolean twoPlusOne) {
        JPanel panel = new JPanel(new BorderLayout());

        // 제목
        JLabel titleLabel = new JLabel("제품 상세 정보", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 이미지 패널
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(300, 300));

        // 이미지 표시
        JLabel imageLabel = new JLabel("이미지 없음", SwingConstants.CENTER);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(250, 250));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        String imageLoc = "";

        //DB에서 제품카테고리, 제품목록, 제품이미지 항목 불러오기
        /* 제품목록 있을때(예시)
        String testData1 = "커피"; //(MYSQL)제품카테고리
        String testData2 = "냉장커피"; //(MYSQL)제품목록
        String testData3 = "바닐라라떼300.jpg"; //(MYSQL)제품이미지
        */
        
        //제품목록 없을때(예시)
        String testData1 = "과일음료"; //(MYSQL)제품카테고리
        String testData2 = ""; //(MYSQL)제품목록
        String testData3 = "갈아만든배500.jpg"; //(MYSQL)제품이미지
        
        try {
            if (testData2 == "") { //제품목록 없을때
            	imageLoc = System.getProperty("user.dir") + "\\이미지파일\\" + testData1 + "\\" + testData3;
            } else { //제품목록 있을때
            	imageLoc = System.getProperty("user.dir") + "\\이미지파일\\" + testData1 + "\\" + testData2 + "\\" + testData3;
            }
            ImageIcon productImage = new ImageIcon(imageLoc);
            Image scaledImage = productImage.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText("");
            
        } catch (Exception e) {
            imageLabel.setText("이미지 없음");
        }
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // 상세 정보 패널
        JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        detailsPanel.add(new JLabel("제품 이름:"));
        detailsPanel.add(new JLabel(productName));

        detailsPanel.add(new JLabel("가격:"));
        detailsPanel.add(new JLabel(price));

        detailsPanel.add(new JLabel("재고:"));
        detailsPanel.add(new JLabel(stock));

        JPanel eventPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox onePlusOneCheckBox = new JCheckBox("1+1", onePlusOne);
        JCheckBox twoPlusOneCheckBox = new JCheckBox("2+1", twoPlusOne);

        // 관리자 여부에 따라 체크박스 활성화
        onePlusOneCheckBox.setEnabled(isAdmin);
        twoPlusOneCheckBox.setEnabled(isAdmin);

        eventPanel.add(new JLabel("이벤트:"));
        eventPanel.add(onePlusOneCheckBox);
        eventPanel.add(twoPlusOneCheckBox);
        detailsPanel.add(new JLabel("이벤트:"));
        detailsPanel.add(eventPanel);

        // 이미지와 상세 정보 패널을 JSplitPane으로 구분
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, detailsPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        panel.add(splitPane, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton favoriteButton = createCustomButton("★ 즐겨찾기 추가");
        favoriteButton.addActionListener(e -> addFavorite(productName));

        JButton backButton = createCustomButton("뒤로가기");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ProductList"));

        buttonPanel.add(favoriteButton);
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // 즐겨찾기 화면
    private JPanel createFavoritesScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("즐겨찾기", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // 즐겨찾기 그리드 초기화
        if (favoritGridPanel == null) {
            favoritGridPanel = new JPanel(new GridLayout(4, 3, 10, 10)); // 4행 3열 그리드
        }
        updateFavoritesGrid(); // 초기 즐겨찾기 목록 갱신

        JScrollPane scrollPane = new JScrollPane(favoritGridPanel); // 스크롤 추가
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // 즐겨찾기 목록 업데이트 메서드
    private void updateFavoritesGrid() {
        if (favoritGridPanel == null) {
            System.err.println("favoritGridPanel이 초기화되지 않았습니다!");
            return;
        }

        favoritGridPanel.removeAll(); // 기존 내용 제거

        for (String favorite : favorites) {
            JButton productButton = new JButton(favorite);
            productButton.addActionListener(e -> {
                // 제품 상세 페이지로 이동
                String productName = favorite;
                String price = "가격 미정"; // 실제 데이터베이스에서 가져올 수 있음
                String stock = "재고 미정"; // 실제 데이터베이스에서 가져올 수 있음
                boolean onePlusOne = false; // 기본 값
                boolean twoPlusOne = false; // 기본 값

                // 상세 페이지 표시
                showProductDetail(productName, price, stock, onePlusOne, twoPlusOne);
            });
            favoritGridPanel.add(productButton);
        }

        // 빈 칸 채우기 (최대 12개)
        int emptySlots = 12 - favorites.size();
        for (int i = 0; i < emptySlots; i++) {
            favoritGridPanel.add(new JPanel()); // 빈 패널 추가
        }

        favoritGridPanel.revalidate();
        favoritGridPanel.repaint();
    }

    // 즐겨찾기 추가 메서드 수정
    private void addFavorite(String productName) {
        if (!favorites.contains(productName)) {
            favorites.add(productName);
            updateFavoritesGrid(); // 즐겨찾기 화면 갱신
            JOptionPane.showMessageDialog(this, productName + "이(가) 즐겨찾기에 추가되었습니다!");
        } else {
            JOptionPane.showMessageDialog(this, "이미 추가된 즐겨찾기입니다.");
        }
    }

    // 즐겨찾기 화면 갱신 메서드
    private void updateFavoritesScreen() {
        JPanel favoritesScreen = createFavoritesScreen();
        mainPanel.add(favoritesScreen, "Favorites"); // 기존 화면 덮어쓰기
        cardLayout.show(mainPanel, "Favorites");
    }

    private JButton createCustomButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }
}