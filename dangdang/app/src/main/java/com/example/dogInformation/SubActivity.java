package com.example.dogInformation;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SubActivity extends AppCompatActivity {
    // 정보는 맵으로 구현
    // 유기견은 객체(정보를 담고있는)를 포함하는 리스트(10개)로 구현
    APIKEYS keys = new APIKEYS();

    public class youtubeItem {
        String videoid = "";
        String title = "";
        String description = "";
        String thumbnaiil = "";
    }
    ArrayList<youtubeItem> youtubeItemArrayList = new ArrayList<youtubeItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_activity);

        Thread thread = new Thread((Runnable) () -> {
            try {
                StringBuilder PA = new StringBuilder();
                Bitmap bitmap1 = null;
                Intent intent = getIntent();
                String keyword = intent.getStringExtra("keyword");
                String str = getNaverSearch(keyword);
                String str2 = getAbandonmentSearch();
                String stryoutube = getYoutubeSearch(keyword);

                Document doc = Jsoup.connect(str).get();
                Element name = doc.selectFirst("div.headword_title h2.headword");
                String title = name.text();
                Elements s = doc.getElementsByClass("summary_area");
                PA.append(s.text()).append("\n");

                if(str.contains("docId=989")) {

                    Element img = doc.getElementById("innerImage0");
                    Elements el = doc.getElementsByTag("tr");
                    //Elements el = doc.select("div.wr_tmp_profile div.tmp_profile table.tmp_profile_tb tbody tr");

                    String imgUrl = img.attr("origin_src");
                    System.out.println("img : " + imgUrl);
                    URL iu = new URL(imgUrl);

                    URLConnection conn = iu.openConnection();
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap1 = BitmapFactory.decodeStream(is);

                    for (Element e : el) {
                        System.out.println("td : " + e.select("td").text());
                        System.out.println("th : " + e.select("th").text());
                        PA.append(e.text()).append("\n");
                    }
                }
                else if(str.contains("&cid=40942&categoryId=32624"))
                    {
                        Element img = doc.selectFirst("div.size_ct_v2 p.txt img");
                        Elements asd = doc.getElementsByClass("txt");

                        if(img != null) {
                            String imgUrl = img.attr("origin_src");
                            System.out.println("img : " + imgUrl);
                            URL iu = new URL(imgUrl);

                            URLConnection conn = iu.openConnection();
                            conn.connect();

                            InputStream is = conn.getInputStream();
                            bitmap1 = BitmapFactory.decodeStream(is);
                        }
                        for (Element e : asd) {
                            System.out.println(e.text());
                            PA.append(e.text()).append("\n");
                        }
                }
                else if(str.contains("docId=566"))
                {
                    Elements asd = doc.getElementById("size_ct").getAllElements();

                    boolean c = false;
                    for (Element e : asd) {

                        if(e.attr("id").equals("TABLE_OF_CONTENT1")) { c = true;}
                        if(e.attr("id").equals("TABLE_OF_CONTENT2")) { c = false; break;}
                        System.out.println("a = " + e.text() + c);
                        if(c) {
                            System.out.println("b = " + e.text());
                            PA.append(e.text()).append("\n");
                        }
                    }
                }
                else{
                    PA = new StringBuilder(str);
                }
                final Bitmap B = bitmap1;
                final String T = PA.toString();

                for(youtubeItem y : youtubeItemArrayList) {
                    System.out.println("videoID: "+y.videoid);
                    System.out.println("title: "+y.title);
                    System.out.println("description: "+y.description);
                    System.out.println("thumbnail: "+y.thumbnaiil);
                    System.out.println("-----------------------------------");
                }


                runOnUiThread((Runnable) () -> {
                    TextView headTitle = (TextView) findViewById(R.id.headTitle);
                    ImageView imageView = (ImageView) findViewById(R.id.mainImage);
                    TextView searchResult2 = (TextView) findViewById(R.id.searchResult2);
                    TextView searchResult3 = (TextView) findViewById(R.id.searchResult3);
                    headTitle.setText(title);
                    imageView.setImageBitmap(B);
                    searchResult2.setText(T);
                    searchResult3.setText(str2);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });thread.start();
    }

    public String getNaverSearch(String keyword) {

        String clientID = keys.naverclientid;
        String clientSecret = keys.naverclientsecret;
        StringBuffer sb = new StringBuffer();

        try {
            String text = URLEncoder.encode(keyword, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/encyc.xml?query=" + text + "&display=20" + "&start=1";
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Naver-Client-Id", clientID);
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            String tag;
            //inputStream으로부터 xml값 받기
            xpp.setInput(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기

                        if (tag.equals("item")) ; //첫번째 검색 결과
//                        else if (tag.equals("title")) {
//
//                            sb.append("제목 : ");
//
//                            xpp.next();
//
//
//                            sb.append(xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", ""));
//                            sb.append("\n");
//                        }
                        else if (tag.equals(("link"))) {
                            xpp.next();
                            String li = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            System.out.println(li);
                            if(li.contains("docId=989")) {
                                sb.setLength(0);
                                System.out.println("OK");
                                sb.append(li);
                                break;
                            }
                            if(li.contains("&cid=40942&categoryId=32624") && sb.length()==0) {
                                sb.setLength(0);
                                System.out.println("OK dosan");
                                sb.append(li);
                                break;
                            }
                            if(li.contains("docId=566") && sb.length()==0) {
                                sb.setLength(0);
                                System.out.println("OK standard");
                                sb.append(li);
                                break;
                            }
                        }
//                        else if (tag.equals("description")) {
//
//                            sb.append("내용 : ");
//                            xpp.next();
//
//
//                            sb.append(xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", ""));
//                            sb.append("\n");
//                        }
                        break;
                }

                eventType = xpp.next();

            }
        } catch (Exception e) {
            return e.toString();
        }
        if(sb.length()==0) { sb.append("no result");}
        return sb.toString();
    }

    public String getAbandonmentSearch() {
//        StringBuilder sb = new StringBuilder();
        StringBuffer sb = new StringBuffer();
        String key = keys.publickey;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String today = df.format(cal.getTime());
        System.out.println("current: " + today );
        cal.add(Calendar.MONTH, -1);
        String monthago = df.format(cal.getTime());
        System.out.println("after: " + monthago);

        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.animal.go.kr/openapi/service/rest/abandonmentPublicSrvc/abandonmentPublic"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + key); /*Service Key*/
            urlBuilder.append("&bgnde="+monthago+"&endde="+today+"&upkind=417000&state=protect&pageNo=1&numOfRows=10");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            System.out.println("string: "+urlBuilder.toString());

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            String tag;
            //inputStream으로부터 xml값 받기
            xpp.setInput(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기
                        // 공고종료일, 성별, 중성화여부, 이미지, 특징, 보호소이름, 보호소 전화번호
                        // 접수일, 발견장소, 품종, 색상, 나이, 체중, 공고시작일, 공고번호, 상태
                        if (tag.equals("item")) ; //첫번째 검색 결과
                        else if (tag.equals("noticeEdt")) {
                            sb.append("공고종료일 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("popfile")) {
                            sb.append("이미지 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("processState")) {
                            sb.append("상태 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("sexCd")) {
                            sb.append("성별 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("neuterYn")) {
                            sb.append("중성화여부 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("specialMark")) {
                            sb.append("특징 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("careNm")) {
                            sb.append("보호소이름 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("careTel")) {
                            sb.append("보호소전화번호 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("desertionNo")) {
                            sb.append("유기번호 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("happenDt")) {
                            sb.append("접수일 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("happenPlace")) {
                            sb.append("발견장소 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("kindCd")) {
                            sb.append("품종 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("colorCd")) {
                            sb.append("색상 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("age")) {
                            sb.append("나이 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("weight")) {
                            sb.append("체중 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n\n");
                        }
                        else if (tag.equals("noticeNo")) {
                            sb.append("공고번호 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
                        else if (tag.equals("noticeSdt")) {
                            sb.append("공고시작일 : ");
                            xpp.next();
                            sb.append(xpp.getText());
                            sb.append("\n");
                        }
//
                        break;
                }

                eventType = xpp.next();
            }
            System.out.println(sb.toString());
        } catch (Exception e){
            return e.toString();
        }
        return sb.toString();
    }

    public String getYoutubeSearch(String keyword) throws IOException {

        String apiurl = "https://www.googleapis.com/youtube/v3/search";
        apiurl += "?key="+keys.youtubekey;
        apiurl += "&part=snippet&type=video&maxResults=20&videoEmbeddable=true";
        apiurl += "&q="+URLEncoder.encode(keyword,"UTF-8");
        StringBuffer response = new StringBuffer();
        JSONObject youtubejson = null;

        try  {
            URL url = new URL(apiurl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String inputLine;

            while((inputLine = br.readLine()) != null) {
                response.append(inputLine);
                //System.out.println(inputLine);
            }
            br.close();

            // 특정 아이템 가져오기
            // id.videoid, snippet.title, snippet.description
            // snippet.thumnails.high.url
            youtubejson = new JSONObject(response.toString());
            JSONArray items = youtubejson.getJSONArray("items");
            for (int i = 0; i <items.length();i++ ){
                JSONObject data = items.getJSONObject(i);
                youtubeItem it = new youtubeItem();
                it.videoid = data.getJSONObject("id").getString("videoId");
                it.title = data.getJSONObject("snippet").getString("title");
                it.description = data.getJSONObject("snippet").getString("description");
                it.thumbnaiil = data.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");
                youtubeItemArrayList.add(it);
            }

        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return response.toString();

    }
}
