package sample;

import java.util.Random;

public class Sensor {
    private static float DEFAULT_TEMPERATURE = 32.7f;
    private static float DEFAULT_LIGHT = 288.0f;
    private static float DEFAULT_HUMIDITY = 55.7f;

    private static float latest_temperature = DEFAULT_TEMPERATURE;
    private static float latest_light = DEFAULT_LIGHT;
    private static float latest_humidity = DEFAULT_HUMIDITY;

    public static float getTemperature(){
        latest_temperature += randomValue();
        return latest_temperature;
    }

    public static float getLight(){
        latest_light += randomValue();
        return latest_light;
    }

    public static float getHumidity(){
        latest_humidity += randomValue();
        return latest_humidity;
    }

    private static float randomValue(){
        final float min = -3f;
        final float max = 3f;
        Random r = new Random();
        r.setSeed(System.currentTimeMillis() + r.nextInt());
        
        float random = min + r.nextFloat() * (max - min);
                
        return random;
    }
}