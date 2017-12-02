/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.newsticker;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.example.newsticker.model.Article;
import com.example.newsticker.model.News;
import com.example.newsticker.model.Ticker;
import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends Activity {

    @Inject NewsClient newsClient;

    private static final int LED_BRIGHTNESS = 1;
    private static final int LED_SIZE = 4;

    private AlphanumericDisplay display;
    private Apa102 ledStrip;

    Ticker ticker = new Ticker("");

    private int messageStartingIndex = 0;
    private int delay = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.get().getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        
        Timber.d("News Ticker Started");

        try {
            initializeDisplay();
        } catch (IOException e) {
            throw new RuntimeException("Error initializing display", e);
        }

        try {
            initializeLed();
        } catch (IOException e) {
            throw new RuntimeException("Error initializing LED strip", e);
        }

        // Initialize buttons
        try {
            Button buttonSlow = RainbowHat.openButtonA();
            buttonSlow.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    slowDownTicker();
                }
            });

            Button buttonFast = RainbowHat.openButtonC();
            buttonFast.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    speedUpTicker();
                }
            });

        } catch (IOException e) {
            throw new RuntimeException("Error initializing button", e);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Options are cnn, reddit-r-all, the-verge
        Call<News> newsCall = newsClient.getNews("", "cnn");
        newsCall.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                News news = response.body();

                if (news == null) {
                    Timber.e("Response is null");
                    return;
                }

                StringBuilder message = new StringBuilder();
                for (Article article : news.getArticles()) {
                    message.append(article.getTitle().toUpperCase()).append("    ");
                }

                String clean = message.toString().replaceAll("[^\\p{ASCII}]", "");

                //String messageStr = message.toString();
                //messageStr = messageStr.replaceAll("•", "");

                ticker.setText(clean);
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Timber.d("onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO: Don't use an infinite loop
                while (true) {

                    if (!ticker.getText().equals("")) {
                        messageStartingIndex++;
                    }

                    try {
                        display.display(ticker.getTextAtIndex(messageStartingIndex, LED_SIZE));
                        Thread.sleep(delay);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        closeDisplay();
        closeLedStrip();
    }

    private void initializeDisplay() throws IOException {
        display = RainbowHat.openDisplay();
        display.setEnabled(true);
    }

    private void closeDisplay() {
        if (display != null) {
            try {
                display.clear();
                display.setEnabled(false);
                display.close();
            } catch (IOException e) {
                Timber.e(e, "Error closing display");
            } finally {
                display = null;
            }
        }
    }

    private void initializeLed() throws IOException {
        ledStrip = RainbowHat.openLedStrip();
        ledStrip.setBrightness(LED_BRIGHTNESS);
        int[] colors = new int[7];
        Arrays.fill(colors, Color.RED);
        ledStrip.write(colors);
        // Because of a known APA102 issue, write the initial value twice.
        ledStrip.write(colors);
    }

    private void closeLedStrip() {
        if (ledStrip != null) {
            try {
                ledStrip.setBrightness(0);
                ledStrip.write(new int[7]);
                ledStrip.close();
            } catch (IOException e) {
                Timber.e(e,"Error closing LED strip");
            } finally {
                ledStrip = null;
            }
        }
    }

    private void slowDownTicker() {
        if (delay >= 250) {
            delay += 250;
        } else {
            delay += 50;
        }

        if (delay > 2000) {
            delay = 2000;
        }
    }

    private void speedUpTicker() {
        if (delay >= 500) {
            delay -= 250;
        } else {
            delay -= 50;
        }

        if (delay < 50) {
            delay = 50;
        }
    }

}
