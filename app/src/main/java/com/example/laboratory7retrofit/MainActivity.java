package com.example.laboratory7retrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String apiUrl = "https://api.monobank.ua/bank/";

    private List<CurrencyPojo> postMono;
    private RadioGroup radioGroup;

    /* база данных для наших валют, вписываем одинаковые название с string.xml и кодировку ISO 4217 */
    private final Map<String, Integer> currencyCodes = new HashMap<String, Integer>() {{
        put("Долар", 840);
        put("Євро", 978);
        put("Фунт", 826);
        put("Злотий", 985);
        put("Гривня", 980);
    }};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button convertButton = findViewById(R.id.convertButton);
        Spinner comboA = findViewById(R.id.comboA);
        Spinner comboB = findViewById(R.id.comboB);

        EditText editText = findViewById(R.id.editTextNumber3);
        EditText amoutIn = findViewById(R.id.inputMoney);
        EditText resultMoney = findViewById(R.id.resultMoney);

        radioGroup = findViewById(R.id.radioGroup);

        getDataFromApi(); // первоначальная инициализация(там запуск потока)


        convertButton.setOnClickListener(e -> {
            if(postMono != null) {
                int currencyA = currencyCodes.get(comboA.getSelectedItem().toString());
                int currencyB = currencyCodes.get(comboB.getSelectedItem().toString());
                double amount = Double.parseDouble(amoutIn.getText().toString());

                if (currencyA == currencyB) {
                    editText.setText("Вы не можете перевести " + comboA.getSelectedItem().toString() +
                            " в " + comboB.getSelectedItem().toString());
                } else {
                    try{ // обработка внеплановых ошибок

                    double res = (convert(postMono, 1,
                            String.valueOf(currencyA),
                            String.valueOf(currencyB)));

                    editText.setText(new DecimalFormat("#0.00").format(res));
                    resultMoney.setText(new DecimalFormat("#0.00").format(res * amount));

                    } catch (Exception ex) {
                        resultMoney.setText("Ошибка конвертирования, напишите в тех поддержку.");
                        ex.printStackTrace();
                    }
                }
            }else {
                editText.setText("Подождите, локальная база пустая. Данные в процесе обновления..");
            }
        });
    }

    /* вытягиваем данные с бека */
    private void getDataFromApi() {
        NetworkService.getInstance(apiUrl)
                .getJSONApi()
                .getAllCurrencies()
                .enqueue(new Callback<List<CurrencyPojo>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<CurrencyPojo>> call, @NonNull Response<List<CurrencyPojo>> response) {
                        /* бесконечный поток, тоесть мы каждые 30 секунд обновляем базу */
                        Thread myThread = new Thread(() -> {
                            while (true) {
                                postMono = response.body();
                                try {
                                    Thread.sleep(30000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        myThread.start();
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<CurrencyPojo>> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    /* метод конвертора  */
    public Double convert(@NonNull List<CurrencyPojo> currenciesList,
                          double amount, String fromCurrency, String toCurrency) {
        /* находим индентификатор выбраного пользователём переключателя */
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton myRadioButton = findViewById(checkedRadioButtonId);
        int checkedIndex = radioGroup.indexOfChild(myRadioButton);

        /* проходим по обьектам и ищем сходство, если находим считаем разницу */
        for (CurrencyPojo currency : currenciesList) {
            if (currency.getCurrencyCodeA() == Integer.parseInt(fromCurrency)
                    && currency.getCurrencyCodeB() == Integer.parseInt(toCurrency)) {
                switch(checkedIndex){
                    case 0: { return amount * currency.getRateSell(); }
                    case 1: { return amount * currency.getRateBuy(); }
                    case 2: { return amount * currency.getRateCross(); }
                }
            }
            if (currency.getCurrencyCodeA() == Integer.parseInt(toCurrency)
                    && currency.getCurrencyCodeB() == Integer.parseInt(fromCurrency)){
                switch(checkedIndex){
                    case 0: { return amount / currency.getRateSell(); }
                    case 1: { return amount / currency.getRateBuy(); }
                    case 2: { return amount / currency.getRateCross(); }
                }
            }
        }
        return null;
    }
}