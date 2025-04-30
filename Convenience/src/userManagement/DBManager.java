package userManagement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

public class DBManager {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // MySQL JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // MySQL 연결
            String url = "jdbc:mysql://localhost/worker";
            conn = DriverManager.getConnection(url, "root", "1234");

            stmt = conn.createStatement();

            // SQL 쿼리 실행
            String sql = "SELECT 제품이미지 FROM list2";

            rs = stmt.executeQuery(sql);

            // 이미지 파일 경로 (상대 경로)
            String imageDirectory = "./이미지파일/물/"; // 상대 경로 설정

            // 이미지 리스트 저장
            List<BufferedImage> images = new ArrayList<>();

            while (rs.next()) {
                // 데이터베이스에서 이미지 파일명을 가져오기
                String imageName = rs.getString("제품이미지");
                System.out.println("몽베스트500" + imageName);

                // 이미지 로드 (로컬 디렉토리에서)
                File imageFile = new File(imageDirectory + imageName);
                if (imageFile.exists()) {
                    BufferedImage image = ImageIO.read(imageFile);
                    System.out.println("Image successfully loaded: " + imageName);
                    images.add(image);
                } else {
                    System.out.println("Image file not found: " + imageName);
                }
            }

            // GUI로 모든 이미지를 4x3 그리드로 표시
            displayImagesInGrid(images, 4, 3);

        } catch (ClassNotFoundException e) {
            System.out.println("Driver loading failed.");
        } catch (SQLException e) {
            System.out.println("SQL Error >>> " + e);
        } catch (Exception e) {
            System.out.println("Error loading image: " + e);
        } finally {
            // 자원 해제
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
                if (stmt != null && !stmt.isClosed()) {
                    stmt.close();
                }
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 여러 이미지를 GridLayout으로 표시
     * @param images BufferedImage 리스트
     * @param rows 그리드의 행 개수
     * @param cols 그리드의 열 개수
     */
    private static void displayImagesInGrid(List<BufferedImage> images, int rows, int cols) {
        JFrame frame = new JFrame("Product Images");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.GridLayout(rows, cols, 5, 5)); // 4x3 레이아웃, 간격 5px

        for (BufferedImage image : images) {
            // 각 이미지를 JLabel에 추가
            JLabel label = new JLabel(new ImageIcon(image));
            panel.add(label);
        }

        frame.add(new JScrollPane(panel)); // 스크롤 가능
        frame.setVisible(true);
    }
}
