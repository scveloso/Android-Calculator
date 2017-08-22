package com.ubclaunchpad.calculator;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CalculatorActivity extends AppCompatActivity {
    private final static String TAG = CalculatorActivity.class.getSimpleName();

    private EditText edTxtFirstInput;
    private EditText edTxtSecondInput;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        edTxtFirstInput = (EditText) findViewById(R.id.firstInput);
        edTxtSecondInput = (EditText) findViewById(R.id.secondInput);
        txtResult = (TextView) findViewById(R.id.txtResult);

        Button btnAdd = (Button) findViewById(R.id.operation_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
        Button btnSub = (Button) findViewById(R.id.operation_sub);
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sub();
            }
        });
        Button btnMulti = (Button) findViewById(R.id.operation_multi);
        btnMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multi();
            }
        });
        Button btnDiv = (Button) findViewById(R.id.operation_div);
        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                div();
            }
        });
        Button btnReset = (Button) findViewById(R.id.operation_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
        Button btnGetTemp = (Button) findViewById(R.id.operation_temp);
        btnGetTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetHoustonTemperatureTask().execute();
            }
        });
    }

    /* Retrieves user inputs, adds them together and displays the result to the user. */
    private void add() {
        int[] userInputs = getInputs();
        txtResult.setText(getString(R.string.result, userInputs[0] + userInputs[1]));
    }

    /* Retrieves user inputs, subtracts the second input from the first, and displays the result to the user. */
    private void sub() {
        int[] userInputs = getInputs();
        txtResult.setText(getString(R.string.result, userInputs[0] - userInputs[1]));
    }

    /* Retrieves user inputs, multiplies them together and displays the result to the user. */
    private void multi() {
        int[] userInputs = getInputs();
        txtResult.setText(getString(R.string.result, userInputs[0] * userInputs[1]));
    }

    /* Retrieves user inputs, divides the first input by the second input, and displays the result to the user. */
    private void div() {
        int[] userInputs = getInputs();
        if (userInputs[1] == 0) {
            Toast.makeText(this, getString(R.string.divide_by_zero), Toast.LENGTH_SHORT).show();
            return;
        }
        txtResult.setText(getString(R.string.result, userInputs[0] / userInputs[1]));
    }

    /* Resets all the text fields to default values. */
    private void reset() {
        txtResult.setText("0");
        edTxtFirstInput.setText("0");
        edTxtSecondInput.setText("0");
        Toast.makeText(this, getString(R.string.fields_reset), Toast.LENGTH_SHORT).show();
    }

    /* Retrieve user inputs and return them in an array. Default input is 0. */
    private int[] getInputs() {
        /* Declare my return value */
        int[] inputs = new int[2];

        /* Retrieve user inputs */
        String strFirst = edTxtFirstInput.getText().toString();
        String strSecond = edTxtSecondInput.getText().toString();

        if (strFirst.equals("")) {
            inputs[0] = 0;
        } else {
            inputs[0] = Integer.parseInt(strFirst);
        }

        if (strSecond.equals("")) {
            inputs[1] = 0;
        } else {
            inputs[1] = Integer.parseInt(strSecond);
        }

        return inputs;
    }

    /* An AsyncTask that calls a Web API to retrieve the current temperature in Houston, Texas. */
    private class GetHoustonTemperatureTask extends AsyncTask<Void, Void, String> {

        /* Get a response from an API call. */
        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=Houston,tx&appid=2230d97bf7db109fae8225e5e627733e");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally {
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        /* Parse the response, retrieving the temperature in Houston, Texas. Then, convert that temperature to Fahrenheit. */
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject main = object.getJSONObject("main");
                double temperature = main.getDouble("temp");
                int tempFahrenheit = (int) (((9 * temperature) / 5) - 459.67); /* Convert the obtained temperature to Fahrenheit */
                Toast.makeText(CalculatorActivity.this, getString(R.string.houston_temp_output, tempFahrenheit), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Log.e("ERROR", e.getMessage());
            }
        }
    }
}