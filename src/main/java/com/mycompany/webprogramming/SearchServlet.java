package com.mycompany.webprogramming;

import com.mycompany.webprogramming.SimpleIndexer;
import com.mycompany.webprogramming.TestDocument;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;


/**
 *
 * @author Asus
 */
@WebServlet(urlPatterns = {"/com.mycompany.webprogramming.SearchServlet"})
public class SearchServlet extends HttpServlet {

     
     @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {

        PrintWriter out = resp.getWriter();

        String searchbox = req.getParameter("searchbox");
        if (searchbox == null) {
            out.println("No search parameter");
            return;
        }

        String[] searchWords = searchbox.split("\\s+");

        if (searchWords.length == 0) {
            out.println("Empty search parameter");
            return;
        }

        List<TestDocument> resultDocuments = getDocumentsBySearchWords(searchWords);


        if (resultDocuments.isEmpty()) {
            printNoFoundDocumentsMessage(out, searchbox);
        } else {
            printFoundDocuments(out, resultDocuments);
        }

    }

    private List<TestDocument> getDocumentsBySearchWords(String[] searchWords) {
        List<TestDocument> resultDocuments = SimpleIndexer.getInstance().search(searchWords[0]);

        for (int i = 1; i < searchWords.length; ++i) {
            if (resultDocuments.isEmpty()) break;

            resultDocuments = SimpleIndexer.getInstance().search(resultDocuments, searchWords[i]);
        }

        return resultDocuments;
    }

    private void printNoFoundDocumentsMessage(PrintWriter out, String searchbox) {
        out.println("No documents were found with " + searchbox + " parametr");
    }

    private void printFoundDocuments(PrintWriter out, List<TestDocument> resultDocuments) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Search result</title>");
        out.println("</head>");
        out.println("<body bgcolor=\"white\">");

        for (TestDocument document : resultDocuments) {
            out.println("<span>");
            out.println("<p>Title: " + document.getTitle() + "</p>");
            out.println("<p>" + document.getText() + "</p>");
            out.println("</span>");

        }

        out.println("</body>");
        out.println("</html>");
    }

}