package test.code.avenue.com.androidtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private EditText searchInput;
    private Button searchButton;
    private ListView resultList;
    private Context context;
    private TextView failedText;

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listCoord = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _init();

        context = this;

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    actionGetPlaces();

                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionGetPlaces();
            }
        });

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(context, MapsActivity.class);
                i.putExtra("listPlaces",listItems);
                i.putExtra("listCoord",listCoord);
                i.putExtra("position",position);

                startActivity(i);
            }
        });
    }

    private void actionGetPlaces(){
        adapter.clear();
        listCoord.clear();
        listItems.clear();
        String textLocation = searchInput.getText().toString();
        new GetPlacesTask(context,textLocation).execute();
    }

    private void _init(){
        setContentView(R.layout.activity_main);

        searchInput = (EditText) findViewById(R.id.searchEditText);
        searchButton = (Button) findViewById(R.id.searchButton);
        resultList = (ListView) findViewById(R.id.resultListView);
        failedText = (TextView) findViewById(R.id.failedResultTextView);

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        resultList.setAdapter(adapter);

    }



    public class GetPlacesTask extends AsyncTask<String, String, Boolean> {

        Context ctx;
        String location;
        Boolean isJsonEmpty;
        ProgressDialog progDailog;

        public GetPlacesTask(Context mContext, String location){
            this.ctx = mContext;
            this.location = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(ctx);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {

            //Blank spaces result in an Illegal character in query, so we need to replace the space
            //with something else
            location = location.replace(" ", "%20");

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://maps.googleapis.com/maps/api/geocode/json?address="
                            + location +
                            "&sensor=false");

            // Execute HTTP Post Request
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
                HttpEntity resEntityGet = response.getEntity();
                String result = EntityUtils.toString(resEntityGet);

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                if (jsonArray.length() > 0) {

                    isJsonEmpty = false;

                    for (int i = 0; i < jsonArray.length(); i++){
                        listItems.add(i, jsonArray.getJSONObject(i).
                                getString("formatted_address"));
                        listCoord.add(i, jsonArray.getJSONObject(i).
                                getJSONObject("geometry").getJSONObject("location").toString());
                    }
                } else
                    isJsonEmpty = true;



            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            progDailog.dismiss();

            if(isJsonEmpty) {
                failedText.setVisibility(View.VISIBLE);
                resultList.setVisibility(View.INVISIBLE);
                failedText.setText("No results for: " + location);
            } else {
                failedText.setVisibility(View.INVISIBLE);
                resultList.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
