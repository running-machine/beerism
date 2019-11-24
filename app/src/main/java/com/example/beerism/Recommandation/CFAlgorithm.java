package com.example.beerism.Recommandation;

import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 참고 url : https://kutar37.tistory.com/39

public class CFAlgorithm {
    String TAG = "ObjectDetection";
    private Map<String, Map<String, Double>> critics;

    private static CFAlgorithm instance = new CFAlgorithm();

    public static CFAlgorithm getInstance() {
        return instance;
    }

    private CFAlgorithm() {
        critics = new HashMap<>();
    }

    public void inputData(String name, Map<String, Double>  movieInfo) {
        critics.put(name, movieInfo);
    }

    public void initData() {
        critics.clear();
    }

    public void inputData() {
        critics = new HashMap<>();

        Map<String, Double> items = new HashMap<>();
        items.put("택시운전사", 2.5);
        items.put("남한산성", 3.5);
        items.put("킹스맨:골든서클", 3.0);
        items.put("범죄도시", 3.5);
        items.put("아이 캔 스피크", 2.5);
        items.put("The Night Listener", 3.0);
        critics.put("차현석", items);

        items = new HashMap<>();
        items.put("택시운전사", 1.0);
        items.put("남한산성", 4.5);
        items.put("킹스맨:골든서클", 0.5);
        items.put("범죄도시", 1.5);
        items.put("아이 캔 스피크", 4.5);
        items.put("The Night Listener", 5.0);
        critics.put("황해도", items);

        items = new HashMap<>();
        items.put("택시운전사", 3.0);
        items.put("남한산성", 3.5);
        items.put("킹스맨:골든서클", 1.5);
        items.put("범죄도시", 5.0);
        items.put("아이 캔 스피크", 3.5);
        items.put("The Night Listener", 3.0);
        critics.put("김미희", items);

        items = new HashMap<>();
        items.put("택시운전사", 2.5);
        items.put("남한산성", 3.0);
        items.put("범죄도시", 3.5);
        items.put("The Night Listener", 4.0);
        critics.put("김준형", items);

        items = new HashMap<>();
        items.put("남한산성", 3.5);
        items.put("킹스맨:골든서클", 3.0);
        items.put("범죄도시", 4.0);
        items.put("아이 캔 스피크", 2.5);
        items.put("The Night Listener", 4.5);
        critics.put("이은비", items);

        items = new HashMap<>();
        items.put("택시운전사", 3.0);
        items.put("남한산성", 4.0);
        items.put("킹스맨:골든서클", 2.0);
        items.put("범죄도시", 3.0);
        items.put("아이 캔 스피크", 2.0);
        items.put("The Night Listener", 3.5);
        critics.put("임명진", items);

        items = new HashMap<>();
        items.put("택시운전사", 3.0);
        items.put("남한산성", 4.0);
        items.put("범죄도시", 5.0);
        items.put("아이 캔 스피크", 3.5);
        items.put("The Night Listener", 3.0);
        critics.put("심수정", items);

        items = new HashMap<>();
        items.put("남한산성", 4.5);
        items.put("범죄도시", 4.0);
        items.put("아이 캔 스피크", 1.0);
        critics.put("박병관", items);
    }

    public double calculatePearsonNum(String name1, String name2) {
        double sumX = 0, sumY = 0, sumPowX = 0, sumPowY = 0, sumXY = 0, count = 0;

        for (String key : critics.get(name1).keySet()) {
            if (critics.get(name2).containsKey(key)) {
                sumX += critics.get(name1).get(key);
                sumY += critics.get(name2).get(key);
                sumPowX += Math.pow(critics.get(name1).get(key), 2);
                sumPowY += Math.pow(critics.get(name2).get(key), 2);
                sumXY += critics.get(name1).get(key) * critics.get(name2).get(key);
                count++;
            }
        }

        return (sumXY - ((sumX * sumY) / count)) / Math.sqrt((sumPowX - (Math.pow(sumX, 2) / count)) * (sumPowY - (Math.pow(sumY, 2) / count)));
    }

    // 각 사람들에 대한 유사도
    public List<Map.Entry<String, Double>> topMatch(String name) {
        List<Map.Entry<String, Double>> answer = new ArrayList<>();

        for (String key : critics.keySet()) {
            if (!key.equals(name)) {
                answer.add(new AbstractMap.SimpleEntry(key, calculatePearsonNum(name, key)));
            }
        }

        answer.sort((o1, o2) -> {
            double score1 = o1.getValue(), score2 = o2.getValue();

            if (score1 == score2) return 0;
            else if (score1 > score2) return -1;
            else return 1;
        });

        return answer;
    }

    public List<Map.Entry<String, Double>> getRecommandation(String name) {
        List<Map.Entry<String, Double>> answer = topMatch(name);

        double simNum = 0, score = 0;
        List<Map.Entry<String, Double>> recommandationLst = new ArrayList<>();
        Map<String, Double> scoreMap = new HashMap<>();
        Map<String, Double> simMap = new HashMap<>();

        for (Map.Entry<String, Double> sim: answer) {
            if (sim.getValue() < 0) continue;
            for (String beerName : critics.get(sim.getKey()).keySet()) {
                if (!critics.get(name).containsKey(beerName)) {
                    score += sim.getValue() * critics.get(sim.getKey()).get(beerName);
                    if (!simMap.containsKey(beerName)) {
                        scoreMap.put(beerName, 0.0);
                    }
                    else {
                        scoreMap.put(beerName, scoreMap.get(beerName) + score);
                    }

                    if (!simMap.containsKey(beerName)) {
                        simMap.put(beerName, 0.0);
                    }
                    else {
                        simMap.put(beerName, simMap.get(beerName) + sim.getValue());
                    }
                }

                score = 0;
            }
        }

        for (String key : scoreMap.keySet()) {
            scoreMap.put(key, scoreMap.get(key) / simMap.get(key));
            recommandationLst.add(new AbstractMap.SimpleEntry(key, scoreMap.get(key)));
        }

        recommandationLst.sort((o1, o2) -> {
            double score1 = o1.getValue(), score2 = o2.getValue();

            if (score1 == score2) return 0;
            else if (score1 > score2) return -1;
            else return 1;
        });

        return recommandationLst;
    }

    public List<Map.Entry<String, Double>> getUserData(String name) {
        List<Map.Entry<String, Double>> data = new ArrayList<>();
        for (String key : critics.get(name).keySet()) {
            data.add(new AbstractMap.SimpleEntry<>(key, critics.get(name).get(key)));
        }

        return data;
    }

//    public void main() {
//        inputData();
//
//        List<Map.Entry<String, Double>> ranks = topMatch("박병관");
//
//        Log.d(TAG, "유사도");
//        for (Map.Entry<String, Double> item : ranks) {
////            Log.d(TAG, item.getKey() + "'s score = " + item.getValue());
//        }
//
//        Log.d(TAG, "예상 평점");
//        List<Map.Entry<String, Double>> scores = getRecommandation("박병관");
//        for (Map.Entry<String, Double> item : scores) {
//            Log.d(TAG, item.getKey() + "'s score = " + item.getValue());
//        }
//    }
}