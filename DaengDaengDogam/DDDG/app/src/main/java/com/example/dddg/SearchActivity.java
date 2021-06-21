package com.example.dddg;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class SearchActivity extends AppCompatActivity {
    APIKEYS keys = new APIKEYS();
    int finish = 0;
    private ProgressDialog pd;

    private PublicAdapter padapter;
    private YoutubeAdapter yadapter;

    NaverItem NI = new NaverItem();
    PublicItem pi = null;
    int nexstate = 0;
    int starstate = 0;

    DBHelper helper;
    SQLiteDatabase db;
    public static class DbBitmapUtility {

        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        helper = new DBHelper(SearchActivity.this, "dog.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);


        RecyclerView PublicView = findViewById(R.id.public_Rview);
        RecyclerView YoutubeView = findViewById(R.id.youtube_Rview);
        ImageView star = findViewById(R.id.star);

        LinearLayoutManager PublicManager = new LinearLayoutManager(SearchActivity.this);
        PublicManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        PublicView.setLayoutManager(PublicManager);

        GridLayoutManager YoutubeManager = new GridLayoutManager(SearchActivity.this,2);
        YoutubeView.setLayoutManager(YoutubeManager);

        padapter = new PublicAdapter();
        yadapter = new YoutubeAdapter();

        PublicView.setAdapter(padapter);
        YoutubeView.setAdapter(yadapter);
        setVisible(false);
        showProgress("로딩중");

        Thread thread = new Thread((Runnable) () -> {
            try {

                Intent intent = getIntent();
                String keyword = intent.getStringExtra("keyword");
                getNaverSearch(keyword);
                String str2 = getPublicSearch();
                getYoutubeSearch(keyword);




                runOnUiThread((Runnable) () -> {

                    if (finish == 200) {
                        hideProgress();
                        setVisible(true);
                        TextView headTitle = (TextView) findViewById(R.id.head_title);
                        ImageView imageView = (ImageView) findViewById(R.id.main_image);
                        TextView searchResult2 = (TextView) findViewById(R.id.search_result2);

                        TextView moreex = (TextView) findViewById(R.id.more);

//                        System.out.println(str2);

                        headTitle.setText(NI.title);
                        if(NI.image == null){

                            imageCheck(keyword,imageView);
                        }else{
                            imageView.setImageBitmap(NI.image);
                        }
                        searchResult2.setText(NI.summary);

                        // ---- db에서 정보가 미리 있는지 체크 ----
                        String sql = "select * from dog where title='" + NI.title + "';";
                        Cursor c = db.rawQuery(sql, null);
                        while(c.moveToNext()){  // 정보가 있는 경우
                            starstate = 1;
                            star.setImageResource(R.drawable.star_1);
                            break;
                        }
                        // --------------------------------------

                        star.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                starstate = 1 - starstate;
                                starCheck(starstate,star);
                                System.out.println("즐겨찾기");
                            }
                        });

                        moreex.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nexstate = 1 - nexstate;
                                if(nexstate == 1){
                                    moreex.setText(NI.ex);
                                }
                                else{
                                    moreex.setText("...더보기");
                                }
                                // TextView 클릭될 시 할 코드작성
                            }
                        });



                        padapter.notifyDataSetChanged();
                        yadapter.notifyDataSetChanged();


                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void starCheck(int starstate,ImageView star){
        if(starstate==1){
            star.setImageResource(R.drawable.star_1);
            ContentValues values = new ContentValues();  // db insert
            //set data
            values.put("title",NI.title);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            NI.image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();
            values.put("image",data);
            values.put("summary",NI.summary);
            values.put("ex",NI.ex);
            //insert data
            db.insert("dog",null,values);
        }
        else{
            star.setImageResource(R.drawable.star_2);
            System.out.println(NI.title);  // db delete
            String[] Array = new String[] {NI.title};
            db.delete("dog","title=?",Array);
        }
    }

    public void imageCheck(String keyword, ImageView imageView){
        System.out.println("imagecheck "+NI.image);
        System.out.println("keyword "+keyword);
        if(keyword.equalsIgnoreCase("entlebucher sennenhund")){
            System.out.println("사진변경!");
            imageView.setImageResource(R.drawable.entlebucher_sennenhund);
        }
        if(keyword.equalsIgnoreCase("Appenzeller Sennenhund")){
            System.out.println("사진변경!");
            imageView.setImageResource(R.drawable.appenzeller);
        }
        if(keyword.equalsIgnoreCase("bluetick coonhound")){
            System.out.println("사진변경!");
            imageView.setImageResource(R.drawable.bluetick_coonhound);
        }
        if(keyword.equalsIgnoreCase("leonberger")){
            System.out.println("사진변경!");
            imageView.setImageResource(R.drawable.leonberger);
        }
        if(keyword.equalsIgnoreCase("mexican hairless")){
            System.out.println("사진변경!");
            imageView.setImageResource(R.drawable.mexican_hairless);
        }
        if(keyword.equalsIgnoreCase("redbone coonhound")){
            System.out.println("사진변경!");
            imageView.setImageResource(R.drawable.redbone_coonhound);
        }
        if(keyword.equalsIgnoreCase("walker hound")){
            System.out.println("사진변경!");
            imageView.setImageResource(R.drawable.treeing_walker_coonhound);
        }
    }


    public String getNaverSearch(String keyword) {

        String clientID = keys.naverclientid;
        String clientSecret = keys.naverclientsecret;
        StringBuilder sb = new StringBuilder();
        StringBuilder PA = new StringBuilder();

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
            xpp.setInput(new InputStreamReader(conn.getInputStream(), "UTF_8"));

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기
                        if (tag.equals("item")) ; //첫번째 검색 결과
                        else if (tag.equals(("link"))) {
                            xpp.next();
                            String li = xpp.getText().replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "");
                            System.out.println(li);
                            if (li.contains("docId=989")) {
                                sb.setLength(0);
                                System.out.println("OK");
                                sb.append(li);
                                break;
                            }
                            if (li.contains("&cid=40942&categoryId=32624") && sb.length() == 0) {
                                sb.setLength(0);
                                System.out.println("OK dosan");
                                sb.append(li);
                                break;
                            }
                            if (li.contains("docId=566") && sb.length() == 0) {
                                sb.setLength(0);
                                System.out.println("OK standard");
                                sb.append(li);
                                break;
                            }
                        }
                        break;
                }

                eventType = xpp.next();
            }

            if (sb.length() == 0) {
                sb.append("no result");
                return sb.toString();
            } // 해당하는 링크가 존재하지 않을때

            // 원하는 링크 수집 완료
            // jsoup으로 크롤링
            String str = sb.toString();
            Document doc = Jsoup.connect(str).get();
            Element name = doc.selectFirst("div.headword_title h2.headword");
            NI.title = name.text();
            Elements s = doc.getElementsByClass("summary_area");
            NI.summary = " \" "+s.text().substring(3)+" \" ";

            if (str.contains("docId=989")) {

                Element img = doc.getElementById("innerImage0");
                Elements el = doc.getElementsByTag("tr");
                //Elements el = doc.select("div.wr_tmp_profile div.tmp_profile table.tmp_profile_tb tbody tr");
                Element ex = doc.getElementById("size_ct");

                String imgUrl = img.attr("origin_src");
                System.out.println("img : " + imgUrl);
                URL iu = new URL(imgUrl);

                URLConnection con2 = iu.openConnection();
                con2.connect();

                InputStream is = con2.getInputStream();
                NI.image = BitmapFactory.decodeStream(is);

                for (Element e : el) {
//                    System.out.println("td : " + e.select("td").text());
//                    System.out.println("th : " + e.select("th").text());
                    PA.append(e.select("th").text()).append("\n\t\t-\t");
                    PA.append(e.select("td").text()).append("\n");
                }
                PA.append("\n\n").append(ex.select("p.txt").text()).append("\n");
            } else if (str.contains("&cid=40942&categoryId=32624")) {
                Element img = doc.selectFirst("div.size_ct_v2 p.txt img");
                Elements asd = doc.getElementsByClass("txt");

                if (img != null) {
                    String imgUrl = img.attr("origin_src");
                    System.out.println("img : " + imgUrl);
                    URL iu = new URL(imgUrl);

                    URLConnection con2 = iu.openConnection();
                    con2.connect();

                    InputStream is = con2.getInputStream();
                    NI.image = BitmapFactory.decodeStream(is);
                }
                for (Element e : asd) {
//                    System.out.println(e.text());
                    PA.append(e.text()).append("\n");
                }
            } else if (str.contains("docId=566")) {
                Elements asd = doc.getElementById("size_ct").getAllElements();

                boolean c = false;
                for (Element e : asd) {

                    if (e.attr("id").equals("TABLE_OF_CONTENT1")) {
                        c = true;
                    }
                    if (e.attr("id").equals("TABLE_OF_CONTENT2")) {
                        c = false;
                        break;
                    }
                    System.out.println("a = " + e.text() + c);
                    if (c) {
                        System.out.println("b = " + e.text());
                        PA.append(e.text()).append("\n");
                    }
                }
            } else {
                PA = new StringBuilder(str);
            }


        } catch (IOException e) {
            return e.toString();
        } catch (Exception e) {
            return e.toString();
        }
        NI.ex = PA.toString();
        return PA.toString();
    }

    public String getPublicSearch() {
        StringBuilder sb = new StringBuilder();
        String key = keys.publickey;
        String temp;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String today = df.format(cal.getTime());
        System.out.println("today: " + today);
        cal.add(Calendar.MONTH, +1);
        String monthlater = df.format(cal.getTime());
        System.out.println("later: " + monthlater);
        cal.add(Calendar.MONTH, -2);
        String monthago = df.format(cal.getTime());
        System.out.println("ago: " + monthago);

        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.animal.go.kr/openapi/service/rest/abandonmentPublicSrvc/abandonmentPublic"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + key); /*Service Key*/
            urlBuilder.append("&bgnde=" + monthago + "&endde=" + monthlater + "&upkind=417000&state=protect&pageNo=1&numOfRows=10");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            String tag;
            //inputStream으로부터 xml값 받기
            xpp.setInput(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();
            PublicItem PI = new PublicItem();


            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기
                        // 공고종료일, 성별, 중성화여부, 이미지, 특징, 보호소이름, 보호소 전화번호
                        // 접수일, 발견장소, 품종, 색상, 나이, 체중, 공고시작일, 공고번호
                        if (tag.equals("item")) ; //첫번째 검색 결과
                        else if (tag.equals("noticeEdt")) {
                            sb.append("공고종료일 : ");
                            xpp.next();
                            temp = xpp.getText();
                            temp = temp.substring(0,4)+"."+temp.substring(4,6)+"."+temp.substring(6,8);
                            sb.append(temp);
                            sb.append("\n");
                            PI.noticeEdt = temp;
                        } else if (tag.equals("popfile")) {
                            sb.append("이미지 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            URL iu = new URL(temp);
                            URLConnection co = iu.openConnection();
                            co.connect();
                            InputStream is = co.getInputStream();
                            PI.image = BitmapFactory.decodeStream(is);
                        } else if (tag.equals("sexCd")) {
                            sb.append("성별 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.sexCd = temp;
                        } else if (tag.equals("neuterYn")) {
                            sb.append("중성화여부 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.neuterYn = temp;
                        } else if (tag.equals("specialMark")) {
                            sb.append("특징 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.feature = temp;
                        } else if (tag.equals("careNm")) {
                            sb.append("보호소이름 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.careNm = temp;
                        } else if (tag.equals("careTel")) {
                            sb.append("보호소전화번호 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.careTel = temp;
                        } else if (tag.equals("desertionNo")) {
                            sb.append("유기번호 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.desertionNm = temp;
                        } else if (tag.equals("happenDt")) {
                            sb.append("접수일 : ");
                            xpp.next();
                            temp = xpp.getText();
                            temp = temp.substring(0,4)+"."+temp.substring(4,6)+"."+temp.substring(6,8);
                            sb.append(temp);
                            sb.append("\n");
                            PI.happenDt = temp;
                        } else if (tag.equals("happenPlace")) {
                            sb.append("발견장소 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.happenPlace = temp;
                        } else if (tag.equals("kindCd")) {
                            sb.append("품종 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.kindCd = temp;
                        } else if (tag.equals("colorCd")) {
                            sb.append("색상 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.colorCd = temp;
                        } else if (tag.equals("age")) {
                            sb.append("나이 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.age = temp;
                        } else if (tag.equals("weight")) {
                            sb.append("체중 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n\n");
                            PI.weight = temp;
                            padapter.addItem(PI);
                            PI = new PublicItem();
                        } else if (tag.equals("noticeNo")) {
                            sb.append("공고번호 : ");
                            xpp.next();
                            temp = xpp.getText();
                            sb.append(temp);
                            sb.append("\n");
                            PI.noticeNo = temp;
                        } else if (tag.equals("noticeSdt")) {
                            sb.append("공고시작일 : ");
                            xpp.next();
                            temp = xpp.getText();
                            temp = temp.substring(0,4)+"."+temp.substring(4,6)+"."+temp.substring(6,8);
                            sb.append(temp);
                            sb.append("\n");
                            PI.noticeSdt = temp;
                        }
//
                        break;
                }

                eventType = xpp.next();

            }
//            finish = conn.getResponseCode();
//            System.out.println(sb.toString());
        } catch (Exception e) {
            return e.toString();
        }


        return sb.toString();
    }

    public void getYoutubeSearch(String keyword) throws IOException {

        String apiurl = "https://www.googleapis.com/youtube/v3/search";
        apiurl += "?key=" + keys.youtubekey;
        apiurl += "&part=snippet&type=video&maxResults=20&videoEmbeddable=true";
        apiurl += "&q=" + URLEncoder.encode(keyword, "UTF-8");
        StringBuilder response = new StringBuilder();
        JSONObject youtubejson;

        try {
            URL url = new URL(apiurl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
                //System.out.println(inputLine);
            }
            br.close();



            // 특정 아이템 가져오기
            // id.videoid, snippet.title, snippet.description
            // snippet.thumnails.high.url
            youtubejson = new JSONObject(response.toString()); // 버퍼에 입력된 문자열 jsonobject로 만듬
            JSONArray items = youtubejson.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject data = items.getJSONObject(i);
                YoutubeItem YI = new YoutubeItem(); // 정보 저장
                YI.videoID = data.getJSONObject("id").getString("videoId");
                YI.link = "https://www.youtube.com/watch?v=" + YI.videoID;
                YI.title = data.getJSONObject("snippet").getString("title");
                YI.description = data.getJSONObject("snippet").getString("description");
                String thumbnail = data.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");
                System.out.println("thumbnail: "+thumbnail);
                // 이미지 저장
                URL iu = new URL(thumbnail);
                URLConnection conn = iu.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                YI.thumbnail = BitmapFactory.decodeStream(is);
                int imgWidth = YI.thumbnail.getWidth();
//                System.out.println("가로길이는 "+imgWidth);
                YI.thumbnail = Bitmap.createBitmap(YI.thumbnail,0,imgWidth * 3 / 32,imgWidth,imgWidth * 9 / 16);

                yadapter.addItem(YI);
            }
            finish = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgress(String msg) {
        if (pd == null) { // 객체를 1회만 생성한다.
            pd = new ProgressDialog(this); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
        }
        pd.setMessage(msg); // 원하는 메시지를 세팅한다.
        pd.show(); // 화면에 띠워라
    }

    //프로그레스 다이얼로그 숨기기
    public void hideProgress() {
        if (pd != null && pd.isShowing()) { // 닫는다 : 객체가 존재하고, 보일때만
            pd.dismiss();
        }
    }


}
