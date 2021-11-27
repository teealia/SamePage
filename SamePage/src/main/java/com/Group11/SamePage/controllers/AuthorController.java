package com.Group11.SamePage.controllers;

import com.Group11.SamePage.Books.Submission;
import com.Group11.SamePage.Users.Author;
import com.Group11.SamePage.repositories.BookRepository;
import com.Group11.SamePage.repositories.SubmissionRepository;
import com.Group11.SamePage.repositories.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.Calendar;

@Controller
public class AuthorController {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final BookRepository bookRepository;

    public AuthorController(UserRepository userRepository, SubmissionRepository submissionRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/api/submit")
    public @ResponseBody Submission submit(@RequestBody String jsonString){

        Submission submission = null;

        try {
            JSONObject obj = new JSONObject(jsonString);

            Integer userID = obj.getInt("userID");  //author
            Integer bookID = obj.getInt("bookID");  //current book
            String title = obj.getString("title");  //title of submission
            Integer chapterNum = obj.getInt("chapterNum");  //current book chapter
            String body = obj.getString("body");    //text of submission

            //estimated date of completion
            Integer year = obj.getInt("year");
            Integer month = obj.getInt("month");
            Integer day = obj.getInt("day");

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month-1); //start of month
            cal.set(Calendar.DAY_OF_MONTH, day);
            Date date = new Date(cal.getTimeInMillis());

            submission = new Submission(userRepository.findByID(userID), bookID, title, chapterNum, body, 0,
                    false, date, body.length(), body.split(" ").length);

            submissionRepository.save(submission);

        }catch (Exception e){
            System.out.println(e);
        }

        return submission;
    }

    @PostMapping("/api/edit")
    public @ResponseBody void edit(@RequestBody String jsonString) {

        try {
            JSONObject obj = new JSONObject(jsonString);

            Integer userID = obj.getInt("userID");  //author or editor
            Integer submissionID = obj.getInt("submissionID"); //current submission to edit

            /*
            check if they are the author of the submission
            OR if they are an editor of the book

            if( submissionRepository.findByID(submissionID).getAuthor().getId() == userID ||
                bookRepository.checkEditor(userID, bookID) != null)
            {

            }

             */

            String title = obj.getString("title");  //title of submission
            String body = obj.getString("body");    //text of submission

            //estimated date of completion
            Integer year = obj.getInt("year");
            Integer month = obj.getInt("month");
            Integer day = obj.getInt("day");

            Submission temp = submissionRepository.findByID(submissionID);
            Date date = null;

            //If they didn't edit an area
            if(title.isEmpty())
                title = temp.getTitle();
            if(body.isEmpty())
                title = temp.getBody();
            if(year == 0 && month == 0 && day == 0)
                date = temp.getEstimatedTime();
            else
            {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month-1); //start of month
                cal.set(Calendar.DAY_OF_MONTH, day);
                date = new Date(cal.getTimeInMillis());
            }

            submissionRepository.editSubmission(submissionID, title, body, date, body.length(), body.split(" ").length);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
