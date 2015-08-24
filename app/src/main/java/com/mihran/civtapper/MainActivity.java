package com.mihran.civtapper;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

/**
 * This activity does everything (for now). Not practical for future
 *
 * @author Charlie Mihran
 */
public class MainActivity extends ActionBarActivity {

    static boolean running = false;

    static final int posGreen = Color.parseColor("#04B404");
    static final int negRed = Color.parseColor("#B40404");
    static final int neutralGray = Color.parseColor("#898989");

    static final int W_FARMER = 1;
    static final int W_LUMBERJACK = 2;
    static final int W_MINER = 3;

    public static final String PREFS_NAME = "DataStore";

    // Materials
    BigDecimal food;
    BigDecimal wood;
    BigDecimal stone;

    // Population
    int population;
    int unemployed;
    int farmer;
    int lumberjack;
    int miner;

    // Growth Rates
    BigDecimal foodRate;
    BigDecimal woodRate;
    BigDecimal stoneRate;
    BigDecimal farmerRateIncrease;
    BigDecimal lumberjackRateIncrease;
    BigDecimal minerRateIncrease;
    BigDecimal populationFoodConsumption;

    // TextViews
    TextView textFoodAmount;
    TextView textFoodRate;

    TextView textWoodAmount;
    TextView textWoodRate;

    TextView textStoneAmount;
    TextView textStoneRate;

    TextView textWorker;
    TextView textFarmer;
    TextView textLumberjack;
    TextView textMiner;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        running = true;

        //test comment to demonstrate committing

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences data = getSharedPreferences(PREFS_NAME, 0);

        food = new BigDecimal(data.getString("food", "0.0"));
        wood = new BigDecimal(data.getString("wood", "0.0"));
        stone = new BigDecimal(data.getString("stone", "0.0"));

        foodRate = new BigDecimal(data.getString("foodRate", "0.0"));
        woodRate = new BigDecimal(data.getString("woodRate", "0.0"));
        stoneRate = new BigDecimal(data.getString("stoneRate", "0.0"));

        farmerRateIncrease = new BigDecimal(data.getString("farmerRateIncrease", "1.2"));
        lumberjackRateIncrease = new BigDecimal(data.getString("lumberjackRateIncrease", "0.5"));
        minerRateIncrease = new BigDecimal(data.getString("minerRateIncrease", "0.2"));
        populationFoodConsumption = new BigDecimal(data.getString("populationFoodConsumption", "1.0"));

        population = data.getInt("population", 0);
        unemployed = data.getInt("unemployed", 0);
        farmer = data.getInt("farmer", 0);
        lumberjack = data.getInt("lumberjack", 0);
        miner = data.getInt("miner", 0);

        textFoodAmount = (TextView) findViewById(R.id.foodAmount);
        textFoodRate = (TextView) findViewById(R.id.foodRate);

        textWoodAmount = (TextView) findViewById(R.id.woodAmount);
        textWoodRate = (TextView) findViewById(R.id.woodRate);

        textStoneAmount = (TextView) findViewById(R.id.stoneAmount);
        textStoneRate = (TextView) findViewById(R.id.stoneRate);

        textWorker = (TextView) findViewById(R.id.textViewWorker);
        textFarmer = (TextView) findViewById(R.id.textViewFarmer);
        textLumberjack = (TextView) findViewById(R.id.textViewLumberjack);
        textMiner = (TextView) findViewById(R.id.textViewMiner);

        updateText();

        new Thread(new Ticker()).start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("food", food.toString());
        editor.putString("wood", wood.toString());
        editor.putString("stone", stone.toString());

        editor.putString("foodRate", foodRate.toString());
        editor.putString("woodRate", woodRate.toString());
        editor.putString("stoneRate", stoneRate.toString());

        editor.putString("farmerRateIncrease", farmerRateIncrease.toString());
        editor.putString("lumberjackRateIncrease", lumberjackRateIncrease.toString());
        editor.putString("minerRateIncrease", minerRateIncrease.toString());
        editor.putString("populationFoodConsumption", populationFoodConsumption.toString());

        editor.putInt("population", population);
        editor.putInt("unemployed", unemployed);
        editor.putInt("farmer", farmer);
        editor.putInt("lumberjack", lumberjack);
        editor.putInt("miner", miner);

