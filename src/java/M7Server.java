/**
 * MIT License
 *
 * Copyright (c) 2016 David Hinchliffe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import raspiworks.exceptions.ChannelNotReadyException;
import raspiworks.exceptions.InvalidChannelException;
import raspiworks.exceptions.InvalidCommandException;
import raspiworks.client.M7ServerClient;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class M7Server extends HttpServlet
{
    M7ServerClient M7Client;
    /**
     * Initializes the client and controller by lazy instantiating M7ServerClient & calling the initialize method
     * @param config
     * @throws ServletException 
     */
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        M7Client=new M7ServerClient();
        try{
            M7Client.executeClientCommand("initialize");
        }
        catch(InvalidCommandException e){
            //manually passing a command so it is guaranteed to be valid  
            System.err.println(e);
        }
    }
    @Override
    public void destroy()
    {
        
    }
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    protected void processRequest(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        String message;
        response.setContentType("text/html;charset=UTF8");
        //Enumeration<String>commands=request.getParameterNames();
        Map<String,String[]>commands=request.getParameterMap();
        commands.size();
        
        response.setContentType("text/html;charset=UTF-8");
        
        //try with resource will close the response.getWriter when finished with it.  
        try(PrintWriter out=response.getWriter()){
            //get all of the keys
            if(commands.isEmpty()){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"No command issued");
            }
            else{
                for(String command:commands.keySet()){
                        String parameterValues[]=commands.get(command);
                        //loop through the channel number parameters     
                        for(String channel:parameterValues){
                                try{
                                    if (channel.isEmpty()){//if there isn't a parameter passed, a blank string is present for the parameter so size is >0
                                        out.println(M7Client.executeClientCommand(command.toLowerCase().trim()));
                                    }else //a command and channel number was passed 
                                        out.println(M7Client.executeClientCommand(command.toLowerCase().trim(),Integer.parseInt(channel.trim())));
                                }catch(NumberFormatException|NullPointerException|ChannelNotReadyException e){
                                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.toString());
                                }catch(InvalidChannelException|InvalidCommandException e){
                                    response.sendError(HttpServletResponse.SC_NOT_FOUND,e.toString());

                                }
                            }
                        //}
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
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
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

}


