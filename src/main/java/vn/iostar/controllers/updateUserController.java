package vn.iostar.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iostar.config.DBConnectSQL;
import vn.iostar.models.User;

@WebServlet(urlPatterns ="/updateUser")
public class updateUserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection conn = null;
	private PreparedStatement ps = null;

	// Phương thức để hiển thị trang
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("account");

		if (user != null) {
			req.setAttribute("fullname", user.getFullName());
			req.setAttribute("phone", user.getPhone());
			req.getRequestDispatcher("/views/multiPartServlet.jsp").forward(req, resp);
		} else {
			// Nếu không có thông tin người dùng, chuyển hướng về trang đăng nhập
			resp.sendRedirect(req.getContextPath() + "/login");
		}
	}

	// Phương thức để cập nhật thông tin người dùng
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("account");

		if (user != null) {
			// Lấy thông tin mới từ biểu mẫu
			String newFullName = req.getParameter("fullname");
			String newPhone = req.getParameter("phone");

			// Câu lệnh SQL để cập nhật thông tin
			String sql = "UPDATE [User] SET fullname = ?, phone = ? WHERE username = ?";
			try {
				conn = new DBConnectSQL().getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, newFullName);
				ps.setString(2, newPhone);
				ps.setString(3, user.getUserName());
				ps.executeUpdate();

				// Cập nhật lại thông tin trong session
				user.setFullName(newFullName);
				user.setPhone(newPhone);
				session.setAttribute("account", user);
				
				// Chuyển hướng lại đến trang multiPartServlet
				resp.sendRedirect(req.getContextPath() + "/multiPartServlet");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// Đảm bảo đóng kết nối
				try {
					if (conn != null)
						conn.close();
					if (ps != null)
						ps.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			// Nếu không có thông tin người dùng, chuyển hướng về trang đăng nhập
			resp.sendRedirect(req.getContextPath() + "/login");
		}
	}
}
