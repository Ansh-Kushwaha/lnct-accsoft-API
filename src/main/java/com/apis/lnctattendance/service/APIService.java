package com.apis.lnctattendance.service;

import com.apis.lnctattendance.model.OverallStatistics;
import com.apis.lnctattendance.model.StudentInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class APIService {

    private Document login(String username, String password) throws IOException {
        Document loginPage = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/StudentLogin.aspx").get();
        String viewState = loginPage.select("[name=__VIEWSTATE]").val();
        String eventValidation = loginPage.select("[name=__EVENTVALIDATION]").val();

        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("ctl00$ScriptManager1", "ctl00$cph1$UpdatePanel5|ctl00$cph1$btnStuLogin");
        loginPayload.put("__EVENTTARGET", "");
        loginPayload.put("__EVENTARGUMENT", "");
        loginPayload.put("__LASTFOCUS", "");
        loginPayload.put("__VIEWSTATE", viewState);
        loginPayload.put("__EVENTVALIDATION", eventValidation);
        loginPayload.put("ctl00$cph1$rdbtnlType", "2");
        loginPayload.put("ctl00$cph1$hdnSID", "");
        loginPayload.put("ctl00$cph1$hdnSNO", "");
        loginPayload.put("ctl00$cph1$hdnRDURL", "");
        loginPayload.put("ctl00$cph1$txtStuUser", String.valueOf(username));
        loginPayload.put("ctl00$cph1$txtStuPsw", password);
        loginPayload.put("__ASYNCPOST", "True");
        loginPayload.put("ctl00$cph1$btnStuLogin", "Login >>");

        return Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/StudentLogin.aspx")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36 Edg/111.0.1661.62")
                .header("Referer", "https://portal.lnct.ac.in/")
                .data(loginPayload)
                .post();
    }

    public OverallStatistics getOverallAttendance(String username, String password) {
        OverallStatistics overallStatistics = null;

        try {
            Document loginResponse = login(username, password);
            boolean loginState = !loginResponse.select("a#alertsDropdown:contains(" + username + ")").isEmpty();

            if (loginState) {
                Document attendancePage = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/parents/StuAttendanceStatus.aspx").get();
                String totalPeriods = attendancePage.select("span#ctl00_ContentPlaceHolder1_lbltotperiod11").text();
                String totalPresent = attendancePage.select("span#ctl00_ContentPlaceHolder1_lbltotalp11").text();

                int totalCount = (!totalPresent.isEmpty()) ? Integer.parseInt(totalPeriods) : 0;
                int presentCount = (!totalPresent.isEmpty()) ? Integer.parseInt(totalPresent) : 0;
                int absentCount = totalCount - presentCount;
                double percentage = (totalCount == 0) ? 0 : (presentCount * 100.0) / totalCount;

                overallStatistics =  new OverallStatistics(totalCount, presentCount, percentage, absentCount);
            }

        } catch (IOException e) {
        }
        return new OverallStatistics(0, 0, 0.0, 0);
    }

    public Optional<StudentInfo> getStudentInformation(String username, String password) {
        StudentInfo studentInfo = null;
        try {
            Document loginResponse = login(username, password);
//            System.out.println(loginResponse.html());
            boolean loginState = !loginResponse.select("a#alertsDropdown:contains(" + username + ")").isEmpty();

            if (loginState) {
                Document dashboardPage = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/parents/ParentDesk.aspx").get();
                String name = loginResponse.select("a#userDropdown > span").text();
                String classRoll = loginResponse.select("a#a1").text().split(" ")[3];
                String branch = loginResponse.select("a#messagesDropdown").text();
                studentInfo = new StudentInfo(name, classRoll, branch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(studentInfo);
    }
}
