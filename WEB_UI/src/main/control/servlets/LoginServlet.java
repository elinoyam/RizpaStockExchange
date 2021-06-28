package main.control.servlets;

import com.sun.javaws.exceptions.InvalidArgumentException;
import users.UsersManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpSession;


public class LoginServlet extends HttpServlet {

    private final String Dash_Board_URL = "../../webapp/pages/dashboard/dashboard.html"; //page no. 2

    private final String Login_Error = "<script><>";

    public boolean inSession(HttpServletRequest request,HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if(session!=null){
            response.sendRedirect(Dash_Board_URL);
            return true;
        }
        return false;
    }


    public boolean login(HttpServletRequest request, HttpServletResponse response) {
        String name =  request.getParameter("name");
        boolean isAdmin = Boolean.parseBoolean(request.getParameter("is_admin"));
        try (PrintWriter out = response.getWriter()) {
            if(name == null){
                out.println("You must enter a unique user name to enter the system.");
                return false;
            }

            UsersManager usersManager = engine.Engine.getInstance().getUsersManager();
            if (usersManager.isExists(name)) {
                out.println("error html js change"); // TODO: update to the real html line to add
                return false;
            } else {

                usersManager.addUser(name, isAdmin);
                HttpSession newSession = request.getSession(true);
                newSession.setAttribute("username", name);
                newSession.setAttribute("is_admin", isAdmin);
                response.sendRedirect(Dash_Board_URL);
                return true;
            }

        } catch (IOException | InvalidArgumentException ex) {
            return false;
        }

    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
            login(request, response);
        }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        login(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
