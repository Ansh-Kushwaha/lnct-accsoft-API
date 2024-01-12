package com.apis.lnctattendance.service;

import com.apis.lnctattendance.model.OverallStatistics;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class APIService {


    public OverallStatistics getOverallAttendance(String username, String password) {
        OverallStatistics result = null;

        try {
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

            Document loginResponse = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/StudentLogin.aspx")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36 Edg/111.0.1661.62")
                    .header("Referer", "https://portal.lnct.ac.in/")
                    .data(loginPayload)
                    .post();

            boolean loginState = !loginResponse.select("a#alertsDropdown:contains(" + username + ")").isEmpty();

            if (loginState) {
                Document attendancePage = Jsoup.connect("https://portal.lnct.ac.in/Accsoft2/parents/StuAttendanceStatus.aspx").get();
                String totalPeriods = attendancePage.select("span#ctl00_ContentPlaceHolder1_lbltotperiod").text();
                String totalPresent = attendancePage.select("span#ctl00_ContentPlaceHolder1_lbltotalp").text();

                int totalCount = (!totalPresent.isEmpty()) ? Integer.parseInt(totalPeriods) : 0;
                int presentCount = (!totalPresent.isEmpty()) ? Integer.parseInt(totalPresent) : 0;
                int absentCount = totalCount - presentCount;
                double percentage = (totalCount == 0) ? 0 : (presentCount * 100.0) / totalCount;

                return new OverallStatistics(totalCount, presentCount, percentage, absentCount);
            }

        } catch (IOException e) {
        }
        return new OverallStatistics(0, 0, 0.0, 0);
    }
}
