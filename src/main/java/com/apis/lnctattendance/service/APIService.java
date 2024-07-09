package com.apis.lnctattendance.service;

import com.apis.lnctattendance.model.DatewiseStatistics;
import com.apis.lnctattendance.model.OverallStatistics;
import com.apis.lnctattendance.model.StudentInfo;
import com.apis.lnctattendance.model.SubwiseStatistics;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class APIService {

    private Response login(String username, String password) throws IOException {
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
                .method(Method.POST)
                .execute();
    }

    public Optional<OverallStatistics> getOverallAttendance(String username, String password) {
        try {
            Response loginResponse = login(username, password);
            Map<String, String> cookies = loginResponse.cookies();
            Document responsePage = loginResponse.parse();
            boolean loginState = !responsePage.select("a#alertsDropdown:contains(" + username + ")").isEmpty();

            if (loginState) {
                Document attendancePage = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/parents/StuAttendanceStatus.aspx").cookies(cookies).get();
                String totalPeriods = attendancePage.select("span#ctl00_ContentPlaceHolder1_lbltotperiod11").text().split(" ")[4];
                String totalPresent = attendancePage.select("span#ctl00_ContentPlaceHolder1_lbltotalp11").text().split(" ")[3];

                int totalCount = (!totalPresent.isEmpty()) ? Integer.parseInt(totalPeriods) : 0;
                int presentCount = (!totalPresent.isEmpty()) ? Integer.parseInt(totalPresent) : 0;
                int absentCount = totalCount - presentCount;
                double percentage = (totalCount == 0) ? 0 : (presentCount * 100.0) / totalCount;

                return Optional.of(new OverallStatistics(totalCount, presentCount, percentage, absentCount));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<StudentInfo> getStudentInformation(String username, String password) {
        try {
            Response loginResponse = login(username, password);
            Map<String, String> cookies = loginResponse.cookies();
            Document responsePage = loginResponse.parse();
            boolean loginState = !responsePage.select("a#alertsDropdown:contains(" + username + ")").isEmpty();

            if (loginState) {
                Document dashboard = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/parents/ParentDesk.aspx").cookies(cookies).get();
                String name = dashboard.select("a#userDropdown > span").text();
                String classRoll = dashboard.select("a#a1").text().split(" ")[3];
                String branch = dashboard.select("a#messagesDropdown").text();
                return Optional.of(new StudentInfo(name, classRoll, branch));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean getLoginStatus(String username, String password) {
        try {
            Response loginResponse = login(username, password);
            return !loginResponse.parse().select("a#alertsDropdown:contains(" + username + ")").isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Optional<List<SubwiseStatistics>> getSubjectWiseAttendance(String username, String password) {
        try {
            Response loginResponse = login(username, password);
            Map<String, String> cookies = loginResponse.cookies();
            Document responsePage = loginResponse.parse();
            boolean loginState = !responsePage.select("a#alertsDropdown:contains(" + username + ")").isEmpty();

            if (loginState) {
                Document attendancePage = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/Parents/StuAttendanceStatus.aspx").cookies(cookies).get();
                Element table = attendancePage.select("table#ctl00_ContentPlaceHolder1_grdSubjectWiseAttendance > tbody").getFirst();
                Elements subjectRows = table.select("tr");
                List<SubwiseStatistics> subwiseStatisticsList = new ArrayList<>();

                for (int i = 1; i < subjectRows.size(); i++) {
                    Elements cols = subjectRows.get(i).select("td");
                    String subName = cols.get(0).text();
                    Integer classesHeld = Integer.parseInt(cols.get(1).text());
                    Integer presentCount = Integer.parseInt(cols.get(2).text());
                    Integer absentCount = Integer.parseInt(cols.get(3).text());

                    subwiseStatisticsList.add(new SubwiseStatistics(subName, classesHeld, presentCount, absentCount));
                }

                return Optional.of(subwiseStatisticsList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<DatewiseStatistics>> getDatewiseAttendance(String username, String password) {
        try {
            Response loginResponse = login(username, password);
            Map<String, String> cookies = loginResponse.cookies();
            Document responsePage = loginResponse.parse();
            boolean loginState = !responsePage.select("a#alertsDropdown:contains(" + username + ")").isEmpty();

            if (loginState) {
                Document attendancePage = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/Parents/StuAttendanceStatus.aspx").cookies(cookies).get();
                Element table = attendancePage.select("table#ctl00_ContentPlaceHolder1_Gridview1 > tbody").getFirst();
                Elements periodRows = table.select("tr");
                List<DatewiseStatistics> datewiseStatisticsList = new ArrayList<>();

                for (int i = 1; i < periodRows.size(); i++) {
                    Elements cols = periodRows.get(i).select("td");
                    String date = cols.get(1).text();
                    Integer periodNo = Integer.parseInt(cols.get(2).text());
                    String subName = cols.get(3).text();
                    Character attendanceStatus = cols.get(4).text().charAt(0);

                    datewiseStatisticsList.add(new DatewiseStatistics(date, periodNo, subName, attendanceStatus));
                }

                return Optional.of(datewiseStatisticsList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