        editor.commit();
    }

    /**
     * Responsible for incrementing resources every second, or "tick"
     */
    class Ticker implements Runnable {
        @Override
        public void run() {
            while (running) {
                // Update Resources
                food = food.add(foodRate);
                wood = wood.add(woodRate);
                stone = stone.add(stoneRate);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateText();
                    }
                });
            }
        }
    }

    /**
     * Updates the text in the UI with new values
     */
    public void updateText() {
        textFoodAmount.setText(food + " ");
        textFoodRate.setText(" " + foodRate + "/s");
        textFoodRate.setTextColor((foodRate.compareTo(BigDecimal.ZERO) == 0) ? neutralGray : (foodRate.compareTo(BigDecimal.ZERO) < 0) ? negRed : posGreen);

        textWoodAmount.setText(wood + " ");
        textWoodRate.setText(" " + woodRate + "/s");
        textWoodRate.setTextColor((woodRate.compareTo(BigDecimal.ZERO) == 0) ? neutralGray : (woodRate.compareTo(BigDecimal.ZERO) < 0) ? negRed : posGreen);

        textStoneAmount.setText(stone + " ");
        textStoneRate.setText(" " + stoneRate + "/s");
        textStoneRate.setTextColor((stoneRate.compareTo(BigDecimal.ZERO) == 0) ? neutralGray : (stoneRate.compareTo(BigDecimal.ZERO) < 0) ? negRed : posGreen);

        textWorker.setText("Unemployed Workers: " + unemployed);
        textFarmer.setText("Farmers " + "(+" + farmerRateIncrease + "/s): " + farmer);
        textLumberjack.setText("Lumberjacks " + "(+" + lumberjackRateIncrease + "/s): " + lumberjack);
        textMiner.setText("Miners " + "(+" + minerRateIncrease + "/s): " + miner);
    }

    public void gatherFood(View v) {
        food = food.add(BigDecimal.ONE);
        updateText();
    }

    public void gatherWood(View v) {
        wood = wood.add(BigDecimal.ONE);
        updateText();
    }

    public void gatherStone(View v) {
        stone = stone.add(BigDecimal.ONE);
        updateText();
    }

    public void createPopulation(View v) {
        if (food.compareTo(BigDecimal.TEN) > 0) {
            food = food.subtract(BigDecimal.TEN);
            foodRate = foodRate.subtract(populationFoodConsumption);
            unemployed++;
            population++;
            updateText();
        } else {
            showToast("Not enough food!");
        }
    }

    public void createFarmer(View v) {
        createWorker(W_FARMER);
    }

    public void createLumberjack(View v) {
        createWorker(W_LUMBERJACK);
    }

    public void createMiner(View v) {
        createWorker(W_MINER);
    }

    public void destroyFarmer(View v) {
        destroyWorker(W_FARMER);
    }

    public void destroyLumberjack(View v) {
        destroyWorker(W_LUMBERJACK);
    }

    public void destroyMiner(View v) {
        destroyWorker(W_MINER);
    }

    public void createWorker(int worker) {
        if (unemployed >= 1) {
            unemployed--;
            if (worker == W_FARMER) {
                farmer++;
                foodRate = foodRate.add(farmerRateIncrease);
            }
            if (worker == W_LUMBERJACK) {
                lumberjack++;
                woodRate = woodRate.add(lumberjackRateIncrease);
            }
            if (worker == W_MINER) {
                miner++;
                stoneRate = stoneRate.add(minerRateIncrease);
            }
            updateText();
        } else {
            showToast("Not enough workers!");
        }
    }

    public void destroyWorker(int worker) {
        switch (worker) {
            case W_FARMER:
                if (farmer >= 1) {
                    farmer--;
                    unemployed++;
                    foodRate = foodRate.subtract(farmerRateIncrease);
                } else {
                    showToast("None left!");
                }
                break;
            case W_LUMBERJACK:
                if (lumberjack >= 1) {
                    lumberjack--;
                    unemployed++;
                    woodRate = woodRate.subtract(lumberjackRateIncrease);
                } else {
                    showToast("None left!");
                }
                break;
            case W_MINER:
                if (miner >= 1) {
                    miner--;
                    unemployed++;
                    stoneRate = stoneRate.subtract(minerRateIncrease);
                } else {
                    showToast("None left!");
                }
                break;
            default:
                showToast("Error: unrecognized worker type, int: " + worker);
                break;
        }
        updateText();
    }

    /**
     * Manages the toast used throughout the app
     *
     * @param text what the toast will display
     */
    public void showToast(CharSequence text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
